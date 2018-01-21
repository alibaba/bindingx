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

#import "EBExpressionOrientation.h"
#import "EBGypoOrientationEvaluator.h"
#import <CoreMotion/CoreMotion.h>

#define SUPPORT_SCENE_TYPES (@[@"2d",@"3d"])
#define SCENE_TYPE_2D @"2d"

@interface EBExpressionOrientation ()
@end

@implementation EBExpressionOrientation
{
    bool _isStarted;
    double _startAlpha;
    double _startBeta;
    double _startGamma;
    double _lastAlpha;
    double _lastBeta;
    double _lastGamma;
    
    NSString *_sceneType;
    
    EBGypoOrientationEvaluator *_evaluatorX;
    EBGypoOrientationEvaluator *_evaluatorY;
    EBGypoOrientationEvaluator *_evaluator3D;
    NSMutableArray<NSNumber *> *_alphaRecords;
    CMMotionManager* _motionManager;
    
}

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                                source:(id)source {
    if (self = [super initWithExpressionType:exprType source:source]) {
        _isStarted = false;
        _startAlpha = 0;
        _startBeta = 0;
        _startGamma = 0;
        _lastAlpha = 0;
        _lastBeta = 0;
        _lastGamma = 0;
        _alphaRecords = [NSMutableArray new];
        _motionManager = [CMMotionManager new];
    }
    return self;
}

- (void)updateTargetExpression:(NSMapTable<id, NSDictionary *> *)targetExpression
         options:(NSDictionary *)options
       exitExpression:(NSString *)exitExpression
             callback:(KeepAliveCallback)callback {
    [super updateTargetExpression:targetExpression
            options:options
          exitExpression:exitExpression
                callback:callback];
    
    if (options != nil) {
        _sceneType = [options[@"sceneType"] lowercaseString];
        _sceneType = [SUPPORT_SCENE_TYPES containsObject:_sceneType] ? _sceneType : SCENE_TYPE_2D;
    } else {
        _sceneType = SCENE_TYPE_2D;
    }
    if ([_sceneType isEqualToString:SCENE_TYPE_2D]) {
        _evaluatorX = [[EBGypoOrientationEvaluator alloc] initWithConstraintAlpha:nil constraintBeta:@(90.0) constraintGamma:nil];
        _evaluatorY = [[EBGypoOrientationEvaluator alloc] initWithConstraintAlpha:@(0.0) constraintBeta:nil constraintGamma:@(90.0)];
    } else {
        _evaluator3D = [[EBGypoOrientationEvaluator alloc] initWithConstraintAlpha:nil constraintBeta:nil constraintGamma:nil];
    }
    
    __weak typeof(self) welf = self;
    _motionManager.gyroUpdateInterval = 1/60;
    NSOperationQueue* queue = [[NSOperationQueue alloc] init];
    [_motionManager startGyroUpdatesToQueue:queue withHandler:^(CMGyroData * _Nullable gyroData, NSError * _Nullable error) {
        [welf handleUpdates:gyroData.rotationRate.x beta:gyroData.rotationRate.y gamma:gyroData.rotationRate.z];
    }];
    
    [self fireStateChangedEvent:@"start" alpha:0 beta:0 gamma:0];
}

- (void)removeExpressionBinding {
    [super removeExpressionBinding];
    [self stopWatchOrientation];
    // 非主线程则为eb dealloc调用，无需执行回调，否则将crash
    // 另外这里的主线程是由于ebmodule在主线程下，否则应该判断和ebmodule同线程
    if ([NSThread isMainThread]) {
        [self fireStateChangedEvent:@"end" alpha:_lastAlpha beta:_lastBeta gamma:_lastGamma];
    }
}
    
- (void)stopWatchOrientation {
    [_motionManager stopGyroUpdates];
}

#pragma mark - privateMethods
- (BOOL)handleUpdates:(double)alpha beta:(double)beta gamma:(double)gamma {
    
    if(!_isStarted){
        _isStarted = true;
        _startAlpha = alpha;
        _startBeta = beta;
        _startGamma = gamma;
    }
    _lastAlpha = alpha;
    _lastBeta = beta;
    _lastGamma = gamma;
    
    @try {
        
        double x=0,y=0,z=0;
        
        double formatAlpha = [self formatAlpha:alpha startAlpha:_startAlpha];
        
        if ([_sceneType isEqualToString:SCENE_TYPE_2D]) {
            EBGypoQuaternion *quaternionX = [_evaluatorX calculateWithDeviceAlpha:formatAlpha deviceBeta:beta deviceGamma:gamma];
            EBGypoQuaternion *quaternionY = [_evaluatorY calculateWithDeviceAlpha:formatAlpha deviceBeta:beta deviceGamma:gamma];
            EBGypoVector3 *vecX = [EBGypoVector3 vectorWithX:0 y:0 z:1];
            [vecX applyQuaternionW:quaternionX.w x:quaternionX.x y:quaternionX.y z:quaternionX.z];
            
            EBGypoVector3 *vecY = [EBGypoVector3 vectorWithX:0 y:1 z:1];
            [vecY applyQuaternionW:quaternionY.w x:quaternionY.x y:quaternionY.y z:quaternionY.z];
            
            x = 90 - acos(vecX.x) * 180 / M_PI;
            y = 90 - acos(vecY.y) * 180 / M_PI;
        } else {
            EBGypoQuaternion *quaternion = [_evaluator3D calculateWithDeviceAlpha:formatAlpha deviceBeta:beta deviceGamma:gamma];
            x = quaternion.x;
            y = quaternion.y;
            z = quaternion.z;
        }
        
        NSDictionary *scope = [self setUpScope:alpha beta:beta gamma:gamma x:x y:y z:z];
        
        BOOL exit = ![self executeExpression:scope];
        if (exit) {
            [self stopWatchOrientation];
            [self fireStateChangedEvent:@"exit" alpha:alpha beta:beta gamma:gamma];
            return NO;
        }
        return YES;
    } @catch (NSException *exception) {
        NSLog(@"%@",exception);
        return NO;
    }
}

- (double)formatAlpha:(double)currentAlpha startAlpha:(double)startAlpha{
    double threshold = 360.0;
    [_alphaRecords addObject:@(currentAlpha)];
    long l = _alphaRecords.count;
    if (l > 1) {
        if (l>5) {
            [_alphaRecords removeObjectAtIndex:0];
            l = l-1;
        }
        double times = 0;
        for (int i = 1; i < l; i++) {
            double last = [_alphaRecords[i-1] doubleValue];
            double current = [_alphaRecords[i] doubleValue];
            if(last && current) {
                if(current-last < -threshold/2) {
                    times = floor(last / threshold) + 1;
                    current = current + times * threshold;
                    _alphaRecords[i] = @(current);
                }
                if (current-last > threshold/2) {
                    _alphaRecords[i] = @(current - threshold);
                }
            }
        }
    }
    return fmod([_alphaRecords.lastObject doubleValue] - startAlpha, threshold);
}

- (NSDictionary *)setUpScope:(double)alpha beta:(double)beta gamma:(double)gamma x:(double)x y:(double)y z:(double)z {
    NSMutableDictionary *scope = [self generalScope];
    
    [scope setValue:@(alpha) forKey:@"alpha"];
    [scope setValue:@(beta) forKey:@"beta"];
    [scope setValue:@(gamma) forKey:@"gamma"];
    
    [scope setValue:@(alpha - _startAlpha) forKey:@"dalpha"];
    [scope setValue:@(beta - _startBeta) forKey:@"dbeta"];
    [scope setValue:@(gamma - _startGamma) forKey:@"dgamma"];
    
    [scope setValue:@(x) forKey:@"x"];
    [scope setValue:@(y) forKey:@"y"];
    [scope setValue:@(z) forKey:@"z"];
    
    return [scope copy];
}

- (void)fireStateChangedEvent:(NSString *)state alpha:(double)alpha beta:(double)beta gamma:(double)gamma {
    NSDictionary *result = @{@"alpha": @(alpha),
                             @"beta": @(beta),
                             @"gamma": @(gamma),
                             @"state": state};
    
    if (self.callback) {
        self.callback(result, YES);
    }
}

@end
