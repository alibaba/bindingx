//
//  WXJSEase.h
//  Pods
//
//  Created by 对象 on 2017/8/10.
//
//

#import "WXNativeFunction.h"

@interface WXJSEase: NSObject

+ (WXNativeFunction *)easeInQuad;
+ (WXNativeFunction *)easeOutQuad;
+ (WXNativeFunction *)easeInOutQuad;
+ (WXNativeFunction *)easeInCubic;
+ (WXNativeFunction *)easeOutCubic;
+ (WXNativeFunction *)easeInOutCubic;
+ (WXNativeFunction *)easeInQuart;
+ (WXNativeFunction *)easeOutQuart;
+ (WXNativeFunction *)easeInOutQuart;
+ (WXNativeFunction *)easeInQuint;
+ (WXNativeFunction *)easeOutQuint;
+ (WXNativeFunction *)easeInOutQuint;
+ (WXNativeFunction *)easeInSine;
+ (WXNativeFunction *)easeOutSine;
+ (WXNativeFunction *)easeInOutSine;
+ (WXNativeFunction *)easeInExpo;
+ (WXNativeFunction *)easeOutExpo;
+ (WXNativeFunction *)easeInOutExpo;
+ (WXNativeFunction *)easeInCirc;
+ (WXNativeFunction *)easeOutCirc;
+ (WXNativeFunction *)easeInOutCirc;
+ (WXNativeFunction *)easeInElastic;
+ (WXNativeFunction *)easeOutElastic;
+ (WXNativeFunction *)easeInOutElastic;
+ (WXNativeFunction *)easeInBack;
+ (WXNativeFunction *)easeOutBack;
+ (WXNativeFunction *)easeInOutBack;
+ (WXNativeFunction *)easeInBounce;
+ (WXNativeFunction *)easeOutBounce;
+ (WXNativeFunction *)easeInOutBounce;
+ (WXNativeFunction *)linear;
+ (WXNativeFunction *)cubicBezier;

@end
