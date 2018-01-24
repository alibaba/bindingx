//
//  Euler.m
//  AliWeex
//
//  Created by 对象 on 2017/9/19.
//

#import "Euler.h"

#define ROTATION_ORDERS (@[@"XYZ",@"YZX",@"ZXY",@"XZY",@"YXZ",@"ZYX"])
#define DEFAULT_ORDER @"XYZ"

@implementation Euler

+ (instancetype)eulerWithX:(double)x y:(double)y z:(double)z order:(NSString *)order {
    Euler *euler = [[Euler alloc] initWithX:x y:y z:z order:order];
    return euler;
}

- (instancetype)initWithX:(double)x y:(double)y z:(double)z order:(NSString *)order{
    if (self = [self init]) {
        [self setValueWithX:x y:y z:z order:order];
    }
    return self;
}

- (void)setValueWithX:(double)x y:(double)y z:(double)z order:(NSString *)order {
    self.x = x;
    self.y = y;
    self.z = z;
    self.order = [ROTATION_ORDERS containsObject:order] ? order : DEFAULT_ORDER;
}

@end
