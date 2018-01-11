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
            [EBExpressionExecutor makeBackgroundColor:result model:model];
            break;
        case WXEPViewPropertyColor:
            [EBExpressionExecutor makeColor:result model:model];
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

+ (void)execute:(EBExpressionProperty *)model to:(id)target {
    
    NSMutableDictionary *styles = [NSMutableDictionary dictionary];
    
    if (model.isTransformChanged) {
        NSString *transform = @"";
        if (model.isTranslateChanged) {
            transform = [NSString stringWithFormat:@"translate(%f,%f) ", model.tx, model.ty];
        }
        if (model.isRotateChanged) {
            transform = [transform stringByAppendingFormat:@"rotate(%fdeg) ", model.angle];
        }
        if (model.isScaleChagned) {
            transform = [transform stringByAppendingFormat:@"scale(%f, %f) ", model.sx, model.sy];
        }
        if (model.isRotateXChanged) {
            transform = [transform stringByAppendingFormat:@"rotatex(%fdeg) ", model.rotateX];
        }
        if (model.isRotateYChanged) {
            transform = [transform stringByAppendingFormat:@"rotatey(%fdeg) ", model.rotateY];
        }
        if (model.isPerspectiveChanged) {
            transform = [transform stringByAppendingFormat:@"perspective(%f) ", model.perspective];
        }
        styles[@"transform"] = transform;
    }
    if (model.isTransformOriginChanged) {
        styles[@"transformOrigin"] = model.transformOrigin;
    }
    if (model.isLeftChanged) {
        styles[@"left"] = @(model.left);
    }
    if (model.isTopChanged) {
        styles[@"top"] = @(model.top);
    }
    if (model.isWidthChanged) {
        styles[@"width"] = @(model.width);
    }
    if (model.isHeightChanged) {
        styles[@"height"] = @(model.height);
    }
    if (model.isBackgroundColorChanged) {
        styles[@"backgroundColor"] = model.backgroundColor;
    }
    if (model.isColorChanged) {
        styles[@"color"] = model.color;
    }
    if (model.isAlphaChanged) {
        styles[@"opacity"] = @(model.alpha);
    }
    
    [EBUtility execute:styles to:target];
    
//    if ((model.isContentOffsetXChanged || model.isContentOffsetYChanged) && [target conformsToProtocol:@protocol(WXScrollerProtocol)]) {
//        id<WXScrollerProtocol> scroller = (id<WXScrollerProtocol>)target;
//        CGFloat offsetX = (model.isContentOffsetXChanged ? model.contentOffsetX : scroller.contentOffset.x);
//        CGFloat offsetY = (model.isContentOffsetYChanged ? model.contentOffsetY : scroller.contentOffset.y);
//        offsetX = MIN(offsetX, scroller.contentSize.width-target.view.frame.size.width);
//        offsetX = MAX(0, offsetX);
//        offsetY = MIN(offsetY, scroller.contentSize.height-target.view.frame.size.height);
//        offsetY = MAX(0, offsetY);
//        [scroller setContentOffset:CGPointMake(offsetX, offsetY) animated:NO];
//    }
    
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

+ (void)makeBackgroundColor:(NSObject *)result model:(EBExpressionProperty **)model {
    (*model).backgroundColor = [EBExpressionExecutor hexFromUIColor:[EBExpressionExecutor makeColor:result]];
}

+ (void)makeColor:(NSObject *)result model:(EBExpressionProperty **)model {
    (*model).color = [EBExpressionExecutor hexFromUIColor:[EBExpressionExecutor makeColor:result]];
}

+ (UIColor *)makeColor:(NSObject *)result {
    id r, g, b, a = @(1);
    [_(r, g, b, a) unpackFrom:result];
    
    return [UIColor colorWithRed:[r doubleValue]/255.0f green:[g doubleValue]/255.0f blue:[b doubleValue]/255.0f alpha:[a doubleValue]];
}

+ (CGFloat)unpackSingleRet:(NSObject *)result {
    id a;
    [_(a) unpackFrom:result];
    return [a doubleValue];
}

+ (NSString *)hexFromUIColor:(UIColor *)color {
    if (CGColorGetNumberOfComponents(color.CGColor) < 4) {
        const CGFloat *components = CGColorGetComponents(color.CGColor);
        color = [UIColor colorWithRed:components[0]
                                green:components[0]
                                 blue:components[0]
                                alpha:components[1]];
    }
    
    if (CGColorSpaceGetModel(CGColorGetColorSpace(color.CGColor)) != kCGColorSpaceModelRGB) {
        return [NSString stringWithFormat:@"#FFFFFF"];
    }
    
    return [NSString stringWithFormat:@"#%02X%02X%02X", (int)((CGColorGetComponents(color.CGColor))[0]*255.0),
            (int)((CGColorGetComponents(color.CGColor))[1]*255.0),
            (int)((CGColorGetComponents(color.CGColor))[2]*255.0)];
}

@end
