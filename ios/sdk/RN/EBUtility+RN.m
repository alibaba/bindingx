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

#import "EBUtility+RN.h"
#import <React/RCTUIManagerUtils.h>
#import <React/RCTUtils.h>
#import <React/RCTScrollView.h>

void EBPerformBlockOnBridgeThread(void (^ _Nonnull block)(void))
{
    RCTExecuteOnUIManagerQueue(^{
        block();
    });
}

void EBPerformBlockOnMainThread(void (^ _Nonnull block)(void))
{
    RCTExecuteOnMainQueue(^{
        block();
    });
}

__weak static RCTUIManager* _uiManager = nil;

@implementation EBUtility (RN)

+ (void)setUIManager:(RCTUIManager *)uiManager
{
    _uiManager = uiManager;
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"

+ (void)execute:(EBExpressionProperty *)model to:(id)target
{
//    UIView *view = [self getViewByRef:target];
    
    
    NSMutableDictionary *styles = [NSMutableDictionary dictionary];
    if (model.isTransformChanged) {
        NSMutableArray *transform = [NSMutableArray new];
        if (model.isTranslateChanged) {
            [transform addObject:@{@"translateX":@(model.tx)}];
            [transform addObject:@{@"translateY":@(model.ty)}];
        }
        if (model.isRotateChanged) {
            [transform addObject:@{@"rotate":@(model.angle)}];
        }
        if (model.isScaleChagned) {
            [transform addObject:@{@"scaleX":@(model.sx)}];
            [transform addObject:@{@"scaleY":@(model.sy)}];
        }
        if (model.isRotateXChanged) {
            [transform addObject:@{@"rotatex":@(model.rotateX)}];
        }
        if (model.isRotateYChanged) {
            [transform addObject:@{@"rotatey":@(model.rotateY)}];
        }
        if (model.isPerspectiveChanged) {
            [transform addObject:@{@"perspective":@(model.perspective)}];
        }
        styles[@"transform"] = transform;
    }
    if (model.isTransformOriginChanged) {
        styles[@"transformOrigin"] = model.transformOrigin;
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
    
    RCTExecuteOnUIManagerQueue(^{
        NSNumber *reactTag = [self getReactTagByRef:target];
        NSString* viewName = [_uiManager viewNameForReactTag:reactTag];
        RCTExecuteOnMainQueue(^{
            [_uiManager synchronouslyUpdateViewOnUIThread:reactTag viewName:viewName props:styles];
        });
    });
}

+ (UIPanGestureRecognizer *_Nullable)getPanGestureForComponent:(id _Nullable )source callback:(EBGetPanGestureCallback)callback
{
    UIView* view = [EBUtility getViewByRef:source];
    for (UIGestureRecognizer *obj in view.gestureRecognizers) {
        if ([obj  isKindOfClass:[UIPanGestureRecognizer class]]) {
            //            callback(
            //                     [view.events containsObject:@"horizontalpan"],
            //                     [view.events containsObject:@"verticalpan"]
            //                     );
            callback(true,true);
            return (UIPanGestureRecognizer *)obj;
        }
    }
    return nil;
}

+ (void)addScrollDelegate:(id<UIScrollViewDelegate>)delegate source:(id)source
{
    RCTExecuteOnMainQueue(^{
        RCTScrollView* view = (RCTScrollView *)[EBUtility getViewByRef:source];
        [view addScrollListener:delegate];
    });
}

+ (void)removeScrollDelegate:(id<UIScrollViewDelegate>)delegate source:(id)source
{
    RCTExecuteOnMainQueue(^{
        RCTScrollView* view = (RCTScrollView *)[EBUtility getViewByRef:source];
        [view removeScrollListener:delegate];
    });
}

+ (UIView *)getViewByRef:(id)source
{
    return [_uiManager viewForReactTag:[self getReactTagByRef:source]];
}

+ (NSNumber *)getReactTagByRef:(id)ref
{
    if ([ref isKindOfClass:NSNumber.class]) {
        return ref;
    } else if ([ref isKindOfClass:NSString.class]){
        return @([ref integerValue]);
    } else {
        return nil;
    }
}

#pragma clang diagnostic pop
@end
