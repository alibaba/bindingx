/**
 * Copyright 2017 Alibaba Group
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

#import "EBExpressionScroller.h"
#import "EBExpressionScope.h"
#import "EBExpression.h"
#import "EBExpressionProperty.h"
#import "EBExpressionExecutor.h"

@interface EBExpressionScroller () <UIScrollViewDelegate>

@property (nonatomic, weak) id scroller;
@property (nonatomic, assign) CGPoint lastOffset;
@property (nonatomic, assign) CGPoint lastDOffset;
@property (nonatomic, assign) CGPoint turnOffset;//拐点
@property (nonatomic, assign) BOOL turnChange;//拐点

@end

@implementation EBExpressionScroller

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                                source:(id)source {
    if (self = [super initWithExpressionType:exprType source:source]) {
        [self initScroller];
    }
    return self;
}

- (void)initScroller {
    self.scroller = self.source;
    SEL sel = NSSelectorFromString(@"addScrollDelegate:");
    IMP imp = [self.scroller methodForSelector:sel];
    if (imp) {
        void (*func)(id) = (void *)imp;
        func(self);
    } else {
        sel = NSSelectorFromString(@"addScrollListener:");
        imp = [self.scroller methodForSelector:sel];
        if (imp) {
            void (*func)(id) = (void *)imp;
            func(self);
        }
    }
}

- (void)removeExpressionBinding {
    [super removeExpressionBinding];
    SEL sel = NSSelectorFromString(@"removeScrollDelegate:");
    IMP imp = [self.scroller methodForSelector:sel];
    if (imp) {
        void (*func)(id) = (void *)imp;
        func(self);
    } else {
        sel = NSSelectorFromString(@"removeScrollListener:");
        imp = [self.scroller methodForSelector:sel];
        if (imp) {
            void (*func)(id) = (void *)imp;
            func(self);
        }
    }
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
        PerformBlockOnBridgeThread(^{
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
        NSLog(@"%@",exception);
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self fireStateChangedEvent:@"start"];
}

- (NSDictionary *)setUpScope:(UIScrollView *)sender {
    NSMutableDictionary *scope = [self generalScope];
    CGFloat factor = [EBUtility factor];
    
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
    CGFloat factor = [EBUtility factor];
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
