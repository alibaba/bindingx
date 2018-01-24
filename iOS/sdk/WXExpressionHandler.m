//
//  WXExpressionHandler.m
//  Core
//
//  Created by xiayun on 2017/5/24.
//  Copyright © 2017年 taobao. All rights reserved.
//

#import "WXExpressionHandler.h"
#import "WXExpressionGesture.h"
#import "WXExpressionScroller.h"
#import "WXExpression.h"
#import "WXExpressionProperty.h"
#import "WXExpressionExecutor.h"
#import "WXExpressionScope.h"
#import "WXExpressionTiming.h"
#import "WXExpressionOrientation.h"
#import <JavaScriptCore/JavaScriptCore.h>

@interface WXExpressionHandler ()

@end

@implementation WXExpressionHandler

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (instancetype)initWithExpressionType:(WXExpressionType)exprType
                            WXInstance:(WXSDKInstance *)instance
                                source:(WXComponent *)source {
    if (self = [super init]) {
        self.instance = instance;
        self.source = source;
        self.exprType = exprType;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(removeExpressionBinding) name:@"WXExpressionBindingRemove" object:nil];
    }
    return self;
}

- (void)updateTargets:(NSMapTable<NSString *, WXComponent *> *)targets
           expression:(NSDictionary<NSString *, NSDictionary *> *)targetExpression
         options:(NSDictionary *)options
       exitExpression:(NSString *)exitExpression
             callback:(WXKeepAliveCallback)callback {
    self.targets = targets;
    self.expressions = targetExpression;
    self.exitExpression = exitExpression;
    self.callback = callback;
    self.options = options;
}

- (void)removeExpressionBinding {
    self.targets = nil;
    self.expressions = nil;
}

+ (WXExpressionType)stringToExprType:(NSString *)typeStr {
    if ([@"pan" isEqualToString:typeStr]) {
        return WXExpressionTypePan;
    } else if ([@"scroll" isEqualToString:typeStr]) {
        return WXExpressionTypeScroll;
    } else if ([@"timing" isEqualToString:typeStr]) {
        return WXExpressionTypeTiming;
    } else if ([@"orientation" isEqualToString:typeStr]) {
        return WXExpressionTypeOrientation;
    }
    return WXExpressionTypeUndefined;
}

+ (WXExpressionHandler *)handlerWithExpressionType:(WXExpressionType)exprType
                                        WXInstance:(WXSDKInstance *)instance
                                            source:(WXComponent *)source {
    switch (exprType) {
        case WXExpressionTypePan:
            return [[WXExpressionGesture alloc] initWithExpressionType:exprType WXInstance:instance source:source];
        case WXExpressionTypeScroll:
            return [[WXExpressionScroller alloc] initWithExpressionType:exprType WXInstance:instance source:source];
        case WXExpressionTypeTiming:
            return [[WXExpressionTiming alloc] initWithExpressionType:exprType WXInstance:instance source:source];
        case WXExpressionTypeOrientation:
            return [[WXExpressionOrientation alloc] initWithExpressionType:exprType WXInstance:instance source:source];
        default:
            return [WXExpressionHandler new];
    }
}

- (BOOL)shouldExit:(NSDictionary *)scope {
    NSString* exitExpressionTransformed = self.exitExpression;
    if ([exitExpressionTransformed isKindOfClass:NSString.class]) {
        if( [WXUtility isBlankString:exitExpressionTransformed]) {
            return NO;
        }
    } else if ([exitExpressionTransformed isKindOfClass:NSDictionary.class]) {
        exitExpressionTransformed = (NSString *)((NSDictionary *)exitExpressionTransformed)[@"transformed"];
    }
    
    NSDictionary *expressionTree  = [NSJSONSerialization JSONObjectWithData:[exitExpressionTransformed dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
    if (!expressionTree || expressionTree.count == 0) {
        return NO;
    }
    
    NSObject *result = [[[WXExpression alloc] initWithRoot:expressionTree] executeInScope:scope];
    if (!result) {
        return NO;
    }
    
    if ([result isKindOfClass:[NSNumber class]]) {
        return [(NSNumber *)result boolValue];
    } else if ([result isKindOfClass:[NSString class]]) {
        return [(NSString *)result boolValue];
    }
    
    return NO;
}

- (BOOL)executeExpression:(NSDictionary *)scope {
    
    for (NSString *targetRef in self.expressions) {
        WXComponent *target = [self.targets objectForKey:targetRef];
        if (!target) {
            continue;
        }
        
        NSDictionary *epMap = self.expressions[targetRef];
        WXExpressionProperty *model = [[WXExpressionProperty alloc] init];
        
        // gather property
        for (NSString *property in epMap) {
            NSDictionary *expressionDic = epMap[property];
            id expression = expressionDic[@"expression"];
            NSDictionary *config = expressionDic[@"config"];
            
            NSDictionary* expressionTree = nil;
            NSString* originExpression = nil;
            if ([expression isKindOfClass:NSString.class]) {
                // expressionbinding V1
                expressionTree = [NSJSONSerialization JSONObjectWithData:[(NSString *)expression dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
            } else if ([expression isKindOfClass:NSDictionary.class]) {
                // expressionbinding V2
                expressionTree = [NSJSONSerialization JSONObjectWithData:[(NSString *)(NSDictionary *)expression[@"transformed"] dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
                originExpression = (NSString *)(NSDictionary *)expression[@"origin"];
            }
            NSObject *result = nil;
            if (expressionTree && expressionTree.count > 0) {
                result = [[[WXExpression alloc] initWithRoot:expressionTree] executeInScope:scope];
            } else if (originExpression) {
                JSContext* context = [JSContext new];
                for (NSString *key in scope) {
                    [context setObject:scope[key] forKeyedSubscript:key];
                }
                result = [[context evaluateScript:originExpression] toObject];
            }
            if (result) {
                [WXExpressionExecutor change:&model property:property config:config to:result];
            }
        }
        
        // execute
        [WXExpressionExecutor execute:model to:target];
    }
    
    // exit expression
    if ([self shouldExit:scope]) {
        return NO;
    }
    return YES;
}

- (NSMutableDictionary *)generalScope {
    NSMutableDictionary *scope = [WXExpressionScope generalScope];
    return scope;
}

@end
