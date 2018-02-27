//
//  ExpressionTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBExpression.h"

@interface ExpressionTests : XCTestCase

@end

@implementation ExpressionTests

- (void)testExpression {
    NSDictionary *exitExpression = @{
                                     @"type": @">=",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                             ]
                                     };
    EBExpression *expression = [[EBExpression alloc] initWithRoot:exitExpression];
    
    NSDictionary *scope = @{@"t":@(1000)};
    NSObject *result = [expression executeInScope:scope];
    XCTAssertEqualObjects(result, @(0));
    
    scope = @{@"t":@(5000)};
    result = [expression executeInScope:scope];
    XCTAssertEqualObjects(result, @(1));
    
    scope = @{@"t":@(6000)};
    result = [expression executeInScope:scope];
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
    
    NSDictionary *scope = @{@"t":@(1000)};
    NSObject *result = [expression executeInScope:scope];
    XCTAssertEqualObjects(result, @(0));
    
    scope = @{@"t":@(5000)};
    result = [expression executeInScope:scope];
    XCTAssertEqualObjects(result, @(0));
    
    scope = @{@"t":@(6000)};
    result = [expression executeInScope:scope];
    XCTAssertEqualObjects(result, @(1));
}

@end
