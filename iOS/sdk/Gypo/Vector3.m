//
//  Vector3.m
//  AliWeex
//
//  Created by 对象 on 2017/9/19.
//

#import "Vector3.h"

@implementation Vector3

+ (instancetype)vectorWithX:(double)x y:(double)y z:(double)z {
    Vector3 *vector3 = [[Vector3 alloc] initWithX:x y:y z:z];
    return vector3;
}

- (instancetype)initWithX:(double)x y:(double)y z:(double)z {
    if (self = [self init]) {
        self.x = x;
        self.y = y;
        self.z = z;
    }
    return self;
}

- (instancetype)applyQuaternionW:(double)qw x:(double)qx y:(double)qy z:(double)qz {
    double x = self.x, y = self.y, z = self.z;
    
    // calculate quat * vector
    
    double ix = qw * x + qy * z - qz * y;
    double iy = qw * y + qz * x - qx * z;
    double iz = qw * z + qx * y - qy * x;
    double iw = -qx * x - qy * y - qz * z;
    
    // calculate result * inverse quat
    
    self.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
    self.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
    self.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;
    
    return  self;
}

@end
