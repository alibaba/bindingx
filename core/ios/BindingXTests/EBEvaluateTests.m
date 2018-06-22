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

@interface EBEvaluateTests : EBTestCase

@end

@implementation EBEvaluateTests

- (void)testExample {
    
    //evaluateColor('#ff0000','#0000ff',min(x,650)/650)
    EBExpression *expression = [EBTestUtils expressionFromJSON:@"{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"evaluateColor\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"StringLiteral\",\"value\":\"'#ff0000'\"},{\"type\":\"StringLiteral\",\"value\":\"'#0000ff'\"},{\"type\":\"/\",\"children\":[{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"min\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":650}]}]},{\"type\":\"NumericLiteral\",\"value\":650}]}]}]}"];
    
    self.scope[@"x"] = @(650);
    NSArray *result = (NSArray *)[expression executeInScope:self.scope];
    NSArray *right = @[@(0),@(0),@(255)];
    XCTAssertEqualObjects(result, right);
    
}

@end
