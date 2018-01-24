//
//  WXExpressionScroller.m
//  Core
//
//  Created by xiayun on 2017/5/24.
//  Copyright © 2017年 taobao. All rights reserved.
//

#import "WXExpressionScroller.h"
#import <WeexSDK/WXScrollerProtocol.h>
#import "WXExpressionScope.h"
#import "WXExpression.h"
#import "WXExpressionProperty.h"
#import "WXExpressionExecutor.h"

@interface WXExpressionScroller () <UIScrollViewDelegate>

@property (nonatomic, weak) id<WXScrollerProtocol> scroller;
@property (nonatomic, assign) CGPoint lastOffset;
@property (nonatomic, assign) CGPoint lastDOffset;
@property (nonatomic, assign) CGPoint turnOffset;//拐点
@property (nonatomic, assign) BOOL turnChange;//拐点

@end

@implementation WXExpressionScroller

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                            WXInstance:(WXSDKInstance *)instance
                                source:(WXComponent *)source {
    if (self = [super initWithExpressionType:exprType WXInstance:instance source:source]) {
        [self initScroller];
    }
    return self;
}

- (void)initScroller {
    self.scroller = (id<WXScrollerProtocol>)self.source;
    [self.scroller addScrollDelegate:self];
}

- (void)removeExpressionBinding {
    [super removeExpressionBinding];
    [self.scroller removeScrollDelegate:self];
    // 非主线程则为eb dealloc调用，无需执行回调，否则将crash
    // 另外这里的主线程是由于ebmodule在主线程下，否则应该判断ebmodule同线程
    if ([NSThread isMainThread]) {
        [self fireStateChangedEvent:@"end"];
    }
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    @try {
        _turnChange = false;
        NSDictionary *scope = [self setUpScope:scrollView];
        
        __block __weak typeof(self) welf = self;
        WXPerformBlockOnBridgeThread(^{
            //切换到bridge线程，解决turn fire后表达式异步执行造成的抖动问题
            if (_turnChange) {
                [welf fireTurnEvent:scope];
                return;
            }
            BOOL exit = ![welf executeExpression:scope];
            if (exit) {
                [welf fireStateChangedEvent:@"end"];
                return;
            }
        });
    } @catch (NSException *exception) {
        WXLogError(@"%@",exception);
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self fireStateChangedEvent:@"start"];
}

- (NSDictionary *)setUpScope:(UIScrollView *)sender {
    NSMutableDictionary *scope = [self generalScope];
    CGFloat factor = [WXUtility defaultPixelScaleFactor];
    
    CGPoint offset = sender.contentOffset;
    CGPoint dOffset = CGPointMake(offset.x - _lastOffset.x, offset.y - _lastOffset.y);
    
    if ( (dOffset.x>0 && _lastDOffset.x<0) || (dOffset.x<0 && _lastDOffset.x>0) ) {
        _turnOffset.x = _lastOffset.x;
        _turnChange = true;
    }
    if ( (dOffset.y>0 && _lastDOffset.y<0) || (dOffset.y<0 && _lastDOffset.y>0) ) {
        _turnOffset.y = _lastOffset.y;
        _turnChange = true;
    }
    
    [scope setValue:@(offset.x / factor) forKey:@"x"];
    [scope setValue:@(offset.y / factor) forKey:@"y"];
    [scope setValue:@(dOffset.x / factor) forKey:@"dx"];
    [scope setValue:@(dOffset.y / factor) forKey:@"dy"];
    [scope setValue:@((offset.x - _turnOffset.x) / factor) forKey:@"tdx"];
    [scope setValue:@((offset.y - _turnOffset.y) / factor) forKey:@"tdy"];
    _lastOffset = offset;
    _lastDOffset = dOffset;
    
    return [scope copy];
}

- (void)fireStateChangedEvent:(NSString *)state {
    CGFloat factor = [WXUtility defaultPixelScaleFactor];
    NSDictionary *result = @{@"x": @(_lastOffset.x / factor),
                             @"y": @(_lastOffset.y / factor),
                             @"state": state};
    
    if (self.callback) {
        self.callback(result, YES);
    }
}

// 拐点触发回调
- (void)fireTurnEvent:(NSDictionary* )scope {
    NSDictionary *result = @{
                             @"x": scope[@"x"],
                             @"y": scope[@"y"],
                             @"dx": scope[@"dx"],
                             @"dy": scope[@"dy"],
                             @"tdx": scope[@"tdx"],
                             @"tdy": scope[@"tdy"],
                             @"state": @"turn"
                             };
    if (self.callback) {
        self.callback(result, YES);
    }
}

@end
