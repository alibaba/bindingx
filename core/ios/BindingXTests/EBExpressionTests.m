//
//  ExpressionTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

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
