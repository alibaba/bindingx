//
//  EBBaseTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/25.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import "EBTestCase.h"

@implementation EBTestCase

- (void)setUp {
    [super setUp];
    
    _scope = [EBExpressionScope generalScope];
}

- (void)AssertEqualObjects:(NSObject *)object withNewScope:(NSDictionary *)newScope expression:(EBExpression *)expression
{
    [_scope addEntriesFromDictionary:newScope];
    NSObject *result = [expression executeInScope:self.scope];
    XCTAssertEqualObjects(result, object);
}

@end
