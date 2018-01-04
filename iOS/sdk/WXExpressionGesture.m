//
//  WXExpressionGesture.m
//  AliWeex
//
//  Created by xiayun on 16/12/20.
//  Copyright © 2016年 alibaba.com. All rights reserved.
//

#import "WXExpressionGesture.h"
#import "WXExpressionScope.h"
#import "WXExpression.h"
#import "WXExpressionExecutor.h"
#import "WXExpressionProperty.h"
#import <WeexSDK/WXUtility.h>
#import <objc/runtime.h>

@interface WXExpressionGesture () <UIGestureRecognizerDelegate>

@property (nonatomic, weak) id<UIGestureRecognizerDelegate> tmpDelegate;
@property (nonatomic, assign) BOOL isHorizontal;
@property (nonatomic, assign) BOOL isVertical;

@end

@implementation WXExpressionGesture {
    BOOL _isMutex;
}

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                            WXInstance:(WXSDKInstance *)instance
                                source:(WXComponent *)source {
    if (self = [super initWithExpressionType:exprType WXInstance:instance source:source]) {
         [self initGesture];
    }
    return self;
}

- (void)dealloc {
    [_gesture removeTarget:self action:nil];
    _gesture.delegate = _tmpDelegate;
}

#pragma mark - public methods
- (void)removeExpressionBinding {
    [super removeExpressionBinding];
    
    if (self.tmpDelegate) {
        self.gesture.delegate = self.tmpDelegate;
        [self.gesture removeTarget:self action:nil];
        self.tmpDelegate = nil;
    }
    self.gesture = nil;
}

#pragma mark - private methods
- (void)initGesture {
    if (self.exprType == WXExpressionTypePan && !self.gesture) {
        __weak typeof(self) welf = self;
        WXPerformBlockOnMainThread(^{
            [welf addGuestureOnMainThread];
        });
    }
}

-(void) addGuestureOnMainThread {
    self.gesture = [self getPanGestureForComponent:self.source];
    
    if (!self.gesture) {
        UIGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handleGesture:)];
        self.gesture = panGesture;
        self.gesture.delegate = self;
        [self.source.view addGestureRecognizer:self.gesture];
    }
    
    if (self.gesture.delegate && self.gesture.delegate != self) {
        _tmpDelegate = self.gesture.delegate;
    }
    
    [self.gesture removeTarget:self action:nil];
    [self.gesture addTarget:self action:@selector(handleGesture:)];
    self.gesture.delegate = self;
}

- (UIPanGestureRecognizer *)getPanGestureForComponent:(WXComponent *)component {
    
    Ivar ivarPan = class_getInstanceVariable([component class], "_panGesture");
    id panObj = object_getIvar(component, ivarPan);
    
    if (!panObj || ![panObj isKindOfClass:[UIPanGestureRecognizer class]]) {
        SEL selector = NSSelectorFromString(@"addPanGesture");
        if ([component respondsToSelector:selector]) {
            [component performSelector:selector onThread:[NSThread mainThread] withObject:nil waitUntilDone:YES];
        }
        
        ivarPan = class_getInstanceVariable([component class], "_panGesture");
        panObj = object_getIvar(component, ivarPan);
    }
    
    if (panObj && [panObj isKindOfClass:[UIPanGestureRecognizer class]]) {
        _isHorizontal = [component.events containsObject:@"horizontalpan"];
        _isVertical = [component.events containsObject:@"verticalpan"];
        
        if (_isHorizontal || _isVertical) {
            _isMutex = YES;
        } else {
            _isMutex = NO;
        }
        return (UIPanGestureRecognizer *)panObj;
    }
    
    return nil;
}

- (void)fireStateChangedEvent:(UIGestureRecognizer *)sender {
    BOOL keepAlive = ![self isGestureEnded:sender.state];
    NSMutableDictionary *result = [[self gestureMap:sender] mutableCopy];
    result[@"state"] =  [self stateToString:sender.state];
    
    if (self.callback) {
        self.callback(result, keepAlive);
    }
    
    if (!keepAlive) {
        // free resouces
        self.targets = nil;
        self.expressions = nil;
        self.callback = nil;
    }
}

- (void)fireExitEvent:(UIGestureRecognizer *)sender {
    NSMutableDictionary *result = [[self gestureMap:sender] mutableCopy];
    result[@"state"] =  @"exit";
    
    if (self.callback) {
        self.callback(result, YES);
    }
}

- (NSDictionary *)gestureMap:(UIGestureRecognizer *)sender {
    if ([sender isKindOfClass:[UIPanGestureRecognizer class]]) {
        UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)sender;
        CGPoint offset = [pan translationInView:pan.view];

        CGFloat factor = [WXUtility defaultPixelScaleFactor];
        return @{@"deltaX":@(offset.x/factor),@"deltaY":@(offset.y/factor)};
    }
    return [NSDictionary dictionary];
}

#pragma mark - Gesture
- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer {
    if (gestureRecognizer == self.gesture) {
        if (self.exprType == WXExpressionTypePan && [gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) {
            if (!_isHorizontal && !_isVertical) {
                return YES;
            }
            
            CGPoint translation = [(UIPanGestureRecognizer*)_gesture translationInView:self.source.view];
            if (_isHorizontal && fabs(translation.y) > fabs(translation.x)) {
                return NO;
            } else if (_isVertical && fabs(translation.x) > fabs(translation.y)) {
                return NO;
            }
        }
    }
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRequireFailureOfGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    if (self.exprType == WXExpressionTypePan && gestureRecognizer == self.gesture) {
        
        if (otherGestureRecognizer.view && [otherGestureRecognizer.view isKindOfClass:[UIScrollView class]]) {
            UIScrollView *scroller = (UIScrollView *)otherGestureRecognizer.view;
            
            // direction
            if (_isHorizontal && scroller.contentSize.width + scroller.contentInset.left +scroller.contentInset.right > scroller.frame.size.width
                && scroller.contentSize.height + scroller.contentInset.top + scroller.contentInset.bottom <= scroller.frame.size.height) {
                return YES;
            }
            
            if (_isVertical && scroller.contentSize.height > scroller.frame.size.height
                && scroller.contentSize.width <= scroller.frame.size.height) {
                return YES;
            }
        } else if (otherGestureRecognizer.delegate && [otherGestureRecognizer.delegate isKindOfClass:[WXExpressionGesture class]]) {
            if ([self isInnerPan:(UIPanGestureRecognizer *)otherGestureRecognizer otherPan:(UIPanGestureRecognizer *)gestureRecognizer]) {
                WXExpressionGesture *exprGesture = (WXExpressionGesture *)(otherGestureRecognizer.delegate);
                
                // direction
                if (_isHorizontal && exprGesture.isHorizontal) {
                    return YES;
                }
                
                if (_isVertical && exprGesture.isVertical) {
                    return YES;
                }
            }
        } else if(self.exprType == WXExpressionTypePan && [otherGestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]){
            if ([self isInnerPan:(UIPanGestureRecognizer *)otherGestureRecognizer otherPan:(UIPanGestureRecognizer *)gestureRecognizer]) {
                return YES;
            }
        }
    }
    return NO;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldBeRequiredToFailByGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    if (self.exprType == WXExpressionTypePan && gestureRecognizer == self.gesture) {
        if ([otherGestureRecognizer isKindOfClass:NSClassFromString(@"UIScrollViewPanGestureRecognizer")]) {
            if (_isMutex) {
                return YES;
            }
        } else if (otherGestureRecognizer.delegate && [otherGestureRecognizer.delegate isKindOfClass:[WXExpressionGesture class]]) {
            if ([self isInnerPan:(UIPanGestureRecognizer *)gestureRecognizer otherPan:(UIPanGestureRecognizer *)otherGestureRecognizer]) {
                if (_isMutex) {
                    return YES;
                }
            }
        }
    }
    return NO;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    if (self.exprType == WXExpressionTypePan && gestureRecognizer == self.gesture) {
        if ([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]
            && [otherGestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) {
            if (!_isMutex) {
                return YES;
            }
            return NO;
        }
    }
    
    return YES;
}

- (BOOL)isInnerPan:(UIPanGestureRecognizer *)pan otherPan:(UIPanGestureRecognizer *)otherPan {
    UIView *otherView = otherPan.view;
    UIView *parentView = pan.view.superview;
    
    while (parentView) {
        if (parentView == otherView) {
            return YES;
        }
        parentView = parentView.superview;
    }
    return NO;
}

- (void)handleGesture:(UIGestureRecognizer *)sender {
    if (sender.state == UIGestureRecognizerStateBegan || sender.state == UIGestureRecognizerStateEnded
        || sender.state == UIGestureRecognizerStateCancelled || sender.state == UIGestureRecognizerStateFailed) {
        [self fireStateChangedEvent:sender];
    } else if (sender.state == UIGestureRecognizerStateChanged) {
        @try {
            NSDictionary *scope = [self setUpScopeForGesture:sender];
            [self executeExpression:scope];
        } @catch (NSException *exception) {
            WXLogError(@"%@",exception);
        }
    }
}

- (NSDictionary *)setUpScopeForGesture:(UIGestureRecognizer *)sender {
    NSMutableDictionary *scope = [self generalScope];
    
    if ([sender isKindOfClass:[UIPanGestureRecognizer class]]) {
        UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)sender;
        CGPoint offset = [pan translationInView:pan.view];
        
        CGFloat factor = [WXUtility defaultPixelScaleFactor];
        
        [scope setValue:@(offset.x / factor) forKey:@"x"];
        [scope setValue:@(offset.y / factor) forKey:@"y"];
    }
    
    return [scope copy];
}
        
- (NSString *)stateToString:(UIGestureRecognizerState)state {
    switch (state) {
        case UIGestureRecognizerStateBegan:
            return @"start";
        case UIGestureRecognizerStateChanged:
            return @"move";
        case UIGestureRecognizerStateEnded:
            return @"end";
        case UIGestureRecognizerStateCancelled:
            return @"cancel";
        case UIGestureRecognizerStateFailed:
            return @"fail";
        default:
            return @"unknown";
    }
}

- (BOOL)isGestureEnded:(UIGestureRecognizerState)state {
    return state == UIGestureRecognizerStateEnded
        || state == UIGestureRecognizerStateCancelled
        || state == UIGestureRecognizerStateFailed;
}

@end
