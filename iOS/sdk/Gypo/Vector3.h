//
//  Vector3.h
//  Pods
//
//  Created by 对象 on 2017/9/19.
//

@interface Vector3: NSObject

@property(nonatomic,assign) double x;
@property(nonatomic,assign) double y;
@property(nonatomic,assign) double z;

+ (instancetype)vectorWithX:(double)x y:(double)y z:(double)z;

- (instancetype)applyQuaternionW:(double)qw x:(double)qx y:(double)qy z:(double)qz;

@end
