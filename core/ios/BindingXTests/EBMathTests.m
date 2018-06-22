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

@interface EBMathTests : EBTestCase
@end

@implementation EBMathTests


- (void)testMax {
    //max(x,10)
    EBExpression *expression = [EBTestUtils expressionFromJSON:@"{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"max\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":10}]}]}"];
    
    self.scope[@"x"] = @(8);
    NSObject *result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(10));
    
    self.scope[@"x"] = @(11);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(11));
    
    self.scope[@"x"] = @(11);
    result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, @(11));
}


@end
