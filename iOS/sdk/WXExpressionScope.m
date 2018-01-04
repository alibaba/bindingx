//
//  WXExpressionScope.m
//  AliWeex
//
//  Created by xiayun on 16/12/20.
//  Copyright © 2016年 alibaba.com. All rights reserved.
//

#import "WXExpressionScope.h"
#import "WXJSMath.h"
#import "WXJSTransform.h"
#import "WXJSEase.h"
#import "WXJSEvaluate.h"

@implementation WXExpressionScope

+ (NSMutableDictionary *)sharedScope {
    static NSMutableDictionary *sharedScope = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedScope = [[NSMutableDictionary alloc] init];
        [sharedScope setValue:WXJSMath.sin forKey:@"sin"];
        [sharedScope setValue:WXJSMath.cos forKey:@"cos"];
        [sharedScope setValue:WXJSMath.tan forKey:@"tan"];
        [sharedScope setValue:WXJSMath.asin forKey:@"asin"];
        [sharedScope setValue:WXJSMath.acos forKey:@"acos"];
        [sharedScope setValue:WXJSMath.atan forKey:@"atan"];
        [sharedScope setValue:WXJSMath.atan2 forKey:@"atan2"];
        
        [sharedScope setValue:WXJSMath.max forKey:@"max"];
        [sharedScope setValue:WXJSMath.min forKey:@"min"];
        [sharedScope setValue:WXJSMath.abs forKey:@"abs"];
        [sharedScope setValue:WXJSMath.sign forKey:@"sign"];
        
        [sharedScope setValue:WXJSMath.floor forKey:@"floor"];
        [sharedScope setValue:WXJSMath.ceil forKey:@"ceil"];
        [sharedScope setValue:WXJSMath.round forKey:@"round"];
        
        [sharedScope setValue:WXJSMath.pow forKey:@"pow"];
        [sharedScope setValue:WXJSMath.exp forKey:@"exp"];
        [sharedScope setValue:WXJSMath.log forKey:@"log"];
        [sharedScope setValue:WXJSMath.sqrt forKey:@"sqrt"];
        [sharedScope setValue:WXJSMath.cbrt forKey:@"cbrt"];

        [sharedScope setValue:WXJSTransform.asArray forKey:@"asArray"];
        [sharedScope setValue:WXJSTransform.asArray forKey:@"rgba"];
        [sharedScope setValue:WXJSTransform.translate forKey:@"translate"];
        [sharedScope setValue:WXJSTransform.scale forKey:@"scale"];
        [sharedScope setValue:WXJSTransform.matrix forKey:@"matrix"];
        
        [sharedScope setValue:WXJSEvaluate.evaluateColor forKey:@"evaluateColor"];
        
        [sharedScope setValue:WXJSEase.easeInQuad forKey:@"easeInQuad"];
        [sharedScope setValue:WXJSEase.easeOutQuad forKey:@"easeOutQuad"];
        [sharedScope setValue:WXJSEase.easeInOutQuad forKey:@"easeInOutQuad"];
        [sharedScope setValue:WXJSEase.easeInCubic forKey:@"easeInCubic"];
        [sharedScope setValue:WXJSEase.easeOutCubic forKey:@"easeOutCubic"];
        [sharedScope setValue:WXJSEase.easeInOutCubic forKey:@"easeInOutCubic"];
        [sharedScope setValue:WXJSEase.easeInQuart forKey:@"easeInQuart"];
        [sharedScope setValue:WXJSEase.easeOutQuart forKey:@"easeOutQuart"];
        [sharedScope setValue:WXJSEase.easeInOutQuart forKey:@"easeInOutQuart"];
        [sharedScope setValue:WXJSEase.easeInQuint forKey:@"easeInQuint"];
        [sharedScope setValue:WXJSEase.easeOutQuint forKey:@"easeOutQuint"];
        [sharedScope setValue:WXJSEase.easeInOutQuint forKey:@"easeInOutQuint"];
        [sharedScope setValue:WXJSEase.easeInSine forKey:@"easeInSine"];
        [sharedScope setValue:WXJSEase.easeOutSine forKey:@"easeOutSine"];
        [sharedScope setValue:WXJSEase.easeInOutSine forKey:@"easeInOutSine"];
        [sharedScope setValue:WXJSEase.easeInExpo forKey:@"easeInExpo"];
        [sharedScope setValue:WXJSEase.easeOutExpo forKey:@"easeOutExpo"];
        [sharedScope setValue:WXJSEase.easeInOutExpo forKey:@"easeInOutExpo"];
        [sharedScope setValue:WXJSEase.easeInCirc forKey:@"easeInCirc"];
        [sharedScope setValue:WXJSEase.easeOutCirc forKey:@"easeOutCirc"];
        [sharedScope setValue:WXJSEase.easeInOutCirc forKey:@"easeInOutCirc"];
        [sharedScope setValue:WXJSEase.easeInElastic forKey:@"easeInElastic"];
        [sharedScope setValue:WXJSEase.easeOutElastic forKey:@"easeOutElastic"];
        [sharedScope setValue:WXJSEase.easeInOutElastic forKey:@"easeInOutElastic"];
        [sharedScope setValue:WXJSEase.easeInBack forKey:@"easeInBack"];
        [sharedScope setValue:WXJSEase.easeOutBack forKey:@"easeOutBack"];
        [sharedScope setValue:WXJSEase.easeInOutBack forKey:@"easeInOutBack"];
        [sharedScope setValue:WXJSEase.easeInBounce forKey:@"easeInBounce"];
        [sharedScope setValue:WXJSEase.easeOutBounce forKey:@"easeOutBounce"];
        [sharedScope setValue:WXJSEase.easeInOutBounce forKey:@"easeInOutBounce"];
        [sharedScope setValue:WXJSEase.linear forKey:@"linear"];
        [sharedScope setValue:WXJSEase.cubicBezier forKey:@"cubicBezier"];
    });
    
    return sharedScope;
}

+ (NSMutableDictionary *)generalScope {
    return [[WXExpressionScope sharedScope] mutableCopy];
}

@end
