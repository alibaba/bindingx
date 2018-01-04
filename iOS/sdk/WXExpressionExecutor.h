//
//  WXExpressionExecutor.h
//  Pods
//
//  Created by xiayun on 16/12/20.
//
//

#import <Foundation/Foundation.h>
#import "WXExpressionProperty.h"
#import <WeexSDK/WeexSDK.h>

@interface WXExpressionExecutor : NSObject

+ (void)change:(WXExpressionProperty **)model property:(NSString *)propertyName config:(NSDictionary*)config to:(NSObject *)result;

+ (void)execute:(WXExpressionProperty *)model to:(WXComponent *)view;

@end
