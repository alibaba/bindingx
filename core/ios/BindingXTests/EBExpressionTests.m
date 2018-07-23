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

@interface EBExpressionTests : EBTestCase

@end

@implementation EBExpressionTests

- (void)testExpression {
    NSDictionary *exitExpression = @{
                                     @"type": @">=",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                             ]
                                     };
    EBExpression *expression = [[EBExpression alloc] initWithRoot:exitExpression];
    
    self.scope[@"t"] = @(1000);
    NSObject *result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(0));
    
    self.scope[@"t"] = @(5000);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(1));
    
    self.scope[@"t"] = @(6000);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(1));
}

- (void)testExpression2 {
    NSDictionary *exitExpression = @{
                                     @"type": @">",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                             ]
                                     };
    EBExpression *expression = [[EBExpression alloc] initWithRoot:exitExpression];
    
    self.scope[@"t"] = @(1000);
    NSObject *result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(0));
    
    self.scope[@"t"] = @(5000);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(0));
    
    self.scope[@"t"] = @(6000);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(1));
}

@end
