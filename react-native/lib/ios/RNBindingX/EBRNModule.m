/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "EBRNModule.h"
#import "EBExpressionHandler.h"
#import <pthread/pthread.h>
#import "EBUtility+RN.h"
#import <React/RCTUIManager.h>
#import <React/RCTUIManagerUtils.h>
#import <React/RCTText.h>
#import <React/RCTShadowText.h>
#import "EBBindData.h"

#define BINDING_EVENT_NAME @"bindingx:statechange"

@interface EBRNModule ()

@property (nonatomic, strong) EBBindData *bindData;

@end

@implementation EBRNModule {
    pthread_mutex_t mutex;
    pthread_mutexattr_t mutexAttr;
}

RCT_EXPORT_MODULE(bindingx)

- (dispatch_queue_t)methodQueue
{
    return RCTGetUIManagerQueue();
}

- (instancetype)init
{
    if (self = [super init]) {
        pthread_mutexattr_init(&mutexAttr);
        pthread_mutexattr_settype(&mutexAttr, PTHREAD_MUTEX_RECURSIVE);
        pthread_mutex_init(&mutex, &mutexAttr);
        _bindData = [EBBindData new];
    }
    return self;
}

- (void)dealloc
{
    [self unbindAll];
    pthread_mutex_destroy(&mutex);
    pthread_mutexattr_destroy(&mutexAttr);
}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[BINDING_EVENT_NAME];
}

RCT_EXPORT_METHOD(prepare:(NSDictionary *)dictionary)
{
    [EBUtility setUIManager:self.bridge.uiManager];
    NSString *anchor = dictionary[@"anchor"];
    NSString *eventType = dictionary[@"eventType"];
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        RCTLogWarn(@"prepare binding eventType error");
        return;
    }
    
    __weak typeof(self) welf = self;
    RCTExecuteOnUIManagerQueue(^{
        // find sourceRef & targetRef
        UIView* sourceComponent = [EBUtility getViewByRef:anchor];
        if (!sourceComponent && (exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            RCTLogWarn(@"prepare binding can't find component");
            return;
        }
        
        pthread_mutex_lock(&mutex);
        
        EBExpressionHandler *handler = [welf.bindData handlerForToken:anchor expressionType:exprType];
        if (!handler) {
            // create handler for key
            handler = [EBExpressionHandler handlerWithExpressionType:exprType source:sourceComponent];
            [welf.bindData putHandler:handler forToken:anchor expressionType:exprType];
        }
        
        pthread_mutex_unlock(&mutex);
    });
    
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *,bind:(NSDictionary *)dictionary)
{
    [EBUtility setUIManager:self.bridge.uiManager];
    if (!dictionary) {
        RCTLogWarn(@"bind params error, need json input");
        return nil;
    }
    
    NSString *eventType =  dictionary[@"eventType"];
    NSArray *props = dictionary[@"props"];
    NSString *token = dictionary[@"anchor"];
    NSDictionary *exitExpression = dictionary[@"exitExpression"];
    NSDictionary *options = dictionary[@"options"];
    
    if ([EBUtility isBlankString:eventType] || !props || props.count == 0) {
        RCTLogWarn(@"bind params error");
        [self sendEventWithName:BINDING_EVENT_NAME body:@{@"state":@"error",@"msg":@"bind params error"}];
        return nil;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        RCTLogWarn( @"bind params handler error");
        [self sendEventWithName:BINDING_EVENT_NAME body:@{@"state":@"error",@"msg":@"bind params handler error"}];
        return nil;
    }
    
    if ([token isKindOfClass:NSNumber.class]) {
        token = [(NSNumber *)token stringValue];
    }
    
    if ([EBUtility isBlankString:token]){
        if ((exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            RCTLogWarn(@"bind params handler error");
            [self sendEventWithName:BINDING_EVENT_NAME body:@{@"state":@"error",@"msg":@"anchor cannot be blank when type is pan or scroll"}];
            return nil;
        } else {
            token = [[NSUUID UUID] UUIDString];
        }
    }
    
    __weak typeof(self) welf = self;
    RCTExecuteOnUIManagerQueue(^{
        
        NSMapTable<id, NSDictionary *> *targetExpression = [NSMapTable new];
        for (NSDictionary *targetDic in props) {
            NSString *targetRef = targetDic[@"element"];
            NSString *property = targetDic[@"property"];
            NSDictionary *expression = targetDic[@"expression"];
            
            if (targetRef) {
                
                NSMutableDictionary *propertyDic = [[targetExpression  objectForKey:targetRef] mutableCopy];
                if (!propertyDic) {
                    propertyDic = [NSMutableDictionary dictionary];
                }
                NSMutableDictionary *expDict = [NSMutableDictionary dictionary];
                expDict[@"expression"] = [EBBindData parseExpression:expression];
                if( targetDic[@"config"] )
                {
                    expDict[@"config"] = targetDic[@"config"];
                }
                propertyDic[property] = expDict;
                [targetExpression setObject:propertyDic forKey:targetRef];
            }
        }
        
        // find handler for key
        pthread_mutex_lock(&mutex);
        
        EBExpressionHandler *handler = [welf.bindData handlerForToken:token expressionType:exprType];
        if (!handler) {
            // create handler for key
            handler = [EBExpressionHandler handlerWithExpressionType:exprType source:token];
            [welf.bindData putHandler:handler forToken:token expressionType:exprType];
        }
        
        [handler updateTargetExpression:targetExpression
                                options:options
                         exitExpression:[EBBindData parseExpression:exitExpression]
                               callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
                                   id body = nil;
                                   if ([result isKindOfClass:NSDictionary.class]) {
                                       body = [result mutableCopy];
                                       [body setObject:source forKey:@"token"];
                                   } else {
                                       body = result;
                                   }
                                   [welf sendEventWithName:BINDING_EVENT_NAME body:body];
                                   if (keepAlive) {
                                       [welf stopObserving];
                                   }
                               }];
        pthread_mutex_unlock(&mutex);
    });
    return @{@"token":token};
}

RCT_EXPORT_METHOD(unbind:(NSDictionary *)options)
{
    if (!options) {
        RCTLogWarn(@"unbind params error, need json input");
        return;
    }
    NSString* token = options[@"token"];
    NSString* eventType = options[@"eventType"];
    
    if ([EBUtility isBlankString:token] || [EBUtility isBlankString:eventType]) {
        RCTLogWarn(@"unbind params error");
        return;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        RCTLogWarn(@"unbind params handler error");
        return;
    }
    
    pthread_mutex_lock(&mutex);
    
    EBExpressionHandler *handler = [self.bindData handlerForToken:token expressionType:exprType];
    if (handler) {
        [handler removeExpressionBinding];
        [self.bindData removeHandler:handler forToken:token expressionType:exprType];
    } else {
        RCTLogWarn(@"unbind can't find handler handler");
    }
    
    pthread_mutex_unlock(&mutex);
}

RCT_EXPORT_METHOD(unbindAll)
{
    pthread_mutex_lock(&mutex);
    
    [self.bindData unbindAll];
    
    pthread_mutex_unlock(&mutex);
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSArray *, supportFeatures)
{
    return @[@"pan",@"scroll",@"orientation",@"timing"];
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *, getComputedStyle:(NSString *)sourceRef)
{
    NSString *ref = sourceRef;
    if ([ref isKindOfClass:NSNumber.class]) {
        ref = [(NSNumber *)sourceRef stringValue];
    }
    if ([EBUtility isBlankString:ref]) {
        RCTLogWarn(@"getComputedStyle params error");
        return nil;
    }
    
    __block NSMutableDictionary *styles = [NSMutableDictionary new];
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    RCTExecuteOnMainQueue(^{
        UIView* view = [EBUtility getViewByRef:ref];
        if (!view) {
            RCTLogWarn(@"source Ref not exist");
        } else {
            CALayer *layer = view.layer;
            styles[@"translateX"] = [layer valueForKeyPath:@"transform.translation.x"];
            styles[@"translateY"] = [layer valueForKeyPath:@"transform.translation.y"];
            styles[@"scaleX"] = [layer valueForKeyPath:@"transform.scale.x"];
            styles[@"scaleY"] = [layer valueForKeyPath:@"transform.scale.y"];
            styles[@"rotateX"] = [layer valueForKeyPath:@"transform.rotation.x"];
            styles[@"rotateY"] = [layer valueForKeyPath:@"transform.rotation.y"];
            styles[@"rotateZ"] = [layer valueForKeyPath:@"transform.rotation.z"];
            styles[@"opacity"] = [layer valueForKeyPath:@"opacity"];
            
            styles[@"background-color"] = [self colorAsString:view.backgroundColor.CGColor];
        }
        dispatch_semaphore_signal(semaphore);
    });
    
    //color for RCTText
    RCTExecuteOnUIManagerQueue(^{
        RCTShadowView* shadowView = [self.bridge.uiManager shadowViewForReactTag:@([sourceRef integerValue])];
        if ([shadowView isKindOfClass:RCTShadowText.class]) {
            RCTShadowText *shadowText = (RCTShadowText *)shadowView;
            styles[@"color"] = [self colorAsString:shadowText.color.CGColor];
        }
        dispatch_semaphore_signal(semaphore);
    });
    
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    
    return styles;
}

- (NSString *)colorAsString:(CGColorRef)cgColor
{
    const CGFloat *components = CGColorGetComponents(cgColor);
    if (components) {
        return [NSString stringWithFormat:@"rgba(%d,%d,%d,%f)", (int)(components[0]*255), (int)(components[1]*255), (int)(components[2]*255), components[3]];
    }
    return nil;
}

@end
