//
//  EBEventEmitter.m
//  Binding
//
//  Created by 对象 on 2018/1/29.
//

#import "EBEventEmitter.h"

@implementation EBEventEmitter

RCT_EXPORT_MODULE(testBridge)

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"binding"];
}

RCT_EXPORT_METHOD(test)
{
    
    [self sendEventWithName:@"binding" body:@{@"x":@(123)}];
}

@end
