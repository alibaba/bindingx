//
//  WXJSTransform.h
//  Pods
//
//  Created by xiayun on 16/12/22.
//
//

#import <Foundation/Foundation.h>
#import "WXNativeFunction.h"

@interface WXJSTransform : NSObject

+ (WXNativeFunction *)translate;
+ (WXNativeFunction *)scale;
+ (WXNativeFunction *)matrix;
+ (WXNativeFunction *)asArray;

@end
