//
//  WXExpressionTiming.m
//  Core
//
//  Created by xiayun on 2017/6/2.
//  Copyright © 2017年 taobao. All rights reserved.
//

#import "WXExpressionTiming.h"

@interface WXExpressionTiming ()

@property (nonatomic, strong) CADisplayLink *displayLink;
@property (nonatomic, assign) CFTimeInterval duration;

@end

@implementation WXExpressionTiming

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                            WXInstance:(WXSDKInstance *)instance
                                source:(WXComponent *)source {
    if (self = [super initWithExpressionType:exprType WXInstance:instance source:source]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationWillResignActive:)
                                                     name:UIApplicationWillResignActiveNotification object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationDidBecomeActive:)
                                                     name:UIApplicationDidBecomeActiveNotification object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)updateTargets:(NSMapTable<NSString *,WXComponent *> *)targets
           expression:(NSDictionary<NSString *,NSDictionary *> *)targetExpression
         options:(NSDictionary *)options
       exitExpression:(NSString *)exitExpression
             callback:(WXKeepAliveCallback)callback {
    [super updateTargets:targets
              expression:targetExpression
            options:options
          exitExpression:exitExpression
                callback:callback];
    
    [self initDisplayLink];
}

- (void)applicationWillResignActive:(NSNotification *)notification
{
    [self.displayLink setPaused:YES];
}

- (void)applicationDidBecomeActive:(NSNotification *)notification
{
    [self.displayLink setPaused:NO];
}

- (void)removeExpressionBinding {
    [super removeExpressionBinding];
    
    if (self.displayLink) {
        [self.displayLink invalidate];
        self.displayLink = nil;
    }
}

#pragma mark - Private methods
- (void)initDisplayLink {
    self.duration = 0;
    
    self.displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(handleDisplay)];
    [self.displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
    [self.displayLink setPaused:NO];
    
    [self fireStateChangedEvent:@"start"];
}

- (void)handleDisplay {
    @try {
        NSDictionary *scope = [self setUpScope];
        BOOL exit = ![self executeExpression:scope];
        if (exit) {
            [self.displayLink invalidate];
            self.displayLink = nil;
            [self fireStateChangedEvent:@"exit"];
        }
    } @catch (NSException *exception) {
        WXLogError(@"%@",exception);
    }
}

- (NSDictionary *)setUpScope {
    NSMutableDictionary *scope = [self generalScope];
    
    self.duration += (self.displayLink.duration * 1000);
    [scope setValue:@(self.duration) forKey:@"t"];
    
    return [scope copy];
}

- (void)fireStateChangedEvent:(NSString *)state {
    NSDictionary *result = @{@"t": @(_duration),
                             @"state": state};
    
    if (self.callback) {
        self.callback(result, YES);
    }
}

@end
