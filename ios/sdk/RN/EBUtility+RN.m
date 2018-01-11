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

void PerformBlockOnBridgeThread(void (^ _Nonnull block)(void))
{
    block();
    //    WXPerformBlockOnBridgeThread(^{
    //        block();
    //    });
}

void PerformBlockOnMainThread(void (^ _Nonnull block)(void))
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

+ (void)execute:(NSDictionary *)style to:(id)target
{
    RCTExecuteOnUIManagerQueue(^{
        NSString* viewName = [_uiManager viewNameForReactTag:target];
//            [_uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
//                [_uiManager synchronouslyUpdateViewOnUIThread:target viewName:viewName props:style];
//            }];
        
        RCTExecuteOnMainQueue(^{
            [_uiManager synchronouslyUpdateViewOnUIThread:target viewName:viewName props:style];
        });
    });
}

#pragma clang diagnostic pop
@end
