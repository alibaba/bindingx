//
//  EvaluateTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBTestUtils.h"

@interface EvaluateTests : XCTestCase

@property(nonatomic, strong) NSMutableDictionary *scope;

@end

@implementation EvaluateTests


- (void)setUp {
    [super setUp];
    
    _scope = [EBExpressionScope generalScope];
}


- (void)testExample {
    
    //evaluateColor('#ff0000','#0000ff',min(x,650)/650)
    EBExpression *expression = [EBTestUtils expressionFromJSON:@"{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"evaluateColor\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"StringLiteral\",\"value\":\"'#ff0000'\"},{\"type\":\"StringLiteral\",\"value\":\"'#0000ff'\"},{\"type\":\"/\",\"children\":[{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"min\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":650}]}]},{\"type\":\"NumericLiteral\",\"value\":650}]}]}]}"];
    
    _scope[@"x"] = @(650);
    NSArray *result = (NSArray *)[expression executeInScope:_scope];
    XCTAssertEqualObjects(result[0], @(0));
    XCTAssertEqualObjects(result[1], @(0));
    XCTAssertEqualObjects(result[2], @(255));
    
}

@end
