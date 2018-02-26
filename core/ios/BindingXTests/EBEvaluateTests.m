//
//  EvaluateTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

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
