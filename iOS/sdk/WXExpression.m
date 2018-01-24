//
//  WXExpression.m
//  expression
//
//  Created by 寒泉 on 08/12/2016.
//  Copyright © 2016 寒泉. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <math.h>
#import "WXExpression.h"
#import "WXJSFunction.h"
#import "WXJSCallable.h"

@implementation WXExpression
-(id)initWithRoot:(NSDictionary*) _root {
    if(self=[super init])
    {
        root = _root;
    }
    return self;  
}

- (NSObject*) executeInScope:(NSDictionary *)scope {
    return [self executeNodeInScope:scope node:root];
}

- (double) toNumber:(NSObject*) value {
    if([value isKindOfClass:[NSNumber class]]) {
        return [(NSNumber*)value doubleValue];
    }
    if([value isKindOfClass:[NSString class]]) {
        return [(NSString *)value doubleValue];
    }
    return NAN;
}

- (BOOL) toBool:(NSObject*) value {
    if([value isKindOfClass:[NSNumber class]]) {
        return [(NSNumber*)value boolValue] > 0;
    }
    if([value isKindOfClass:[NSString class]]) {
        return [(NSString *)value boolValue] > 0;
    }
    return false;
}

- (NSNumber*) toBoolean:(NSObject*) value {
    if([value isKindOfClass:[NSNumber class]]) {
        return @([(NSNumber*)value boolValue]);
    }
    if([value isKindOfClass:[NSString class]]) {
        return @([(NSString *)value boolValue]);
    }
    return nil;
}

- (NSObject*) executeNodeInScope:(NSDictionary *)scope node:(NSDictionary*) node {
    NSString* type = [node objectForKey:@"type"];
    NSArray* children = [node objectForKey:@"children"];
    if([type isEqualToString:@"Identifier"]) {
        return [scope objectForKey:[node objectForKey:@"value"]];
    } else if([type isEqualToString:@"StringLiteral"]) {
        return [node objectForKey:@"value"];
    } else if([type isEqualToString:@"NumericLiteral"]) {
        return [node objectForKey:@"value"];
    } else if([type isEqualToString:@"BooleanLiteral"]) {
        return [node objectForKey:@"value"];
    } else if([type isEqualToString:@"CallExpression"]) {
        NSArray* argumentNodes = [children[1] objectForKey:@"children"];
        NSMutableArray* arguments = [[NSMutableArray alloc] init];
        for(int i = 0; i < [argumentNodes count]; i++)
            arguments[i] = [self executeNodeInScope:scope node:argumentNodes[i]];
        return [(NSObject<WXJSCallable>*)[self executeNodeInScope:scope node:children[0]] call:arguments];
    } else if([type isEqualToString:@"?"]) {
        if([self toBool:[self executeNodeInScope:scope node:children[0]]]){
            return [self executeNodeInScope:scope node:children[1]];
        }
        else{
            return [self executeNodeInScope:scope node:children[2]];
        }
    }  else if([type isEqualToString:@"+"]) {
        return [[NSNumber alloc] initWithDouble: ([self toNumber:[self executeNodeInScope:scope node:children[0]]] + [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    } else if([type isEqualToString:@"-"]) {
        return [[NSNumber alloc] initWithDouble: ([self toNumber:[self executeNodeInScope:scope node:children[0]]] - [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    } else if([type isEqualToString:@"*"]) {
        return [[NSNumber alloc] initWithDouble: ([self toNumber:[self executeNodeInScope:scope node:children[0]]] * [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    } else if([type isEqualToString:@"/"]) {
        return [[NSNumber alloc] initWithDouble: ([self toNumber:[self executeNodeInScope:scope node:children[0]]] / [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    } else if([type isEqualToString:@"%"]) {
        return [[NSNumber alloc] initWithDouble: fmod([self toNumber:[self executeNodeInScope:scope node:children[0]]], [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    }  else if([type isEqualToString:@"**"]) {
        return [[NSNumber alloc] initWithDouble: pow([self toNumber:[self executeNodeInScope:scope node:children[0]]], [self toNumber:[self executeNodeInScope:scope node:children[1]]])];
    } else if ([type isEqualToString:@">"]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 > num2);
    } else if ([type isEqualToString:@">="]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 >= num2);
    } else if ([type isEqualToString:@"<"]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 < num2);
    } else if ([type isEqualToString:@"<="]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 <= num2);
    } else if ([type isEqualToString:@"=="]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 == num2);
    } else if ([type isEqualToString:@"!="]) {
        CGFloat num1 = [self toNumber:[self executeNodeInScope:scope node:children[0]]];
        CGFloat num2 = [self toNumber:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 != num2);
    } else if ([type isEqualToString:@"||"]) {
        NSNumber *num1 = [self toBoolean:[self executeNodeInScope:scope node:children[0]]];
        NSNumber *num2 = [self toBoolean:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 || num2);
    } else if ([type isEqualToString:@"&&"]) {
        NSNumber *num1 = [self toBoolean:[self executeNodeInScope:scope node:children[0]]];
        NSNumber *num2 = [self toBoolean:[self executeNodeInScope:scope node:children[1]]];
        return @(num1 && num2);
    }
    
    return NULL;
}
@end
