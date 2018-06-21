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

#import <Foundation/Foundation.h>

@protocol EBHandlerProtocol <NSObject>

- (void)execute:(NSDictionary *)properties to:(id)target;

@optional
- (NSDictionary *)customScope;

@end

@interface EBHandlerFactory : NSObject

+ (NSArray<id<EBHandlerProtocol>>*)handlers;

+ (void)addHandler:(id<EBHandlerProtocol>)handler;

+ (void)removeHandler:(id<EBHandlerProtocol>)handler;

@end
