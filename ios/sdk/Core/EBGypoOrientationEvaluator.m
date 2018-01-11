/**
 * Copyright 2017 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "EBGypoOrientationEvaluator.h"
#import "EBGypoVector3.h"
#import "EBGypoEuler.h"

@interface EBGypoOrientationEvaluator()

@property (nonatomic, assign) BOOL enabled;
@property (nonatomic, assign) int screenOrientation;

@property (nonatomic, strong) NSNumber * constraintAlpha;
@property (nonatomic, strong) NSNumber * constraintBeta;
@property (nonatomic, strong) NSNumber * constraintGamma;

@property (nonatomic, assign) double constraintAlphaOffset;
@property (nonatomic, assign) double constraintBetaOffset;
@property (nonatomic, assign) double constraintGammaOffset;

@property (nonatomic, strong) EBGypoQuaternion *quaternion;
@property (nonatomic, strong) EBGypoQuaternion *q0;
@property (nonatomic, strong) EBGypoQuaternion *q1;
@property (nonatomic, strong) EBGypoVector3 *zee;
@property (nonatomic, strong) EBGypoEuler *euler;

@end


@implementation EBGypoOrientationEvaluator

- (instancetype)initWithConstraintAlpha:(NSNumber *)constraintAlpha constraintBeta:(NSNumber *)constraintBeta constraintGamma:(NSNumber *)constraintGamma {
    if (self = [self init]) {
        self.constraintAlpha = constraintAlpha;
        self.constraintBeta = constraintBeta;
        self.constraintGamma = constraintGamma;
        self.quaternion = [EBGypoQuaternion quaternionWithX:0 y:0 z:0 w:1];
        self.zee = [EBGypoVector3 vectorWithX:0 y:0 z:1];
        self.q0 = [EBGypoQuaternion quaternionWithX:0 y:0 z:0 w:0];
        self.q1 = [EBGypoQuaternion quaternionWithX:sqrt(0.5) y:0 z:0 w:sqrt(0.5)];
        self.euler = [EBGypoEuler new];
    }
    return self;
}

- (EBGypoQuaternion *)calculateWithDeviceAlpha:(double)deviceAlpha deviceBeta:(double)deviceBeta  deviceGamma:(double)deviceGamma {
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

