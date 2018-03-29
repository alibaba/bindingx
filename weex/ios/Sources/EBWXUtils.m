//
//  EBWXUtils.m
//  BindingX
//
//  Created by 对象 on 2018/3/29.
//

#import "EBWXUtils.h"

@implementation EBWXUtils

+ (NSDictionary<NSString *,NSString *> *)cssPropertyMapping {
    static NSDictionary *map = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        map = @{@"width":@"width",
                @"height":@"height",
                @"marginTop":@"margin-top",
                @"marginRight":@"margin-right",
                @"marginBottom":@"margin-bottom",
                @"marginLeft":@"margin-left",
                @"paddingTop":@"margin-top",
                @"paddingRight":@"margin-right",
                @"paddingBottom":@"margin-bottom",
                @"paddingLeft":@"margin-left",
                @"borderTopLeftRadius":@"border-top-left-radius",
                @"borderTopRightRadius":@"border-top-right-radius",
                @"borderBottomRightRadius":@"border-bottom-left-radius",
                @"borderBottomLeftRadius":@"border-bottom-right-radius",
                };
    });
    return map;
}

@end
