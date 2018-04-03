//
//  EBHandlerFactory.m
//  BindingX
//
//  Created by 对象 on 2018/4/2.
//  Copyright © 2018年 weexplugin. All rights reserved.
//

#import "EBHandlerFactory.h"

@interface EBHandlerFactory ()

@property(nonatomic, strong) NSMutableArray<id<EBHandler>>* handlers;

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

+ (NSArray<id<EBHandler>> *)handlers {
    return [EBHandlerFactory sharedInstance].handlers;
}

+ (void)addHandler:(id<EBHandler>)handler {
    return [[EBHandlerFactory sharedInstance].handlers addObject:handler];
}

@end
