//
//  WXExpression.h
//  expression
//
//  Created by 寒泉 on 08/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//


@interface WXExpression: NSObject
    {
        NSDictionary* root;
    }
    - (id)initWithRoot:(NSDictionary*) root;
    - (NSObject*) executeInScope:(NSDictionary *)scope;
@end

