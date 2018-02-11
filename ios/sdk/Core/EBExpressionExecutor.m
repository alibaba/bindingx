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

#import "EBExpressionExecutor.h"
#import "EBTaffyTuple.h"
#import "NSObject+EBTuplePacker.h"
#import <objc/message.h>
#import "EBUtility.h"

typedef NS_ENUM(NSInteger, WXEPViewProperty) {
    WXEPViewPropertyUndefined = 0,
    WXEPViewPropertyTranslate,
    WXEPViewPropertyTranslateX,
    WXEPViewPropertyTranslateY,
    WXEPViewPropertyRotate,
    WXEPViewPropertyScale,
    WXEPViewPropertyScaleX,
    WXEPViewPropertyScaleY,
    WXEPViewPropertyTransform,
    WXEPViewPropertyAlpha,
    WXEPViewPropertyBackgroundColor,
    WXEPViewPropertyColor,
    WXEPViewPropertyFrame,
    WXEPViewPropertyLeft,
    WXEPViewPropertyTop,
    WXEPViewPropertyWidth,
    WXEPViewPropertyHeight,
    WXEPViewPropertyContentOffset,
    WXEPViewPropertyContentOffsetX,
    WXEPViewPropertyContentOffsetY,
    WXEPViewPropertyPerspective,
    WXEPViewPropertyRotateX,
    WXEPViewPropertyRotateY
};

@implementation EBExpressionExecutor

+ (void)change:(EBExpressionProperty **)model property:(NSString *)propertyName config:(NSDictionary*)config to:(NSObject *)result {
    WXEPViewProperty property = [[EBExpressionExecutor viewPropertyMap][propertyName] integerValue];
    CGFloat factor = [EBUtility factor];
    switch (property) {
        case WXEPViewPropertyTranslate:
            [EBExpressionExecutor makeTranslate:result model:model];
            break;
        case WXEPViewPropertyTranslateX:
            [*model setTx:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyTranslateY:
            [*model setTy:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyRotate:
            [*model setAngle:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyScale:
            if (![result isKindOfClass:[NSArray class]]) {
                result = @[result, result];
            }
            [EBExpressionExecutor makeScale:result model:model];
            break;
        case WXEPViewPropertyScaleX:
            [*model setSx:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyScaleY:
            [*model setSy:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyAlpha:
            [*model setAlpha:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyBackgroundColor:
            [*model setBackgroundColor:result];
            break;
        case WXEPViewPropertyColor:
            [*model setColor:result];
            break;
        case WXEPViewPropertyLeft:
            [*model setLeft:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyTop:
            [*model setTop:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyWidth:
            [*model setWidth:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyHeight:
            [*model setHeight:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyContentOffset:
            [EBExpressionExecutor makeContentOffset:result model:model];
            break;
        case WXEPViewPropertyContentOffsetX:
            [*model setContentOffsetX:[EBExpressionExecutor unpackSingleRet:result]*factor];
            break;
        case WXEPViewPropertyContentOffsetY:
            [*model setContentOffsetY:[EBExpressionExecutor unpackSingleRet:result]*factor];
            break;
        case WXEPViewPropertyRotateX:
            [*model setRotateX:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        case WXEPViewPropertyRotateY:
            [*model setRotateY:[EBExpressionExecutor unpackSingleRet:result]];
            break;
        default:
            break;
    }
    
    if( config && config[@"perspective"] )
    {
        [*model setPerspective:[config[@"perspective"] floatValue]];
    }
    
    if( config && config[@"transformOrigin"] )
    {
        [*model setTransformOrigin:config[@"transformOrigin"]];
    }
}

+ (NSDictionary *)viewPropertyMap {
    static NSDictionary *map = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        map = @{@"transform.translate":@(WXEPViewPropertyTranslate),
                @"transform.translateX":@(WXEPViewPropertyTranslateX),
                @"transform.translateY":@(WXEPViewPropertyTranslateY),
                @"transform.rotate":@(WXEPViewPropertyRotate),
                @"transform.scale":@(WXEPViewPropertyScale),
                @"transform.scaleX":@(WXEPViewPropertyScaleX),
                @"transform.scaleY":@(WXEPViewPropertyScaleY),
                @"transform.matrix":@(WXEPViewPropertyTransform),
                @"opacity":@(WXEPViewPropertyAlpha),
                @"background-color":@(WXEPViewPropertyBackgroundColor),
                @"color":@(WXEPViewPropertyColor),
                @"frame":@(WXEPViewPropertyFrame),
                @"left":@(WXEPViewPropertyLeft),
                @"top":@(WXEPViewPropertyTop),
                @"width":@(WXEPViewPropertyWidth),
                @"height":@(WXEPViewPropertyHeight),
                @"scroll.contentOffset":@(WXEPViewPropertyContentOffset),
                @"scroll.contentOffsetX":@(WXEPViewPropertyContentOffsetX),
                @"scroll.contentOffsetY":@(WXEPViewPropertyContentOffsetY),
                @"transform.rotateX":@(WXEPViewPropertyRotateX),
                @"transform.rotateY":@(WXEPViewPropertyRotateY),
                @"transform.rotateZ":@(WXEPViewPropertyRotate),};
    });
    return map;
}

+ (void)makeTranslate:(NSObject *)result model:(EBExpressionProperty **)model {
    id x, y;
    [_(x, y) unpackFrom:result];
    
    (*model).tx = [x doubleValue];
    (*model).ty = [y doubleValue];
}

+ (void)makeScale:(NSObject *)result model:(EBExpressionProperty **)model {
    id x, y;
    [_(x, y) unpackFrom:result];
    (*model).sx = [x doubleValue];
    (*model).sy = [y doubleValue];
}

+ (void)makeContentOffset:(NSObject *)result model:(EBExpressionProperty **)model {
    id x, y;
    [_(x, y) unpackFrom:result];
    
    CGFloat factor = [EBUtility factor];
    
    (*model).contentOffsetX = [x doubleValue] * factor;
    (*model).contentOffsetY = [y doubleValue] * factor;
}

+ (CGFloat)unpackSingleRet:(NSObject *)result {
    id a;
    [_(a) unpackFrom:result];
    return [a doubleValue];
}

@end
