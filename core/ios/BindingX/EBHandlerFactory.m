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

#import "EBHandlerFactory.h"

@interface EBHandlerFactory ()

@property(nonatomic, strong) NSMutableArray<id<EBHandlerProtocol>>* handlers;

@end

@implementation EBHandlerFactory

+ (instancetype)sharedInstance {
    static EBHandlerFactory* _sharedInstance = nil;
    static dispatch_once_t oncePredicate;
    dispatch_once(&oncePredicate, ^{
        _sharedInstance = [[self alloc] init];
        _sharedInstance.handlers = [[NSMutableArray alloc] init];
    });
    return _sharedInstance;
}

+ (NSArray<id<EBHandlerProtocol>> *)handlers {
    return [[EBHandlerFactory sharedInstance].handlers copy];
}

+ (void)addHandler:(id<EBHandlerProtocol>)handler {
    [[EBHandlerFactory sharedInstance].handlers addObject:handler];
}

+ (void)removeHandler:(id<EBHandlerProtocol>)handler {
    [[EBHandlerFactory sharedInstance].handlers removeObject:handler];
}

@end
