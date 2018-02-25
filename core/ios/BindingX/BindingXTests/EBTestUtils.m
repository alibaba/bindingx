//
//  EBTestUtils.m
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import "EBTestUtils.h"
#import "EBExpression.h"

@implementation EBTestUtils

+ (EBExpression *)expressionFromJSON:(NSString *)json
{
    NSDictionary *exitExpression = [NSJSONSerialization JSONObjectWithData:[json dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
    return [[EBExpression alloc] initWithRoot:exitExpression];
}

@end
