//
//  EBHandlerFactory.m
//  BindingX
//
//  Created by 对象 on 2018/4/2.
//  Copyright © 2018年 weexplugin. All rights reserved.
//

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
