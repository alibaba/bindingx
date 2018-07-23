/**
 * Copyright 2018 Alibaba Group
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

#import "EBWXUtils.h"

@implementation EBWXUtils

+ (NSDictionary<NSString *,NSString *> *)cssPropertyMapping {
    static NSDictionary *map = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        map = @{@"width":@"width",
                @"height":@"height",
                @"color":@"color",
                @"opacity":@"opacity",
                @"backgroundColor":@"background-color",
                @"marginTop":@"margin-top",
                @"marginRight":@"margin-right",
                @"marginBottom":@"margin-bottom",
                @"marginLeft":@"margin-left",
                @"paddingTop":@"padding-top",
                @"paddingRight":@"padding-right",
                @"paddingBottom":@"padding-bottom",
                @"paddingLeft":@"padding-left",
                @"borderTopLeftRadius":@"border-top-left-radius",
                @"borderTopRightRadius":@"border-top-right-radius",
                @"borderBottomRightRadius":@"border-bottom-left-radius",
                @"borderBottomLeftRadius":@"border-bottom-right-radius",
                };
    });
    return map;
}

@end
