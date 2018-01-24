//
//  WXJSFunction.h
//  expression
//
//  Created by 寒泉 on 08/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WXJSCallable.h"
#import "WXJSObject.h"

@interface WXJSFunction: WXJSObject

- (NSObject*) call:(NSArray *)arguments;

@end


