//
//  EBTestUtils.h
//  BindingXTests
//
//  Created by 对象 on 2018/2/24.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EBExpression.h"
#import "EBExpressionScope.h"

@interface EBTestUtils : NSObject

+ (EBExpression *)expressionFromJSON:(NSString *)json;

@end
