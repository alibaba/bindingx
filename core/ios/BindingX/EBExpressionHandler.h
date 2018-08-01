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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^EBKeepAliveCallback)(id source , id result, BOOL keepAlive);

@class EBExpressionHandler;

@interface EBEventHandlerFactory : NSObject

+ (BOOL)containsEvent:(NSString *)event;

+ (BOOL)eventRequireSource:(NSString *)event;

+ (NSArray<NSString *> *)supportEvents;

+ (void)registerEvent:(NSString *)event withClass:(Class)clazz;

+ (EBExpressionHandler *)createHandlerWithEvent:(NSString *)event source:(id)source;

@end

@interface EBExpressionHandler : NSObject

@property (nonatomic, weak) id source;

@property (nonatomic, copy) NSDictionary *exitExpression;
@property (nonatomic, copy) EBKeepAliveCallback callback;
@property (nonatomic, strong, nullable) NSMapTable<id, NSDictionary *> *expressionMap;
@property (nonatomic, strong) NSDictionary *options;

- (void)updateTargetExpression:(NSMapTable<id, NSDictionary *> *)expressionMap
                       options:(NSDictionary *)options
                exitExpression:(NSDictionary *)exitExpression
                      callback:(EBKeepAliveCallback)callback;

+ (BOOL)requireSource;

- (void)removeExpressionBinding;

- (BOOL)executeExpression:(NSDictionary *)scope;

- (NSMutableDictionary *)generalScope;

@end

NS_ASSUME_NONNULL_END
