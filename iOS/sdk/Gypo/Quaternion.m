//
//  Quaternion.m
//  AliWeex
//
//  Created by 对象 on 2017/9/19.
//

#import "Quaternion.h"
#import "Vector3.h"

@implementation Quaternion

+ (instancetype)quaternionWithX:(double)x y:(double)y z:(double)z w:(double)w {
    Quaternion *quaternion = [[Quaternion alloc] initWithWithX:x y:y z:z w:w];
    return quaternion;
}

- (instancetype)initWithWithX:(double)x y:(double)y z:(double)z w:(double)w {
    if (self = [self init]) {
        self.x = x;
        self.y = y;
        self.z = z;
        self.w = w;
    }
    return self;
}

- (instancetype)setFromEuler:(Euler *)euler {
    if(euler == nil) {
        return nil;
    }
    
    double c1 = cos(euler.x / 2);
    double c2 = cos(euler.y / 2);
    double c3 = cos(euler.z / 2);
    double s1 = sin(euler.x / 2);
    double s2 = sin(euler.y / 2);
    double s3 = sin(euler.z / 2);
    
    NSString* order = euler.order;
    
    if ([order isEqualToString:@"XYZ"]) {
        
        self.x = s1 * c2 * c3 + c1 * s2 * s3;
        self.y = c1 * s2 * c3 - s1 * c2 * s3;
        self.z = c1 * c2 * s3 + s1 * s2 * c3;
        self.w = c1 * c2 * c3 - s1 * s2 * s3;
        
    } else if ([order isEqualToString:@"YXZ"]) {
        
        self.x = s1 * c2 * c3 + c1 * s2 * s3;
        self.y = c1 * s2 * c3 - s1 * c2 * s3;
        self.z = c1 * c2 * s3 - s1 * s2 * c3;
        self.w = c1 * c2 * c3 + s1 * s2 * s3;
        
    } else if ([order isEqualToString:@"ZXY"]) {
        
        self.x = s1 * c2 * c3 - c1 * s2 * s3;
        self.y = c1 * s2 * c3 + s1 * c2 * s3;
        self.z = c1 * c2 * s3 + s1 * s2 * c3;
        self.w = c1 * c2 * c3 - s1 * s2 * s3;
        
    } else if ([order isEqualToString:@"ZYX"]) {
        
        self.x = s1 * c2 * c3 - c1 * s2 * s3;
        self.y = c1 * s2 * c3 + s1 * c2 * s3;
        self.z = c1 * c2 * s3 - s1 * s2 * c3;
        self.w = c1 * c2 * c3 + s1 * s2 * s3;
        
    } else if ([order isEqualToString:@"YZX"]) {
        
        self.x = s1 * c2 * c3 + c1 * s2 * s3;
        self.y = c1 * s2 * c3 + s1 * c2 * s3;
        self.z = c1 * c2 * s3 - s1 * s2 * c3;
        self.w = c1 * c2 * c3 - s1 * s2 * s3;
        
    } else if ([order isEqualToString:@"XZY"]) {
        
        self.x = s1 * c2 * c3 - c1 * s2 * s3;
        self.y = c1 * s2 * c3 - s1 * c2 * s3;
        self.z = c1 * c2 * s3 + s1 * s2 * c3;
        self.w = c1 * c2 * c3 + s1 * s2 * s3;
        
    }
    
    return self;
}

- (instancetype)setFromAxisAngle:(Vector3 *)axis angle:(double)angle {
    double halfAngle = angle / 2;
    double s = sin(halfAngle);
    
    self.x = axis.x * s;
    self.y = axis.y * s;
    self.z = axis.z * s;
    self.w = cos(halfAngle);
    return self;
}

- (instancetype)multiply:(Quaternion *)q p:(Quaternion *)p {
    if(p != nil) {
        return [self multiplyQuaternions:q b:p];
    }
    return [self multiplyQuaternions:self b:q];
}

- (instancetype)multiplyQuaternions:(Quaternion *)a b:(Quaternion *)b {
    
    double qax = a.x, qay = a.y, qaz = a.z, qaw = a.w;
    double qbx = b.x, qby = b.y, qbz = b.z, qbw = b.w;
    
    self.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
    self.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
    self.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
    self.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;
    return self;
}

@end
