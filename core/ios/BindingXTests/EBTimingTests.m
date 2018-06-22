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

#import <XCTest/XCTest.h>
#import "EBTestCase.h"
#import "EBExpressionHandler.h"

@interface EBTimingTests : EBTestCase

@end

@implementation EBTimingTests

- (void)testTimingExit {
    
    XCTestExpectation *expectationExit = [self expectationWithDescription:@"timing exit success"];
    
    
    EBExpressionHandler *timing = [EBExpressionHandler handlerWithExpressionType:WXExpressionTypeTiming
                                                                         source:@"123"];
    NSMapTable<NSString *, id> *targetMap = [NSMapTable strongToWeakObjectsMapTable];
    NSMutableDictionary<NSString *, NSDictionary *> *expressionDict = [NSMutableDictionary new];
    NSDictionary *expression = nil;
    
    NSDictionary *exitExpression = @{
                                     @"type": @">=",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                        ]
                                     };
    [targetMap setObject:@"123" forKey:@"123"];
    [expressionDict setObject:expression forKey:@"123"];
    [timing updateTargetMap:targetMap expressionDict:expressionDict options:nil exitExpression:exitExpression callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
        NSLog(@"%@",result);
        if ([result[@"state"] isEqualToString:@"exit"]) {
            [expectationExit fulfill];
        }
    }];
    
    [self waitForExpectationsWithTimeout:5.1 handler:^(NSError * _Nullable error) {
        NSLog(@"fulfilled");
    }];
    
}

@end
