/**
 * Created by Weex.
 * Copyright (c) 2016, Alibaba, Inc. All rights reserved.
 *
 * This source code is licensed under the Apache Licence 2.0.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

#import <Foundation/Foundation.h>

typedef enum {
    WXRequestTemplateMode_Unknown,
    WXRequestTemplateMode_Standard,
    WXRequestTemplateMode_Wh,
    WXRequestTemplateMode_WxpageScheme,
}WXRequestTemplateMode;

@interface WXURLResolver: NSObject

+ (BOOL)isValidWeexURL:(NSURL*)URL;
+ (BOOL)isValidWeexURL:(NSURL*)URL request:(NSMutableURLRequest**)request mode:(WXRequestTemplateMode*)mode;
+ (BOOL)needNavbarHiddenOfWeexURL:(NSURL*)URL;
+ (BOOL)needNavbarTransparentOfWeexURL:(NSURL *)URL;
+ (NSString *)orientation:(NSURL *)URL;
+ (BOOL)sendTplRequestWithNavigatorURL:(NSURL*)URL completionHandler:(void (^)(BOOL successful, NSURLRequest *request, NSString* tplData, NSURLResponse* response)) handler;  
+ (NSString *)getWXtpl:(NSURL*)URL;
+ (BOOL)handleWeexDebugger:(NSURL *)url;
+ (BOOL)weexReplaceJS:(NSURL*)url;

+ (BOOL)isLocalFileURL:(NSURL*)URL;
+ (NSData *)getLocalFileString:(NSURL*)URL;

+ (NSURL *)getAbsoluteURL:(NSString *)url currentURL:(NSURL *)currentURL;
@end
