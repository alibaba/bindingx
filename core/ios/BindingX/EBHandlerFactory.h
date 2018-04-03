//
//  EBHandlerFactory.h
//  BindingX
//
//  Created by 对象 on 2018/4/2.
//  Copyright © 2018年 weexplugin. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol EBHandler

- (void)execute:(NSDictionary *)model to:(id)target;

@end

@interface EBHandlerFactory : NSObject

+ (NSArray<id<EBHandler>>*)handlers;

+ (void)addHandler:(id<EBHandler>)handler;

@end
