//
//  BindingXTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/13.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBExpressionTiming.h"

@interface TimingTests : XCTestCase

@end

@implementation TimingTests

- (void)testTimingExit {
    
    XCTestExpectation *expectationExit = [self expectationWithDescription:@"timing exit success"];
    
    
    EBExpressionHandler *timing = [EBExpressionHandler handlerWithExpressionType:WXExpressionTypeTiming
                                                                         source:@"123"];
    NSMapTable<id, NSDictionary *> *targetExpression = [NSMapTable new];
    NSDictionary *expression = nil;
    
    NSDictionary *exitExpression = @{
                                     @"type": @">=",
                                     @"children": @[
                                             @{@"type":@"Identifier",@"value":@"t"},
                                             @{@"type":@"NumericLiteral",@"value":@(5000)}
                                        ]
                                     };
    [targetExpression setObject:expression forKey:@"123"];
    [timing updateTargetExpression:targetExpression
                           options:nil
                    exitExpression:exitExpression
                          callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
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
