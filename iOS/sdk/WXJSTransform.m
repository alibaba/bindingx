//
//  WXJSTransform.m
//  Pods
//
//  Created by xiayun on 16/12/22.
//
//

#import "WXJSTransform.h"

@implementation WXJSTransform

+ (WXNativeFunction *)translate {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return arguments;
    }];
}

+ (WXNativeFunction *)scale {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return arguments;
    }];
}

+ (WXNativeFunction *)matrix {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return arguments;
    }];
}

+ (WXNativeFunction *)asArray {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return arguments;
    }];
}

@end
