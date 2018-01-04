//
//  OrientationEvaluator.m
//  AliWeex
//
//  Created by 对象 on 2017/9/19.
//

#import "OrientationEvaluator.h"
#import "Vector3.h"
#import "Euler.h"

@interface OrientationEvaluator()

@property (nonatomic, assign) BOOL enabled;
@property (nonatomic, assign) int screenOrientation;

@property (nonatomic, strong) NSNumber * constraintAlpha;
@property (nonatomic, strong) NSNumber * constraintBeta;
@property (nonatomic, strong) NSNumber * constraintGamma;

@property (nonatomic, assign) double constraintAlphaOffset;
@property (nonatomic, assign) double constraintBetaOffset;
@property (nonatomic, assign) double constraintGammaOffset;

@property (nonatomic, strong) Quaternion *quaternion;
@property (nonatomic, strong) Quaternion *q0;
@property (nonatomic, strong) Quaternion *q1;
@property (nonatomic, strong) Vector3 *zee;
@property (nonatomic, strong) Euler *euler;

@end


@implementation OrientationEvaluator

- (instancetype)initWithConstraintAlpha:(NSNumber *)constraintAlpha constraintBeta:(NSNumber *)constraintBeta constraintGamma:(NSNumber *)constraintGamma {
    if (self = [self init]) {
        self.constraintAlpha = constraintAlpha;
        self.constraintBeta = constraintBeta;
        self.constraintGamma = constraintGamma;
        self.quaternion = [Quaternion quaternionWithX:0 y:0 z:0 w:1];
        self.zee = [Vector3 vectorWithX:0 y:0 z:1];
        self.q0 = [Quaternion quaternionWithX:0 y:0 z:0 w:0];
        self.q1 = [Quaternion quaternionWithX:sqrt(0.5) y:0 z:0 w:sqrt(0.5)];
        self.euler = [Euler new];
    }
    return self;
}

- (Quaternion *)calculateWithDeviceAlpha:(double)deviceAlpha deviceBeta:(double)deviceBeta  deviceGamma:(double)deviceGamma {
    double alpha = _constraintAlpha != nil ? [_constraintAlpha doubleValue] : (deviceAlpha + _constraintAlphaOffset);// Z
    double beta = _constraintBeta != nil ? [_constraintBeta doubleValue] : (deviceBeta + _constraintBetaOffset);// X
    double gamma = _constraintGamma != nil ? [_constraintGamma doubleValue] : (deviceGamma + _constraintGammaOffset);// Y
    
    // TODO 设备方向写死为0 纵向
    [self.euler setValueWithX:beta/180.0*M_PI y:alpha/180.0*M_PI z:-gamma/180.0*M_PI order:@"YXZ"];
    [_quaternion setFromEuler:self.euler];
    [_quaternion multiply:_q1 p:nil];
    [_quaternion multiply:[_q0 setFromAxisAngle:_zee angle:0] p:nil];
    
    return _quaternion;
}

@end

