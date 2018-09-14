/**
 * Created by Weex.
 * Copyright (c) 2016, Alibaba, Inc. All rights reserved.
 *
 * This source code is licensed under the Apache Licence 2.0.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

#import "WXLottieBindingXHandler.h"
#import "WXLottieComponent.h"

@implementation WXLottieBindingXHandler

- (void)execute:(NSDictionary *)properties to:(id)target {
    if ([target isKindOfClass:WXLottieComponent.class]) {
        id progress = properties[@"lottie-progress"];
        if (progress) {
            WXLottieComponent *lottie = (WXLottieComponent *)target;
            [lottie setProgress:[WXConvert CGFloat:progress]];
        }
    }
}

@end
