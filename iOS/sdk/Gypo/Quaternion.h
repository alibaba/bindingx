//
//  Quaternion.h
//  Pods
//
//  Created by 对象 on 2017/9/19.
//
#import <CoreMotion/CoreMotion.h>
#import "Vector3.h"
#import "Euler.h"

@interface Quaternion: NSObject

@property(nonatomic,assign) double x;
@property(nonatomic,assign) double y;
@property(nonatomic,assign) double z;
@property(nonatomic,assign) double w;

+ (instancetype)quaternionWithX:(double)x y:(double)y z:(double)z w:(double)w;

- (instancetype)setFromAxisAngle:(Vector3 *)axis angle:(double)angle;
- (instancetype)setFromEuler:(Euler *)euler;
- (instancetype)multiply:(Quaternion *)q p:(Quaternion *)p;
- (instancetype)multiplyQuaternions:(Quaternion *)a b:(Quaternion *)b;

@end

