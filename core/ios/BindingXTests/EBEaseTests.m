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

#import "EBTestCase.h"

@interface EBEaseTests : EBTestCase

@end

@implementation EBEaseTests


- (void)testExample {
    
    //linear(t,0,500,2000)
    EBExpression *expression = [EBTestUtils expressionFromJSON:@"{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"linear\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":0},{\"type\":\"NumericLiteral\",\"value\":500},{\"type\":\"NumericLiteral\",\"value\":2000}]}]}"];
    
//    self.scope[@"t"] = @(1000);
//    NSObject *result = [expression executeInScope:self.scope];
//    XCTAssertEqualObjects(result, @(250));
    
    [self AssertEqualObjects:@(250)
                   withNewScope:@{@"t":@(1000)}
                  expression:expression];
}

@end
