//
//  WXNativeFunction.h
//  expression
//
//  Created by 寒泉 on 16/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WXJSCallable.h"
#import "WXJSObject.h"

#ifndef NativeFunction_h
#define NativeFunction_h


@interface WXNativeFunction<JSCallable>: WXJSObject
{
    NSObject* (^body)(NSArray* arguments);
}
-(id)initWithBody: (NSObject* (^)(NSArray*)) _body;
@end

#endif /* NativeFunction_h */

