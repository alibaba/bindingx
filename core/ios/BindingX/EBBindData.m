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

#import "EBBindData.h"

@interface EBBindData ()

@property (nonatomic, strong) NSMutableDictionary<NSString *, NSMutableDictionary<NSString *, EBExpressionHandler *> *> *sourceMap;

@end

@implementation EBBindData

- (NSMutableDictionary<NSString *, NSMutableDictionary<NSString *, EBExpressionHandler *> *> *)sourceMap {
    if (!_sourceMap) {
        _sourceMap = [NSMutableDictionary<NSString *, NSMutableDictionary<NSString *, EBExpressionHandler *> *> dictionary];
    }
    return _sourceMap;
}

- (NSMutableDictionary<NSString *, EBExpressionHandler *> *)handlerMapForToken:(NSString *)token {
    return [self.sourceMap objectForKey:token];
}

- (EBExpressionHandler *)handlerForToken:(NSString *)token
                               eventType:(NSString *)eventType {
    return [[self handlerMapForToken:token] objectForKey:eventType];
}

- (void)putHandler:(EBExpressionHandler *)handler
          forToken:(NSString *)token
         eventType:(NSString *)eventType {
    NSMutableDictionary<NSString *, EBExpressionHandler *> *handlerMap = [self handlerMapForToken:token];
    if (!handlerMap) {
        handlerMap = [NSMutableDictionary<NSString *, EBExpressionHandler *> dictionary];
        self.sourceMap[token] = handlerMap;
    }
    handlerMap[eventType] = handler;
}

- (void)removeHandler:(EBExpressionHandler *)handler
             forToken:(NSString *)token
            eventType:(NSString *)eventType {
    NSMutableDictionary<NSString *, EBExpressionHandler *> *handlerMap = [self handlerMapForToken:token];
    if (handlerMap) {
        [handlerMap removeObjectForKey:eventType];
    }
}

- (void)unbindAll {
    for (NSString *sourceRef in self.sourceMap) {
        NSMutableDictionary *handlerMap = self.sourceMap[sourceRef];
        for (NSNumber *expressionType in handlerMap) {
            EBExpressionHandler *handler = handlerMap[expressionType];
            [handler removeExpressionBinding];
        }
        [handlerMap removeAllObjects];
    }
    [self.sourceMap removeAllObjects];
}

+ (id)parseExpression:(NSObject *)expression
{
    if ([expression isKindOfClass:NSDictionary.class]) {
        NSDictionary* expressiondict = (NSDictionary *)expression;
        NSString* transformedExpressionStr = expressiondict[@"transformed"];
        if (transformedExpressionStr && [transformedExpressionStr isKindOfClass:NSString.class]) {
            return [NSJSONSerialization JSONObjectWithData:[transformedExpressionStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
        }
        else {
            return expressiondict[@"origin"];
        }
    } else if ([expression isKindOfClass:NSString.class]) {
        NSString* expressionStr = (NSString *)expression;
        return [NSJSONSerialization JSONObjectWithData:[expressionStr dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
    }
    return nil;
}

@end
