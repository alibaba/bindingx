//
//  NSObject+WXTuplePacker.m
//  TestTuple
//
//  Created by xiayun on 16/12/20.
//  Copyright © 2016年 Taobao. All rights reserved.
//

#import "NSObject+WXTuplePacker.h"
#import <UIKit/UIKit.h>

@implementation NSObject (WXTuplePacker)

#pragma mark - WXTaffyPacker
- (NSEnumerator *)wxtf_pack {
    if ([self isKindOfClass:[NSNumber class]] || [self isKindOfClass:[NSString class]]) {
        return [@[self] objectEnumerator];
    } else if ([self isKindOfClass:[NSArray class]]) {
        return [(NSArray *)self objectEnumerator];
    }
    return nil;
}

@end
