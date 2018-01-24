//
//  WXJSMath.m
//  expression
//
//  Created by 寒泉 on 16/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WXJSMath.h"


@implementation WXJSMath : NSObject

+ (WXNativeFunction *)sin {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:sin([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)cos {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        
        return [[NSNumber alloc] initWithDouble:cos([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)tan {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:tan([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)asin {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:asin([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)acos {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        
        return [[NSNumber alloc] initWithDouble:acos([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)atan {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:atan([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)atan2 {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:atan2([arguments[0] doubleValue], [arguments[1] doubleValue])];
    }];
}
+ (WXNativeFunction *)pow {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:pow([arguments[0] doubleValue], [arguments[1] doubleValue])];
    }];
}
+ (WXNativeFunction *)exp {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:exp([arguments[0] doubleValue])];
    }];
}
+ (WXNativeFunction *)sqrt {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:sqrt([arguments[0] doubleValue])];
    }];
}
+ (WXNativeFunction *)cbrt {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:cbrt([arguments[0] doubleValue])];
    }];
}
+ (WXNativeFunction *)log {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:log([arguments[0] doubleValue])];
    }];
}
/*
+ (WXNativeFunction *)abs {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:abs([arguments[0] doubleValue])];
    }];
}
*/
+ (WXNativeFunction *)sign {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        double n = [arguments[0] doubleValue];
        return [[NSNumber alloc] initWithDouble:(n < 0) ? -1 : (n > 0) ? +1 : 0];
    }];
}
+ (WXNativeFunction *)ceil {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:ceil([arguments[0] doubleValue])];
    }];
}
+ (WXNativeFunction *)floor {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:floor([arguments[0] doubleValue])];
    }];
}
+ (WXNativeFunction *)round {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        return [[NSNumber alloc] initWithDouble:round([arguments[0] doubleValue])];
    }];
}

+ (WXNativeFunction *)max {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        if (arguments.count < 1) {
            return @(0);
        } else if (arguments.count < 2) {
            return (NSNumber *)arguments[0];
        } else {
            return @(MAX([arguments[0] doubleValue], [arguments[1] doubleValue]));
        }
    }];
}
+ (WXNativeFunction *)min {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        if (arguments.count < 1) {
            return @(0);
        } else if (arguments.count < 2) {
            return (NSNumber *)arguments[0];
        } else {
            return @(MIN([arguments[0] doubleValue], [arguments[1] doubleValue]));
        }
    }];
}

+ (WXNativeFunction *)abs {
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        if (arguments.count < 1) {
            return @(0);
        } else {
            return @(ABS([arguments[0] doubleValue]));
        }
    }];
}

@end
