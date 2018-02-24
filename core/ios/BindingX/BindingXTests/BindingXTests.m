//
//  BindingXTests.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/13.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBExpressionTiming.h"

@interface BindingXTests : XCTestCase

@end

@implementation BindingXTests

- (void)setUp {
    [super setUp];
    
    
    
}

- (void)tearDown {
    
    
    [super tearDown];
}

- (void)testExample {
    
    XCTestExpectation *expectationStart = [self expectationWithDescription:@"timing start success"];
    XCTestExpectation *expectation2 = [self expectationWithDescription:@"Test 222222222222"];
    
    
    EBExpressionHandler *timing = [EBExpressionHandler handlerWithExpressionType:WXExpressionTypeTiming
                                                                         source:@"123"];
    NSMapTable<id, NSDictionary *> *targetExpression = [NSMapTable new];
    NSDictionary* expression = @{
                                 @"transform.translateX": @{
                                             @"expression":@{
                                                     @"transformed" : @"{\"type\":\"+\",\"children\":[{\"type\":\"*\",\"children\":[{\"type\":\"NumericLiteral\",\"value\":0.5},{\"type\":\"-\",\"children\":[{\"type\":\"NumericLiteral\",\"value\":1},{\"type\":\"/\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":2000}]}]}]},{\"type\":\"NumericLiteral\",\"value\":0.5}]}"
                                                     },
                                         }
                                 };
    [targetExpression setObject:expression forKey:@"123"];
    [timing updateTargetExpression:targetExpression
                           options:nil
                    exitExpression:nil
                          callback:^(id  _Nonnull source, id  _Nonnull result, BOOL keepAlive) {
                              NSLog(@"%@",result);
                              if (result) {
                                  
                              }
                              [expectationStart fulfill];
                          }];
    
    
    [self waitForExpectationsWithTimeout:5 handler:^(NSError * _Nullable error) {
        NSLog(@"fulfilled");
    }];
    
    
}

- (void)testPerformanceExample {
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
