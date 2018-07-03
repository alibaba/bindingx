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

#import "EBExpressionHandler.h"
#import "EBExpressionGesture.h"
#import "EBExpressionScroller.h"
#import "EBExpression.h"
#import "EBExpressionExecutor.h"
#import "EBExpressionScope.h"
#import "EBExpressionTiming.h"
#import "EBExpressionOrientation.h"
#import "EBUtility.h"
#import "EBHandlerFactory.h"

@interface EBEventHandlerFactory ()

@property(nonatomic, strong) NSMutableDictionary<NSString *, Class> *eventHandlers;
@property (nonatomic, strong)  NSLock   *lock;

@end

@implementation EBEventHandlerFactory

+ (instancetype)sharedInstance {
    static EBEventHandlerFactory* _sharedInstance = nil;
    static dispatch_once_t oncePredicate;
    dispatch_once(&oncePredicate, ^{
        _sharedInstance = [self new];
    });
    return _sharedInstance;
}

- (instancetype)init {
    if (self = [super init]) {
        _eventHandlers = [NSMutableDictionary dictionaryWithObjects:@[
                                                     EBExpressionGesture.class,
                                                     EBExpressionScroller.class,
                                                     EBExpressionTiming.class,
                                                     EBExpressionOrientation.class,
                                                     ]
                                           forKeys:@[
                                                     @"pan",
                                                     @"scroll",
                                                     @"timing",
                                                     @"orientation",
                                                     ]];
        _lock = [NSLock new];
    }
    return self;
}

- (Class)classWithEvent:(NSString *)event {
    [_lock lock];
    Class clazz = [_eventHandlers objectForKey:event];
    [_lock unlock];
    return clazz;
}

- (NSArray<NSString *> *)supportEvents {
    [_lock lock];
    NSArray<NSString *> *supportEvents = _eventHandlers.allKeys;
    [_lock unlock];
    return supportEvents;
}

- (void)registerEvent:(NSString *)event withClass:(Class)clazz {
    [_lock lock];
    [_eventHandlers setObject:clazz forKey:event];
    [_lock unlock];
}

+ (BOOL)containsEvent:(NSString *)event {
    return [[self supportEvents] containsObject:event];
}

+ (BOOL)eventRequireSource:(NSString *)event {
    Class clazz = [[EBEventHandlerFactory sharedInstance] classWithEvent:event];
    if ([clazz respondsToSelector:@selector(requireSource)]) {
        return [clazz requireSource];
    }
    return YES;
}

+ (NSArray<NSString *> *)supportEvents {
    return [EBEventHandlerFactory sharedInstance].supportEvents;
}

+ (void)registerEvent:(NSString *)event withClass:(Class)clazz {
    [[EBEventHandlerFactory sharedInstance] registerEvent:event withClass:clazz];
}

+ (EBExpressionHandler *)createHandlerWithEvent:(NSString *)event source:(id)source {
    Class clazz = [[EBEventHandlerFactory sharedInstance] classWithEvent:event];
    EBExpressionHandler *handler = [clazz new];
    if ([handler respondsToSelector:@selector(setSource:)]) {
        [handler setSource:source];
    }
    return handler;
}

@end


@implementation EBExpressionHandler

- (void)updateTargetExpression:(NSMapTable<id, NSDictionary *> *)expressionMap
                       options:(NSDictionary *)options
                exitExpression:(NSDictionary *)exitExpression
                      callback:(EBKeepAliveCallback)callback {
    [EBUtility performBlockOnMainThread:^{
        self.expressionMap = expressionMap;
        self.exitExpression = exitExpression;
    }];
    self.callback = callback;
    self.options = options;
}

+ (BOOL)requireSource {
    return YES;
}

- (void)removeExpressionBinding {
    [EBUtility performBlockOnMainThread:^{
        self.expressionMap = nil;
    }];
}

- (BOOL)executeExpression:(NSDictionary *)scope {
    if (![NSThread isMainThread]) {
        NSLog(@"not main thread!");
    }
    return [EBExpressionExecutor executeExpression:_expressionMap
                                    exitExpression:_exitExpression
                                             scope:scope];
}

- (NSMutableDictionary *)generalScope {
    NSMutableDictionary *generalScope= [EBExpressionScope generalScope];
    for (id<EBHandlerProtocol> handler in EBHandlerFactory.handlers) {
        if ([handler respondsToSelector:NSSelectorFromString(@"customScope")]) {
            [generalScope addEntriesFromDictionary:[handler customScope]];
        }
    }
    return generalScope;
}

@end

