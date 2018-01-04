//
//  WXJSCallable.h
//  expression
//
//  Created by 寒泉 on 16/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#ifndef JSCallable_h
#define JSCallable_h


@protocol WXJSCallable <NSObject>

- (NSObject*) call:(NSArray *)arguments;

@end
#endif /* JSCallable_h */
