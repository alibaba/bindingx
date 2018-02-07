/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "EBJSEvaluate.h"

@implementation EBJSEvaluate

+(EBNativeFunction *)evaluateColor
{
    return [[EBNativeFunction alloc] initWithBody:^(NSArray *arguments){
        
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
