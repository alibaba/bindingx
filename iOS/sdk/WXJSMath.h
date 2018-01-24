//
//  WXJSMath.h
//  expression
//
//  Created by 寒泉 on 08/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#include <math.h>
#import "WXNativeFunction.h"
#import "WXJSObject.h"

@interface WXJSMath: NSObject

+ (WXNativeFunction *)sin;
+ (WXNativeFunction *)cos;
+ (WXNativeFunction *)tan;
+ (WXNativeFunction *)asin;
+ (WXNativeFunction *)acos;
+ (WXNativeFunction *)atan;
+ (WXNativeFunction *)atan2;

+ (WXNativeFunction *)pow;
+ (WXNativeFunction *)exp;
+ (WXNativeFunction *)log;
+ (WXNativeFunction *)sqrt;
+ (WXNativeFunction *)cbrt;

+ (WXNativeFunction *)floor;
+ (WXNativeFunction *)ceil;
+ (WXNativeFunction *)round;

+ (WXNativeFunction *)max;
+ (WXNativeFunction *)min;
+ (WXNativeFunction *)abs;
+ (WXNativeFunction *)sign;

@end
