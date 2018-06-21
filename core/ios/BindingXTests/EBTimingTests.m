//
//  BindingXTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/13.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBTestCase.h"
#import "EBExpressionHandler.h"

@interface EBTimingTests : EBTestCase

@end

@implementation EBTimingTests

- (void)testTimingExit {
    
    XCTestExpectation *expectationExit = [self expectationWithDescription:@"timing exit success"];
    
    
    EBExpressionHandler *timing = [EBExpressionHandler handlerWithExpressionType:WXExpressionTypeTiming
                                                                         source:@"123"];
    NSMapTable<NSString *, id> *targetMap = [NSMapTable strongToWeakObjectsMapTable];
    NSMutableDictionary<NSString *, NSDictionary *> *expressionDict = [NSMutableDictionary new];
    NSDictionary *expression = nil;
    
    NSDictionary *exitExpression = @{
                                     @"type": @">=",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                        ]
                                     };
    [targetMap setObject:@"123" forKey:@"123"];
    [expressionDict setObject:expression forKey:@"123"];
    [timing updateTargetMap:targetMap expressionDict:expressionDict options:nil exitExpression:exitExpression callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
        NSLog(@"%@",result);
        if ([result[@"state"] isEqualToString:@"exit"]) {
            [expectationExit fulfill];
        }
    }];
    
    [self waitForExpectationsWithTimeout:5.1 handler:^(NSError * _Nullable error) {
        NSLog(@"fulfilled");
    }];
    
}

@end
