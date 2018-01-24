//
//  WXExpressionGesture.h
//  AliWeex
//
//  Created by xiayun on 16/12/20.
//  Copyright © 2016年 alibaba.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <WeexSDK/WeexSDK.h>
#import "WXExpressionHandler.h"

@interface WXExpressionGesture : WXExpressionHandler

@property (nonatomic, weak) UIGestureRecognizer *gesture;

@end
