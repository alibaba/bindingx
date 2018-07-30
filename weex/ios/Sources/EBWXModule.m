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

#import "EBWXModule.h"
#import <WeexSDK/WeexSDK.h>
#import "EBExpressionHandler.h"
#import <pthread/pthread.h>
#import <WeexPluginLoader/WeexPluginLoader.h>
#import "EBBindData.h"
#import "EBUtility+WX.h"
#import "EBWXUtils.h"

WX_PlUGIN_EXPORT_MODULE(bindingx, EBWXModule)

@interface EBWXModule ()

@property (nonatomic, strong) EBBindData *bindData;

@end

@implementation EBWXModule {
    pthread_mutex_t mutex;
    pthread_mutexattr_t mutexAttr;
}

@synthesize weexInstance;

WX_EXPORT_METHOD_SYNC(@selector(prepare:))
WX_EXPORT_METHOD_SYNC(@selector(bind:callback:tokenCallback:))
WX_EXPORT_METHOD_SYNC(@selector(bindAsync:callback:tokenCallback:))
WX_EXPORT_METHOD_SYNC(@selector(unbind:))
WX_EXPORT_METHOD_SYNC(@selector(unbindAll))
WX_EXPORT_METHOD_SYNC(@selector(supportFeatures))
WX_EXPORT_METHOD_SYNC(@selector(getComputedStyle:callback:))
WX_EXPORT_METHOD_SYNC(@selector(getComputedStyleAsync:callback:))

- (instancetype)init {
    if (self = [super init]) {
        pthread_mutexattr_init(&mutexAttr);
        pthread_mutexattr_settype(&mutexAttr, PTHREAD_MUTEX_RECURSIVE);
        pthread_mutex_init(&mutex, &mutexAttr);
        _bindData = [EBBindData new];
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
    
    if (![EBEventHandlerFactory containsEvent:eventType]) {
        WX_LOG(WXLogFlagWarning, @"prepare binding eventType error");
        return;
    }
    
    __weak typeof(self) welf = self;
    WXPerformBlockOnComponentThread(^{
        // find sourceRef & targetRef
        WXComponent *sourceComponent = [weexInstance componentForRef:anchor];
        if (!sourceComponent && [EBEventHandlerFactory eventRequireSource:eventType]) {
            WX_LOG(WXLogFlagWarning, @"prepare binding can't find component");
            return;
        }
        
        pthread_mutex_lock(&mutex);
        
        EBExpressionHandler *handler = [welf.bindData handlerForToken:anchor eventType:eventType];
        if (!handler) {
            // create handler for key
            handler = [EBEventHandlerFactory createHandlerWithEvent:eventType source:sourceComponent];
            [welf.bindData putHandler:handler forToken:anchor eventType:eventType];
        }
        
        pthread_mutex_unlock(&mutex);
    });
    
}

- (NSDictionary *)bind:(NSDictionary *)dictionary
              callback:(WXKeepAliveCallback)callback
         tokenCallback:(WXKeepAliveCallback)tokenCallback {
    
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
    
    if (![EBEventHandlerFactory containsEvent:eventType]) {
        WX_LOG(WXLogFlagWarning, @"bind params handler error");
        callback(@{@"state":@"error",@"msg":@"bind params handler error"}, NO);
        return nil;
    }
    
    if ([WXUtility isBlankString:token]){
        if ([EBEventHandlerFactory eventRequireSource:eventType]) {
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
        if (!sourceComponent && [EBEventHandlerFactory eventRequireSource:eventType]) {
            WX_LOG(WXLogFlagWarning, @"bind can't find source component");
            callback(@{@"state":@"error",@"msg":@"bind can't find source component"}, NO);
            return;
        }
        
        NSMapTable<id, NSDictionary *> *targetExpression = [NSMapTable weakToStrongObjectsMapTable];
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
                expDict[@"expression"] = [EBBindData parseExpression:expression];
                
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
        
        EBExpressionHandler *handler = [welf.bindData handlerForToken:token eventType:eventType];
        if (!handler) {
            // create handler for key
            handler = [EBEventHandlerFactory createHandlerWithEvent:eventType source:sourceComponent];
            [welf.bindData putHandler:handler forToken:token eventType:eventType];
        }
        
        [handler updateTargetExpression:targetExpression
                                options:options
                         exitExpression:[EBBindData parseExpression:exitExpression]
                               callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
                                   callback(result,keepAlive);
                               }];
        
        pthread_mutex_unlock(&mutex);
    });
    
    NSDictionary *ret = @{@"token":token};
    if (tokenCallback) {
        tokenCallback(ret, NO);
    }
    return ret;
}

- (void)bindAsync:(NSDictionary *)dictionary
         callback:(WXKeepAliveCallback)callback
    tokenCallback:(WXKeepAliveCallback)tokenCallback {
    [self bind:dictionary
             callback:callback
        tokenCallback:tokenCallback];
}

- (void)unbind:(NSDictionary *)dictionary {
    
    if (!dictionary) {
        WX_LOG(WXLogFlagWarning, @"unbind params error, need json input");
        return;
    }
    NSString* token = dictionary[@"token"];
    NSString* eventType = dictionary[@"eventType"];
    
    if ([WXUtility isBlankString:token] || [WXUtility isBlankString:eventType]) {
        WX_LOG(WXLogFlagWarning, @"unbind params error");
        return;
    }
    
    if (![EBEventHandlerFactory containsEvent:eventType]) {
        WX_LOG(WXLogFlagWarning, @"unbind params handler error");
        return;
    }
    
    pthread_mutex_lock(&mutex);
    
    EBExpressionHandler *handler = [self.bindData handlerForToken:token eventType:eventType];
    if (!handler) {
        WX_LOG(WXLogFlagWarning, @"unbind can't find handler handler");
        pthread_mutex_unlock(&mutex);
        return;
    }
    
    [handler removeExpressionBinding];
    [self.bindData removeHandler:handler forToken:token eventType:eventType];
    
    pthread_mutex_unlock(&mutex);
}

- (void)unbindAll {
    pthread_mutex_lock(&mutex);
    
    [self.bindData unbindAll];
    
    pthread_mutex_unlock(&mutex);
}

- (NSArray *)supportFeatures {
    return EBsupportFeatures;
}

- (NSDictionary *)getComputedStyle:(NSString *)sourceRef
                          callback:(WXKeepAliveCallback)callback {
    
    if (callback) {
        // call async method when callback is not nil
        [self getComputedStyleAsync:sourceRef callback:callback];
        return nil;
    }
    
    __block NSDictionary *styles = nil;
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    [self fetchComputedStyle:sourceRef callback:^(NSDictionary *callbackStyles) {
        styles = callbackStyles;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)));
    
    return styles;
}

- (void)getComputedStyleAsync:(NSString *)sourceRef
                callback:(WXKeepAliveCallback)callback {
    [self fetchComputedStyle:sourceRef callback:^(NSDictionary *styles) {
        if (callback) {
            callback(styles, NO);
        }
    }];
}

- (void)fetchComputedStyle:(NSString *)sourceRef
                callback:(void (^ _Nullable)(NSDictionary *))callback {
    if (![sourceRef isKindOfClass:NSString.class] || [WXUtility isBlankString:sourceRef]) {
        WX_LOG(WXLogFlagWarning, @"getComputedStyle params error");
        callback(nil);
    }
    
    __block NSMutableDictionary *styles = [NSMutableDictionary new];
    
    WXPerformBlockOnComponentThread(^{
        // find sourceRef & targetRef
        WXComponent *sourceComponent = [weexInstance componentForRef:sourceRef];
        if (!sourceComponent) {
            WX_LOG(WXLogFlagWarning, @"getComputedStyle can't find source component");
            callback(nil);
        }
        NSDictionary* mapping = [EBWXUtils cssPropertyMapping];
        for (NSString* key in mapping) {
            id value = sourceComponent.styles[key];
            if (value) {
                if ([value isKindOfClass:NSString.class]) {
                    NSString *string = (NSString *)value;
                    if ([string hasSuffix:@"px"]) {
                        NSString *number = [string substringToIndex:(string.length-2)];
                        [styles setValue:@([number floatValue]) forKey:mapping[key]];
                    } else {
                        [styles setValue:string forKey:mapping[key]];
                    }
                } else if([value isKindOfClass:NSNumber.class]) {
                    [styles setValue:value forKey:mapping[key]];
                }
            }
        }
        if (sourceComponent.styles[@"borderRadius"]) {
            [styles setValue:sourceComponent.styles[@"borderRadius"] forKey:@"border-top-left-radius"];
            [styles setValue:sourceComponent.styles[@"borderRadius"] forKey:@"border-top-right-radius"];
            [styles setValue:sourceComponent.styles[@"borderRadius"] forKey:@"border-bottom-left-radius"];
            [styles setValue:sourceComponent.styles[@"borderRadius"] forKey:@"border-bottom-right-radius"];
        }
        WXPerformBlockOnMainThread(^{
            CALayer *layer = sourceComponent.view.layer;
            styles[@"translateX"] = [EBUtility transformFactor:@"transform.translation.x" layer:layer];
            styles[@"translateY"] = [EBUtility transformFactor:@"transform.translation.y" layer:layer];
            styles[@"scaleX"] = [layer valueForKeyPath:@"transform.scale.x"];
            styles[@"scaleY"] = [layer valueForKeyPath:@"transform.scale.y"];
            styles[@"rotateX"] = [layer valueForKeyPath:@"transform.rotation.x"];
            styles[@"rotateY"] = [layer valueForKeyPath:@"transform.rotation.y"];
            styles[@"rotateZ"] = [layer valueForKeyPath:@"transform.rotation.z"];
            styles[@"opacity"] = [layer valueForKeyPath:@"opacity"];
            
            callback(styles);
        });
    });
}

@end

WX_PlUGIN_EXPORT_MODULE(binding, EBWXBindingModule)

@interface EBWXBindingModule : EBWXModule

@end

@implementation EBWXBindingModule

@end
