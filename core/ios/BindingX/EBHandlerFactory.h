//
//  EBHandlerFactory.h
//  BindingX
//
//  Created by 对象 on 2018/4/2.
//  Copyright © 2018年 weexplugin. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol EBHandlerProtocol

- (void)execute:(NSDictionary *)model to:(id)target;

@end

@interface EBHandlerFactory : NSObject

+ (NSArray<id<EBHandlerProtocol>>*)handlers;

+ (void)addHandler:(id<EBHandlerProtocol>)handler;

+ (void)removeHandler:(id<EBHandlerProtocol>)handler;

@end
