//
//  WXJSEvaluate.m
//  Pods
//
//  Created by 对象 on 2017/8/17.
//
//

#import "WXJSEvaluate.h"

@implementation WXJSEvaluate

+(WXNativeFunction *)evaluateColor
{
    return [[WXNativeFunction alloc] initWithBody:^(NSArray *arguments){
        
        NSString* startColor = [arguments[0] substringWithRange:NSMakeRange(2, 6)];
        NSString* endColor = [arguments[1] substringWithRange:NSMakeRange(2, 6)];
        double fraction = [arguments[2] doubleValue];
        if (fraction < 0) {
            fraction = 0;
        } else if(fraction > 1) {
            fraction = 1;
        }
        
        unsigned rgbValue = 0;
        [[NSScanner scannerWithString:[@"0x" stringByAppendingString:startColor]] scanHexInt:&rgbValue];
        float sr = (float)((rgbValue & 0xFF0000) >> 16);
        float sg = (float)((rgbValue & 0xFF00) >> 8);
        float sb = (float)(rgbValue & 0xFF);
        
        
        [[NSScanner scannerWithString:[@"0x" stringByAppendingString:endColor]] scanHexInt:&rgbValue];
        float er = (float)((rgbValue & 0xFF0000) >> 16);
        float eg = (float)((rgbValue & 0xFF00) >> 8);
        float eb = (float)(rgbValue & 0xFF);
        
        NSNumber* mr = [[NSNumber alloc] initWithDouble:((er - sr)*fraction + sr)];
        NSNumber* mg = [[NSNumber alloc] initWithDouble:((eg - sg)*fraction + sg)];
        NSNumber* mb = [[NSNumber alloc] initWithDouble:((eb - sb)*fraction + sb)];
        
        return @[mr,mg,mb];
        
    }];
}



@end
