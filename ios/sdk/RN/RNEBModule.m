/**
 * Copyright 2017 Alibaba Group
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

#import "RNEBModule.h"
#import "EBExpressionHandler.h"
#import <pthread/pthread.h>
#import "EBUtility+RN.h"
#import <React/RCTUIManager.h>
#import <React/RCTUIManagerUtils.h>

#define BINDING_EVENT_NAME @"BindingX"

@interface RNEBModule ()

@property (nonatomic, strong) NSMutableDictionary<NSString *, NSMutableDictionary<NSNumber *, EBExpressionHandler *> *> *sourceMap;

@end

@implementation RNEBModule {
    pthread_mutex_t mutex;
}

RCT_EXPORT_MODULE(bindingX)

- (dispatch_queue_t)methodQueue
{
    return RCTGetUIManagerQueue();
}

- (instancetype)init {
    if (self = [super init]) {
        pthread_mutex_init(&mutex, NULL);
    }
    return self;
}

- (void)dealloc {
    [self unbindAll];
    pthread_mutex_destroy(&mutex);
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
        RCTLog(@"prepare binding eventType error");
        return;
    }
    
    __weak typeof(self) welf = self;
    RCTExecuteOnUIManagerQueue(^{
        // find sourceRef & targetRef
        UIView* sourceComponent = [EBUtility getViewByRef:anchor];
        if (!sourceComponent && (exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            RCTLog(@"prepare binding can't find component");
            return;
        }
        
        pthread_mutex_lock(&mutex);
        
        EBExpressionHandler *handler = [welf handlerForToken:anchor expressionType:exprType];
        if (!handler) {
            // create handler for key
            handler = [EBExpressionHandler handlerWithExpressionType:exprType source:sourceComponent];
            [welf putHandler:handler forToken:anchor expressionType:exprType];
        }
        
        pthread_mutex_unlock(&mutex);
    });
    
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *,bind:(NSDictionary *)dictionary)
{
    [EBUtility setUIManager:self.bridge.uiManager];
    if (!dictionary) {
        RCTLog(@"bind params error, need json input");
        return nil;
    }
    
    NSString *eventType =  dictionary[@"eventType"];
    NSArray *props = dictionary[@"props"];
    NSString *token = dictionary[@"anchor"];
    NSString *exitExpression = dictionary[@"exitExpression"];
    NSDictionary *options = dictionary[@"options"];
    
    if ([EBUtility isBlankString:eventType] || !props || props.count == 0) {
        RCTLog(@"bind params error");
        //        callback(@[[NSNull null],@{@"state":@"error",@"msg":@"bind params error"}]);
        return nil;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        RCTLog( @"bind params handler error");
        //        callback(@[@{@"state":@"error",@"msg":@"bind params handler error"}]);
        return nil;
    }
    
    if ([token isKindOfClass:NSNumber.class]) {
        token = [(NSNumber *)token stringValue];
    }
    if ([EBUtility isBlankString:token]){
        if ((exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            RCTLog(@"bind params handler error");
            //            callback(@[[NSNull null],@{@"state":@"error",@"msg":@"anchor cannot be blank when type is pan or scroll"}]);
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
            NSString *expression = targetDic[@"expression"];
            
            if (targetRef) {
                
                NSMutableDictionary *propertyDic = [[targetExpression  objectForKey:targetRef] mutableCopy];
                if (!propertyDic) {
                    propertyDic = [NSMutableDictionary dictionary];
                }
                NSMutableDictionary *expDict = [NSMutableDictionary dictionary];
                expDict[@"expression"] = expression;
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
        
        EBExpressionHandler *handler = [welf handlerForToken:token expressionType:exprType];
        if (!handler) {
            // create handler for key
            handler = [EBExpressionHandler handlerWithExpressionType:exprType source:token];
            [welf putHandler:handler forToken:token expressionType:exprType];
        }
        
        [handler updateTargetExpression:targetExpression
                                options:options
                         exitExpression:exitExpression
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
        NSLog(@"unbind params error, need json input");
        return;
    }
    NSString* token = options[@"token"];
    NSString* eventType = options[@"eventType"];
    
    if ([EBUtility isBlankString:token] || [EBUtility isBlankString:eventType]) {
        NSLog(@"disableBinding params error");
        return;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        RCTLog(@"disableBinding params handler error");
        return;
    }
    
    pthread_mutex_lock(&mutex);
    
    EBExpressionHandler *handler = [self handlerForToken:token expressionType:exprType];
    if (!handler) {
        NSLog(@"disableBinding can't find handler handler");
        pthread_mutex_unlock(&mutex);
        return;
    }
    
    [handler removeExpressionBinding];
    [self removeHandler:handler forToken:token expressionType:exprType];
    
    pthread_mutex_unlock(&mutex);
}

RCT_EXPORT_METHOD(unbindAll)
{
    pthread_mutex_lock(&mutex);
    
    for (NSString *sourceRef in self.sourceMap) {
        NSMutableDictionary *handlerMap = self.sourceMap[sourceRef];
        for (NSNumber *expressionType in handlerMap) {
            EBExpressionHandler *handler = handlerMap[expressionType];
            [handler removeExpressionBinding];
        }
        [handlerMap removeAllObjects];
    }
    [self.sourceMap removeAllObjects];
    
    pthread_mutex_unlock(&mutex);
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSArray *, supportFeatures)
{
    return @[@"pan",@"scroll",@"orientation",@"timing"];
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *, getComputedStyle:(NSString *)sourceRef)
{
    if ([EBUtility isBlankString:sourceRef]) {
        NSLog(@"getComputedStyle params error");
        return nil;
    }
    
    __block NSMutableDictionary *styles = [NSMutableDictionary new];
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    // find sourceRef & targetRef
    UIView* view = [EBUtility getViewByRef:sourceRef];
    if (!view) {
        RCTLog(@"getComputedStyle can't find source component");
        return nil;
    }
    RCTExecuteOnMainQueue(^{
        CALayer *layer = view.layer;
        styles[@"translateX"] = [self transformFactor:@"transform.translation.x" layer:layer];
        styles[@"translateY"] = [self transformFactor:@"transform.translation.y" layer:layer];
        styles[@"scaleX"] = [self transformFactor:@"transform.scale.x" layer:layer];
        styles[@"scaleY"] = [self transformFactor:@"transform.scale.y" layer:layer];
        styles[@"rotateX"] = [self transformFactor:@"transform.rotation.x" layer:layer];
        styles[@"rotateY"] = [self transformFactor:@"transform.rotation.y" layer:layer];
        styles[@"rotateZ"] = [self transformFactor:@"transform.rotation.z" layer:layer];
        styles[@"opacity"] = [layer valueForKeyPath:@"opacity"];
        
        styles[@"background-color"] = [self colorAsString:layer.backgroundColor];
        
        //            if ([sourceComponent isKindOfClass:NSClassFromString(@"WXTextComponent")]) {
        //                Ivar ivar = class_getInstanceVariable(NSClassFromString(@"WXTextComponent"), "_color");
        //                UIColor *color = (UIColor *)object_getIvar(sourceComponent, ivar);
        //                if (color) {
        //                    styles[@"color"] = [self colorAsString:color.CGColor];
        //                }
        //            }
        
        dispatch_semaphore_signal(semaphore);
    });
    
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return styles;
}

- (NSNumber *)transformFactor:(NSString *)key layer:(CALayer* )layer {
    CGFloat factor = [EBUtility factor];
    id value = [layer valueForKeyPath:key];
    if(value){
        return [NSNumber numberWithDouble:([value doubleValue] / factor)];
    }
    return nil;
}

- (NSString *)colorAsString:(CGColorRef)cgColor
{
    const CGFloat *components = CGColorGetComponents(cgColor);
    return [NSString stringWithFormat:@"rgba(%d,%d,%d,%f)", (int)(components[0]*255), (int)(components[1]*255), (int)(components[2]*255), components[3]];
}


#pragma mark - Handler Map
- (NSMutableDictionary<NSString *, NSMutableDictionary<NSNumber *, EBExpressionHandler *> *> *)sourceMap {
    if (!_sourceMap) {
        _sourceMap = [NSMutableDictionary<NSString *, NSMutableDictionary<NSNumber *, EBExpressionHandler *> *> dictionary];
    }
    return _sourceMap;
}

- (NSMutableDictionary<NSNumber *, EBExpressionHandler *> *)handlerMapForToken:(NSString *)token {
    return [self.sourceMap objectForKey:token];
}

- (EBExpressionHandler *)handlerForToken:(NSString *)token expressionType:(WXExpressionType)exprType {
    return [[self handlerMapForToken:token] objectForKey:[NSNumber numberWithInteger:exprType]];
}

- (void)putHandler:(EBExpressionHandler *)handler forToken:(NSString *)token expressionType:(WXExpressionType)exprType {
    NSMutableDictionary<NSNumber *, EBExpressionHandler *> *handlerMap = [self handlerMapForToken:token];
    if (!handlerMap) {
        handlerMap = [NSMutableDictionary<NSNumber *, EBExpressionHandler *> dictionary];
        self.sourceMap[token] = handlerMap;
    }
    handlerMap[[NSNumber numberWithInteger:exprType]] = handler;
}

- (void)removeHandler:(EBExpressionHandler *)handler forToken:(NSString *)token expressionType:(WXExpressionType)exprType {
    NSMutableDictionary<NSNumber *, EBExpressionHandler *> *handlerMap = [self handlerMapForToken:token];
    if (handlerMap) {
        [handlerMap removeObjectForKey:[NSNumber numberWithInteger:exprType]];
    }
}

@end
