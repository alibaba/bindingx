//
//  WXExecutor.m
//  Binding
//
//  Created by 对象 on 2018/1/7.
//

#import "EBUtility+WX.h"
#import <WeexSDK/WeexSDK.h>

void PerformBlockOnBridgeThread(void (^block)(void))
{
    WXPerformBlockOnBridgeThread(^{
        block();
    });
}

void PerformBlockOnMainThread(void (^block)(void))
{
    WXPerformBlockOnMainThread(^{
        block();
    });
}

@implementation EBUtility (WX)

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"

+ (void)execute:(NSDictionary *)styles to:(id)target
{
    if (styles.count > 0) {
        WXComponent* component = nil;
        if ([target isKindOfClass:WXComponent.class]) {
            component = (WXComponent *)target;
        }
        
        SEL componentSel = NSSelectorFromString(@"componentManager");
        if (![component.weexInstance respondsToSelector:componentSel]) {
            return;
        }
        
        typedef WXComponentManager *(*send_type)(id, SEL);
        send_type methodInstance = (send_type)[WXSDKInstance instanceMethodForSelector:componentSel];
        WXComponentManager *componentManager = methodInstance(component.weexInstance, componentSel);
        
        if (componentManager) {
            if (styles.count > 0){
                WXPerformBlockOnMainThread(^{
                    [componentManager handleStyleOnMainThread:[styles copy] forComponent:target isUpdateStyles:YES];
                });
            }
        }
    }
    
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

+ (CGFloat)factor
{
    return [WXUtility defaultPixelScaleFactor];
}

+ (BOOL)hasHorizontalPan:(id)target
{
    WXComponent* component = (WXComponent*)target;
    if ([component isKindOfClass:WXComponent.class]) {
        return [component.events containsObject:@"horizontalpan"];
    }
    return NO;
}

+ (BOOL)hasVerticalPan:(id)target
{
    WXComponent* component = (WXComponent*)target;
    if ([component isKindOfClass:WXComponent.class]) {
        return [component.events containsObject:@"verticalpan"];
    }
    return NO;
}

+ (UIPanGestureRecognizer *_Nullable)getPanGestureForComponent:(id _Nullable )source callback:(EBGetPanGestureCallback)callback
{
    WXComponent *component = (WXComponent *)source;
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
        callback(
                 [component.events containsObject:@"horizontalpan"],
                 [component.events containsObject:@"verticalpan"]
                 );
        return (UIPanGestureRecognizer *)panObj;
    }
    return nil;
}

+ (void)addScrollDelegate:(id)delegate source:(id)source
{
    [source addScrollDelegate:delegate];
}

+ (void)removeScrollDelegate:(id)delegate source:(id)source
{
    [source removeScrollDelegate:delegate];
}

#pragma clang diagnostic pop
@end
