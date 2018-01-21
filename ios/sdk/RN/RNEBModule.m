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

@interface RNEBModule ()

@property (nonatomic, strong) NSMutableDictionary<NSString *, NSMutableDictionary<NSNumber *, EBExpressionHandler *> *> *sourceMap;

@end

@implementation RNEBModule {
    pthread_mutex_t mutex;
}

RCT_EXPORT_MODULE(Binding)

@synthesize bridge = _bridge;

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

RCT_EXPORT_METHOD(prepare:(NSDictionary *)dictionary)
{
    [EBUtility setUIManager:_bridge.uiManager];
    NSString *anchor = dictionary[@"anchor"];
    NSString *eventType = dictionary[@"eventType"];
    
//    NSNumber* tag = @([anchor integerValue]);
//    [EBUtility execute:@{
//                         @"backgroundColor" : @(0xFF0000FF),
//                         @"top" : @(200),
//                         @"width" : @(300),
////                         @"transform" : @[@{
////                                 @"translateY" : @(100),
////                                 }],
//                         } to:tag];
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        NSLog(@"prepare binding eventType error");
        return;
    }

    __weak typeof(self) welf = self;
    RCTExecuteOnUIManagerQueue(^{
        // find sourceRef & targetRef
        UIView* sourceComponent = [welf.bridge.uiManager viewForReactTag:@([anchor integerValue])];
        if (!sourceComponent && (exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            NSLog(@"prepare binding can't find component");
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

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *, bind:(NSDictionary *)dictionary
                  callback:(RCTResponseSenderBlock)callback)
{
    [EBUtility setUIManager:_bridge.uiManager];
    if (!dictionary) {
        NSLog(@"bind params error, need json input");
        return nil;
    }
    
    NSString *eventType =  dictionary[@"eventType"];
    NSArray *targetExpression = dictionary[@"props"];
    NSString *token = dictionary[@"anchor"];
    NSString *exitExpression = dictionary[@"exitExpression"];
    NSDictionary *options = dictionary[@"options"];
    
    if ([EBUtility isBlankString:eventType] || !targetExpression || targetExpression.count == 0) {
        NSLog(@"bind params error");
        callback(@[[NSNull null],@{@"state":@"error",@"msg":@"bind params error"}]);
        return nil;
    }
    
    WXExpressionType exprType = [EBExpressionHandler stringToExprType:eventType];
    if (exprType == WXExpressionTypeUndefined) {
        NSLog( @"bind params handler error");
        callback(@[@{@"state":@"error",@"msg":@"bind params handler error"}]);
        return nil;
    }
    
    if (token){
        if ((exprType == WXExpressionTypePan || exprType == WXExpressionTypeScroll)) {
            NSLog(@"bind params handler error");
            callback(@[[NSNull null],@{@"state":@"error",@"msg":@"anchor cannot be blank when type is pan or scroll"}]);
            return nil;
        } else {
            token = [[NSUUID UUID] UUIDString];
        }
    }
    
    __weak typeof(self) welf = self;
    RCTExecuteOnUIManagerQueue(^{

        NSMapTable<NSString *, id> *weakMap = [NSMapTable strongToWeakObjectsMapTable];
        NSMutableDictionary<NSString *, NSDictionary *> *expressionDic = [NSMutableDictionary dictionary];
        for (NSDictionary *targetDic in targetExpression) {
            NSString *targetRef = targetDic[@"element"];
            NSString *property = targetDic[@"property"];
            NSString *expression = targetDic[@"expression"];
            NSString *instanceId = targetDic[@"instanceId"];
            
//            WXComponent *targetComponent = nil;
//            if (instanceId) {
//                WXSDKInstance *instance = [WXSDKManager instanceForID:instanceId];
//                targetComponent = [instance componentForRef:targetRef];
//            } else {
//                targetComponent = [weexInstance componentForRef:targetRef];
//            }
            if (targetRef) {

                // TOD 移除 animations
//                if ([targetComponent isViewLoaded]) {
//                    WXPerformBlockOnMainThread(^{
//                        [targetComponent.view.layer removeAllAnimations];
//                    });
//                }

                [weakMap setObject:targetRef forKey:targetRef];
                NSMutableDictionary *propertyDic = [expressionDic[targetRef] mutableCopy];
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
                expressionDic[targetRef] = propertyDic;
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

        [handler updateTargets:weakMap
                    expression:expressionDic
                       options:options
                exitExpression:exitExpression
                      callback:^(id  _Nonnull result, BOOL keepAlive) {
                          // TODO 改为keepAlive
                          callback(@[]);
                      }];

        pthread_mutex_unlock(&mutex);
    });
    return  [NSDictionary dictionaryWithObject:token forKey:@"token"];
}

RCT_EXPORT_METHOD(unbind:(NSDictionary *)options)
{
    
}

RCT_EXPORT_METHOD(unbindAll)
{
    
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSArray *, supportFeatures)
{
    return @[@"pan",@"scroll",@"orientation",@"timing"];
}

RCT_EXPORT_SYNCHRONOUS_TYPED_METHOD(NSDictionary *, sgetComputedStyle:(NSString *)sourceRef)
{
    return @{};
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
