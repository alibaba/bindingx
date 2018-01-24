//
//  WXNativeFunction.m
//  expression
//
//  Created by 寒泉 on 16/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WXNativeFunction.h"
@implementation WXNativeFunction : WXJSObject
-(id)initWithBody: (NSObject* (^)(NSArray*)) _body{
    if(self=[super init])
    {
        self->body = _body;
    }
    return self;
}

- (NSObject*) call:(NSArray *)arguments {
    return self->body(arguments);
}

@end
