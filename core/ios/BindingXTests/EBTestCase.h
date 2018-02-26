//
//  EBBaseTests.h
//  BindingXTests
//
//  Created by 对象 on 2018/2/25.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBTestUtils.h"

@interface EBTestCase : XCTestCase

@property(nonatomic, strong) NSMutableDictionary *scope;

- (void)AssertEqualObjects:(NSObject *)object withNewScope:(NSDictionary *)newScope expression:(EBExpression *)expression;

@end
