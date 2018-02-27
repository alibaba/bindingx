//
//  RNBindingXTests.m
//  RNBindingXTests
//
//  Created by 对象 on 2018/2/26.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "EBRNModule.h"

@interface RNBindingXTests : XCTestCase

@property (nonatomic, strong) EBRNModule *module;

@end

@implementation RNBindingXTests

- (void)setUp {
    [super setUp];
    
    self.module = [EBRNModule new];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testPrepare {
    [self.module prepare:@{
                           
                           @"eventType": @"pan",
                           @"anchor": @"1"
                           }];
}


- (void)testBind {
//    self.module.bridge = [[RCTBridge alloc] initWithBundleURL:<#(NSURL *)#> moduleProvider:<#^NSArray<id<RCTBridgeModule>> *(void)block#> launchOptions:<#(NSDictionary *)#>];
//    NSDictionary* result = [self.module bind:@{
//                           @"eventType": @"pan",
//                           @"anchor": @"1"
//                           }];
    
//    XCTAssertNotNil(result[@"token"]);
    
}

- (void)testSupportFeatures {
    NSArray* supportFeatures = [self.module supportFeatures];
    NSArray* rightFeatures = @[@"pan",@"scroll",@"orientation",@"timing"];
    XCTAssertEqualObjects(supportFeatures, rightFeatures);
}
@end
