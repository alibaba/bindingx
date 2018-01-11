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

#import <Foundation/Foundation.h>

typedef void (^KeepAliveCallback)(_Nonnull id result, BOOL keepAlive);

extern void PerformBlockOnMainThread(void (^ _Nonnull block)(void));
extern void PerformBlockOnBridgeThread(void (^ _Nonnull block)(void));

@interface EBUtility: NSObject

+ (BOOL)isBlankString:(NSString *_Nullable)string;

+ (CGFloat)factor;

+ (void)execute:(NSDictionary *_Nullable)style to:(id _Nullable )target;

+ (BOOL)hasHorizontalPan:(id _Nullable )component;

+ (BOOL)hasVerticalPan:(id _Nullable )component;

@end


