//
//  Euler.h
//  Pods
//
//  Created by 对象 on 2017/9/19.
//

@interface Euler: NSObject

@property(nonatomic,assign) double x;
@property(nonatomic,assign) double y;
@property(nonatomic,assign) double z;
@property(nonatomic,copy) NSString *order;

+ (instancetype)eulerWithX:(double)x y:(double)y z:(double)z order:(NSString *)order;

- (void)setValueWithX:(double)x y:(double)y z:(double)z order:(NSString *)order;

@end
