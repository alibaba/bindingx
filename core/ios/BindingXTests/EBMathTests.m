//
//  MathTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

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
