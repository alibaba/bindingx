//
//  WXExpressionHandler.h
//  Core
//
//  Created by xiayun on 2017/5/24.
//  Copyright © 2017年 taobao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WeexSDK/WeexSDK.h>

typedef NS_ENUM(NSInteger, WXExpressionType) {
    WXExpressionTypeUndefined = -1,
    WXExpressionTypePan,
    WXExpressionTypeScroll,
    WXExpressionTypeTiming,
    WXExpressionTypeOrientation
};

@interface WXExpressionHandler : NSObject

@property (nonatomic, assign) WXExpressionType exprType;
@property (nonatomic, weak) WXSDKInstance *instance;
@property (nonatomic, weak) WXComponent *source;

@property (nonatomic, copy) NSString *exitExpression;
@property (nonatomic, copy) WXKeepAliveCallback callback;
@property (nonatomic, strong) NSMapTable<NSString *, WXComponent *> *targets;
@property (nonatomic, strong) NSDictionary<NSString *, NSDictionary *> *expressions;
@property (nonatomic, strong) NSDictionary *options;

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                            WXInstance:(WXSDKInstance *)instance
                                source:(WXComponent *)source;

- (void)updateTargets:(NSMapTable<NSString *, WXComponent *> *)targets
           expression:(NSDictionary<NSString *, NSDictionary *> *)targetExpression
         options:(NSDictionary *)options
       exitExpression:(NSString *)exitExpression
             callback:(WXKeepAliveCallback)callback;

- (void)removeExpressionBinding;

+ (WXExpressionType)stringToExprType:(NSString *)typeStr;

+ (WXExpressionHandler *)handlerWithExpressionType:(WXExpressionType)exprType
                                        WXInstance:(WXSDKInstance *)instance
                                            source:(WXComponent *)source;

- (BOOL)executeExpression:(NSDictionary *)scope;

- (NSMutableDictionary *)generalScope;

@end
