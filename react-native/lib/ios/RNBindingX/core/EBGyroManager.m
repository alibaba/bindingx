/**
 * Copyright 2018 Alibaba Group
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


#import "EBGyroManager.h"
#import <CoreMotion/CoreMotion.h>
#import "EBUtility.h"

#define GYRO_UPDATE_INTERVAL 1/60
#define rad2deg(rad) ((rad) * 180 / M_PI)

@interface EBGyroManager ()

@property(nonatomic, strong) CMMotionManager *motionManager;
@property(nonatomic, strong) NSMutableArray<id<EBGyroWatcherProtocol>> *watchers;

@end

@implementation EBGyroManager

+ (void)watchOrientation:(id<EBGyroWatcherProtocol>)watcher {
    [[EBGyroManager sharedInstance] watchOrientation:watcher];
}

+ (void)removeOrientation:(id<EBGyroWatcherProtocol>)watcher {
    [[EBGyroManager sharedInstance] removeOrientation:watcher];
}

+ (instancetype)sharedInstance {
    static EBGyroManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [EBGyroManager new];
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
        _watchers = [NSMutableArray new];
    }
    return self;
}

- (void)startMotionManager {
    if (!_motionManager) {
        _motionManager = [CMMotionManager new];
        _motionManager.gyroUpdateInterval = GYRO_UPDATE_INTERVAL;
        [_motionManager startDeviceMotionUpdatesToQueue:[NSOperationQueue new]
                                            withHandler:
         ^(CMDeviceMotion * _Nullable motion, NSError * _Nullable error) {
             if (error) {
                 return;
             }
             
             // Acceleration is user + gravity
             CMAcceleration userAccel = motion.userAcceleration;
             CMAcceleration gravity = motion.gravity;
             CMAcceleration totalAccel;
             totalAccel.x = userAccel.x + gravity.x;
             totalAccel.y = userAccel.y + gravity.y;
             totalAccel.z = userAccel.z + gravity.z;
             
             
             CMAttitude* attitude = motion.attitude;
             
             // Compose the raw motion data to an intermediate ZXY-based 3x3 rotation
             // matrix (R) where [z=attitude.yaw, x=attitude.pitch, y=attitude.roll]
             // in the form:
             //
             //   /  R[0]   R[1]   R[2]  \
             //   |  R[3]   R[4]   R[5]  |
             //   \  R[6]   R[7]   R[8]  /
             
             double cX = cos(attitude.pitch);
             double cY = cos(attitude.roll);
             double cZ = cos(attitude.yaw);
             double sX = sin(attitude.pitch);
             double sY = sin(attitude.roll);
             double sZ = sin(attitude.yaw);
             
             double R[] = {
                 cZ * cY - sZ * sX * sY,
                 - cX * sZ,
                 cY * sZ * sX + cZ * sY,
                 cY * sZ + cZ * sX * sY,
                 cZ * cX,
                 sZ * sY - cZ * cY * sX,
                 - cX * sY,
                 sX,
                 cX * cY
             };
             
             // Compute correct, normalized values for DeviceOrientation from
             // rotation matrix (R) according to the angle conventions defined in the
             // W3C DeviceOrientation specification.
             
             double zRot;
             double xRot;
             double yRot;
             
             if (R[8] > 0) {
                 zRot = atan2(-R[1], R[4]);
                 xRot = asin(R[7]);
                 yRot = atan2(-R[6], R[8]);
             } else if (R[8] < 0) {
                 zRot = atan2(R[1], -R[4]);
                 xRot = -asin(R[7]);
                 xRot += (xRot >= 0) ? -M_PI : M_PI;
                 yRot = atan2(R[6], -R[8]);
             } else {
                 if (R[6] > 0) {
                     zRot = atan2(-R[1], R[4]);
                     xRot = asin(R[7]);
                     yRot = -M_PI_2;
                 } else if (R[6] < 0) {
                     zRot = atan2(R[1], -R[4]);
                     xRot = -asin(R[7]);
                     xRot += (xRot >= 0) ? -M_PI : M_PI;
                     yRot = -M_PI_2;
                 } else {
                     zRot = atan2(R[3], R[0]);
                     xRot = (R[7] > 0) ? M_PI_2 : -M_PI_2;
                     yRot = 0;
                 }
             }
             
             // Rotation around the Z axis (pointing up. normalized to [0, 360] deg).
             double alpha = rad2deg(zRot > 0 ? zRot : (M_PI * 2 + zRot));
             // Rotation around the X axis (top to bottom).
             double beta  = rad2deg(xRot);
             // Rotation around the Y axis (side to side).
             double gamma = rad2deg(yRot);
             
             __weak typeof(self) welf = self;
             [EBUtility performBlockOnMainThread:^{
                 NSArray *watchers = [welf.watchers copy];
                 for (id<EBGyroWatcherProtocol> watcher in watchers) {
                     [watcher orientaionChanged:alpha beta:beta gamma:gamma];
                 }
             }];
         }];
    }
}

- (void)watchOrientation:(id<EBGyroWatcherProtocol>)watcher {
    [EBUtility performBlockOnMainThread:^{
        [_watchers addObject:watcher];
        [self startMotionManager];
    }];
}

- (void)removeOrientation:(id<EBGyroWatcherProtocol>)watcher {
    [EBUtility performBlockOnMainThread:^{
        [_watchers removeObject:watcher];
        if (_watchers.count == 0) {
            [_motionManager stopDeviceMotionUpdates];
            _motionManager = nil;
        }
    }];
}

@end
