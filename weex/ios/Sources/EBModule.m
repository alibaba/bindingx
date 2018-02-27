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

#import "EBModule.h"
#import <WeexSDK/WeexSDK.h>
#import "EBExpressionHandler.h"
#import <pthread/pthread.h>
#import <WeexPluginLoader/WeexPluginLoader.h>

WX_PlUGIN_EXPORT_MODULE(bindingx, EBModule)

@interface EBModule ()

@property (nonatomic, strong) NSMutableDictionary<NSString *, NSMutableDictionary<NSNumber *, EBExpressionHandler *> *> *sourceMap;

@end

@implementation EBModule {
    pthread_mutex_t mutex;
    pthread_mutexattr_t mutexAttr;
}

@synthesize weexInstance;

WX_EXPORT_METHOD(@selector(prepare:))
WX_EXPORT_METHOD_SYNC(@selector(bind:callback:))
WX_EXPORT_METHOD(@selector(unbind:))
WX_EXPORT_METHOD(@selector(unbindAll))
WX_EXPORT_METHOD_SYNC(@selector(supportFeatures))
WX_EXPORT_METHOD_SYNC(@selector(getComputedStyle:))

- (instancetype)init {
    if (self = [super init]) {
        pthread_mutexattr_init(&mutexAttr);
        pthread_mutexattr_settype(&mutexAttr, PTHREAD_MUTEX_RECURSIVE);
        pthread_mutex_init(&mutex, &mutexAttr);
    }
    return self;
}

- (void)dealloc {
    [self unbindAll];
    pthread_mutex_destroy(&mutex);
    pthread_mutexattr_destroy(&mutexAttr);
}

- (void)prepare:(NSDictionary *)dictionary {
    if (!dictionary) {
        WX_LOG(WXLogFlagWarning, @"prepare params error, need json input");
        return;
    }
    
    NSString *anchor = dictionary[@"anchor"];
    NSString *eventType = dictionary[@"eventType"];
    
    if ([WXUtility isBlankString:anchor] || [WXUtility isBlankString:eventType]) {
        WX_LOG(WXLogFlagWarning, @"prepare binding params error");
        return;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        WX_LOG(WXLogFlagWarning, @"prepare binding eventType error");
        return;
    }
    
    __weak typeof(self) welf = self;
    WXPerformBlockOnComponentThread(^{
        // find sourceRef & targetRef
        WXComponent *sourceComponent = [weexInstance componentForRef:anchor];
        if (!sourceComponent && (exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            WX_LOG(WXLogFlagWarning, @"prepare binding can't find component");
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

- (NSDictionary *)bind:(NSDictionary *)dictionary
              callback:(WXKeepAliveCallback)callback {
    
    if (!dictionary) {
        WX_LOG(WXLogFlagWarning, @"bind params error, need json input");
        return nil;
    }
    
    NSString *eventType =  dictionary[@"eventType"];
    NSArray *props = dictionary[@"props"];
    NSString *token = dictionary[@"anchor"];
    NSDictionary *exitExpression = dictionary[@"exitExpression"];
    NSDictionary *options = dictionary[@"options"];
    
    if ([WXUtility isBlankString:eventType] || !props || props.count == 0) {
        WX_LOG(WXLogFlagWarning, @"bind params error");
        callback(@{@"state":@"error",@"msg":@"bind params error"}, NO);
        return nil;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        WX_LOG(WXLogFlagWarning, @"bind params handler error");
        callback(@{@"state":@"error",@"msg":@"bind params handler error"}, NO);
        return nil;
    }
    
    if ([WXUtility isBlankString:token]){
        if ((exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            WX_LOG(WXLogFlagWarning, @"bind params handler error");
            callback(@{@"state":@"error",@"msg":@"anchor cannot be blank when type is pan or scroll"}, NO);
            return nil;
        } else {
            token = [[NSUUID UUID] UUIDString];
        }
    }
    
    __weak typeof(self) welf = self;
    WXPerformBlockOnComponentThread(^{
        
        // find sourceRef & targetRef
        WXComponent *sourceComponent = nil;
        NSString *instanceId = dictionary[@"instanceId"];
        if (instanceId) {
            WXSDKInstance *instance = [WXSDKManager instanceForID:instanceId];
            sourceComponent = [instance componentForRef:token];
        } else {
            sourceComponent = [weexInstance componentForRef:token];
        }
        if (!sourceComponent && (exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            WX_LOG(WXLogFlagWarning, @"bind can't find source component");
            callback(@{@"state":@"error",@"msg":@"bind can't find source component"}, NO);
            return;
        }
        
        NSMapTable<id, NSDictionary *> *targetExpression = [NSMapTable new];
        for (NSDictionary *targetDic in props) {
            NSString *targetRef = targetDic[@"element"];
            NSString *property = targetDic[@"property"];
            NSDictionary *expression = targetDic[@"expression"];
            NSString *instanceId = targetDic[@"instanceId"];
            
            WXComponent *targetComponent = nil;
            if (instanceId) {
                WXSDKInstance *instance = [WXSDKManager instanceForID:instanceId];
                targetComponent = [instance componentForRef:targetRef];
            } else {
                targetComponent = [weexInstance componentForRef:targetRef];
            }
            if (targetComponent) {
                
                if ([targetComponent isViewLoaded]) {
                    WXPerformBlockOnMainThread(^{
                        [targetComponent.view.layer removeAllAnimations];
                    });
                }
                
                NSMutableDictionary *propertyDic = [[targetExpression objectForKey:targetComponent] mutableCopy];
                if (!propertyDic) {
                    propertyDic = [NSMutableDictionary dictionary];
                }
                NSMutableDictionary *expDict = [NSMutableDictionary dictionary];
                expDict[@"expression"] = [self parseExpression:expression];
                
                if( targetDic[@"config"] )
                {
                    expDict[@"config"] = targetDic[@"config"];
                }
                propertyDic[property] = expDict;
                [targetExpression setObject:propertyDic forKey:targetComponent];
            }
        }
        
        // find handler for key
        pthread_mutex_lock(&mutex);
        
        EBExpressionHandler *handler = [welf handlerForToken:token expressionType:exprType];
        if (!handler) {
            // create handler for key
            handler = [EBExpressionHandler handlerWithExpressionType:exprType source:sourceComponent];
            [welf putHandler:handler forToken:token expressionType:exprType];
        }
        
        [handler updateTargetExpression:targetExpression
                                options:options
                         exitExpression:[self parseExpression:exitExpression]
                               callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
                                   callback(result,keepAlive);
                               }];
        
        pthread_mutex_unlock(&mutex);
    });
    return @{@"token":token};
}

- (void)unbind:(NSDictionary *)dictionary {
    
    if (!dictionary) {
        WX_LOG(WXLogFlagWarning, @"unbind params error, need json input");
        return;
    }
    NSString* token = dictionary[@"token"];
    NSString* eventType = dictionary[@"eventType"];
    
    if ([WXUtility isBlankString:token] || [WXUtility isBlankString:eventType]) {
        WX_LOG(WXLogFlagWarning, @"disableBinding params error");
        return;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        WX_LOG(WXLogFlagWarning, @"disableBinding params handler error");
        return;
    }
    
    pthread_mutex_lock(&mutex);
    
    EBExpressionHandler *handler = [self handlerForToken:token expressionType:exprType];
    if (!handler) {
        WX_LOG(WXLogFlagWarning, @"disableBinding can't find handler handler");
        pthread_mutex_unlock(&mutex);
        return;
    }
    
    [handler removeExpressionBinding];
    [self removeHandler:handler forToken:token expressionType:exprType];
    
    pthread_mutex_unlock(&mutex);
}

- (void)unbindAll {
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

- (NSArray *)supportFeatures {
    return @[@"pan",@"scroll",@"orientation",@"timing"];
}

- (NSDictionary *)getComputedStyle:(NSString *)sourceRef {
    if ([WXUtility isBlankString:sourceRef]) {
        WX_LOG(WXLogFlagWarning, @"getComputedStyle params error");
        return nil;
    }
    
    __block NSMutableDictionary *styles = [NSMutableDictionary new];
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    WXPerformBlockOnComponentThread(^{
        // find sourceRef & targetRef
        WXComponent *sourceComponent = [weexInstance componentForRef:sourceRef];
        if (!sourceComponent) {
            WX_LOG(WXLogFlagWarning, @"getComputedStyle can't find source component");
            dispatch_semaphore_signal(semaphore);
            return;
        }
        WXPerformBlockSyncOnMainThread(^{
            CALayer *layer = sourceComponent.view.layer;
            styles[@"translateX"] = [self transformFactor:@"transform.translation.x" layer:layer];
            styles[@"translateY"] = [self transformFactor:@"transform.translation.y" layer:layer];
            styles[@"scaleX"] = [self transformFactor:@"transform.scale.x" layer:layer];
            styles[@"scaleY"] = [self transformFactor:@"transform.scale.y" layer:layer];
            styles[@"rotateX"] = [self transformFactor:@"transform.rotation.x" layer:layer];
            styles[@"rotateY"] = [self transformFactor:@"transform.rotation.y" layer:layer];
            styles[@"rotateZ"] = [self transformFactor:@"transform.rotation.z" layer:layer];
            styles[@"opacity"] = [layer valueForKeyPath:@"opacity"];
            
            styles[@"background-color"] = [self colorAsString:layer.backgroundColor];;
            if ([sourceComponent isKindOfClass:NSClassFromString(@"WXTextComponent")]) {
                Ivar ivar = class_getInstanceVariable(NSClassFromString(@"WXTextComponent"), "_color");
                UIColor *color = (UIColor *)object_getIvar(sourceComponent, ivar);
                if (color) {
                    styles[@"color"] = [self colorAsString:color.CGColor];
                }
            }
            
            dispatch_semaphore_signal(semaphore);
        });
    });
    
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return styles;
}

- (NSNumber *)transformFactor:(NSString *)key layer:(CALayer* )layer {
    CGFloat factor = [WXUtility defaultPixelScaleFactor];
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

- (id)parseExpression:(NSDictionary *)expression
{
    if ([expression isKindOfClass:NSDictionary.class]) {
        NSString* transformedExpressionStr = expression[@"transformed"];
        if (transformedExpressionStr && [transformedExpressionStr isKindOfClass:NSString.class]) {
            return [NSJSONSerialization JSONObjectWithData:[transformedExpressionStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
        }
        else {
            return expression[@"origin"];
        }
    } else if ([expression isKindOfClass:NSString.class]) {
        NSString* expressionStr = (NSString *)expression;
        return [NSJSONSerialization JSONObjectWithData:[expressionStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
    }
    return nil;
}


@end
