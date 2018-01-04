//
//  OrientationEvaluator.h
//  Pods
//
//  Created by 对象 on 2017/9/19.
//
#import "Quaternion.h"

@interface OrientationEvaluator: NSObject

- (instancetype)initWithConstraintAlpha:(NSNumber *)constraintAlpha constraintBeta:(NSNumber *)constraintBeta constraintGamma:(NSNumber *)constraintGamma;


- (Quaternion *)calculateWithDeviceAlpha:(double)deviceAlpha deviceBeta:(double)deviceBeta  deviceGamma:(double)deviceGamma;

@end
