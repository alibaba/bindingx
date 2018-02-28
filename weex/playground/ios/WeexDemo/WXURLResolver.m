/**
 * Created by Weex.
 * Copyright (c) 2016, Alibaba, Inc. All rights reserved.
 *
 * This source code is licensed under the Apache Licence 2.0.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

#import "WXURLResolver.h"
#import "NSString+Additions.h"
#import <WeexSDK/WeexSDK.h>

@implementation WXURLResolver

+ (BOOL)isLocalFileURL:(NSURL*) URL {
    return [[URL scheme] isEqualToString:@"file"];
}

+ (NSData *)getLocalFileString:(NSURL*)URL
{
    if ([self isLocalFileURL:URL]) {
        return [NSData dataWithContentsOfURL:URL];
    }
    return nil;
}


+ (BOOL)isValidWeexURL:(NSURL*) URL {
    NSMutableURLRequest* urlRequest = nil;
    WXRequestTemplateMode requestTmpMode = WXRequestTemplateMode_Unknown;
    return [self isValidWeexURL:URL request:&urlRequest mode:&requestTmpMode];
}

+ (BOOL)isValidWeexURL:(NSURL*) URL request:(NSMutableURLRequest**)request mode:(WXRequestTemplateMode*)mode {
    *mode = WXRequestTemplateMode_Unknown;
    *request = nil;
    
    NSString *newUrlStr = URL.absoluteString;
    if([URL.scheme isEqualToString:@"wxpage"]) {
        newUrlStr = [newUrlStr stringByReplacingOccurrencesOfString:@"wxpage://" withString:@"http://"];
        *mode = WXRequestTemplateMode_WxpageScheme;
        NSURL* newUrl = [NSURL URLWithString:newUrlStr];
        if (newUrl)
            *request = [NSMutableURLRequest requestWithURL:newUrl];
        *request = [NSMutableURLRequest requestWithURL:URL];
    } else {
        NSDictionary* queryDictionary = [[URL query] queryDictionaryUsingEncoding:NSUTF8StringEncoding];
        NSString* tplUrlStr = [queryDictionary objectForKey:@"_wx_tpl"];
        if ([queryDictionary[@"hybrid"] isEqualToString:@"true"]) {
            //nothing
        }
        if (tplUrlStr && ![tplUrlStr isEqualToString:@""]) {
            *mode = WXRequestTemplateMode_Standard;
            NSURL* tplUrl = [NSURL URLWithString:tplUrlStr];
            if (tplUrl)
                *request = [NSMutableURLRequest requestWithURL:tplUrl];
        }
        else if ([[queryDictionary objectForKey:@"wh_weex"] isEqualToString:@"true"]) {
            *mode = WXRequestTemplateMode_Wh;
            *request = [NSMutableURLRequest requestWithURL:URL];
        }
    }
    
    return *request != nil;
}

+ (BOOL)needNavbarHiddenOfWeexURL:(NSURL *)URL {
    NSDictionary* queryDictionary = [[URL query] queryDictionaryUsingEncoding:NSUTF8StringEncoding];
    if([queryDictionary[@"wx_navbar_hidden"] isEqualToString:@"true"]) {
        return YES;
    }
    return NO;
}

+ (BOOL)needNavbarTransparentOfWeexURL:(NSURL *)URL {
    NSDictionary* queryDictionary = [[URL query] queryDictionaryUsingEncoding:NSUTF8StringEncoding];
    if([queryDictionary[@"wx_navbar_transparent"] isEqualToString:@"true"]) {
        return YES;
    }
    return NO;
}

+ (NSString *)orientation:(NSURL *)URL {
    NSDictionary* queryDictionary = [[URL query] queryDictionaryUsingEncoding:NSUTF8StringEncoding];
    return queryDictionary[@"wx_orientation"];
}

+ (BOOL)sendTplRequestWithNavigatorURL:(NSURL*) URL
                     completionHandler:(void (^)(BOOL successful, NSURLRequest *request,  NSString* tplData, NSURLResponse* response))handler {
    NSMutableURLRequest* urlRequest = nil;
    WXRequestTemplateMode requestTmpMode = WXRequestTemplateMode_Unknown;
    
    BOOL isWeexURL = [self isValidWeexURL:URL request:&urlRequest mode:&requestTmpMode];
    
    if (isWeexURL && urlRequest) {
        [urlRequest setTimeoutInterval:30];
        [urlRequest setValue:[WXUtility userAgent] forHTTPHeaderField:@"User-Agent"];
        [urlRequest setValue:@"weex" forHTTPHeaderField:@"f-refer"];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
            
            if ([response isKindOfClass:[NSHTTPURLResponse class]] && ((NSHTTPURLResponse *)response).statusCode != 200) {
                WX_MONITOR_FAIL_ON_PAGE(WXMTJSDownload, WX_ERR_JSBUNDLE_DOWNLOAD, connectionError.description, urlRequest.URL.absoluteString);
                handler(NO, urlRequest, nil, nil);
                return ;
            }
            
            NSString* tplData = nil;
            if (connectionError == nil) {
                switch (requestTmpMode) {
                    case WXRequestTemplateMode_Standard:
                    {
                        tplData = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                    }
                        break;
                    case WXRequestTemplateMode_Wh:
                    {
                        NSString* contentType = [[(NSHTTPURLResponse*)response allHeaderFields] valueForKey:@"Content-Type"];
                        if (contentType && [contentType containsString:@"application/javascript"]) {
                            tplData = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                        }
                    }
                        break;
                    default:
                        NSCAssert(NO, @"");
                        break;
                }
                
                if(!tplData) {
                    WX_MONITOR_FAIL_ON_PAGE(WXMTJSDownload, WX_ERR_JSBUNDLE_DOWNLOAD, @"Template data is empty.", URL.absoluteString);
                }
                else {
                    WX_MONITOR_SUCCESS_ON_PAGE(WXMTJSDownload, URL.absoluteString);
                }
            }
            else {
                WX_MONITOR_FAIL_ON_PAGE(WXMTJSDownload, WX_ERR_JSBUNDLE_DOWNLOAD, connectionError.description, URL.absoluteString);
            }
            handler(tplData != nil, urlRequest, tplData, response);
        }];
        return YES;
    }
    else {
        handler(NO, urlRequest, nil, nil);
        return NO;
    }
}

+ (NSString *)getWXtpl:(NSURL*)URL
{
    NSString *url = URL.absoluteString;
    if ([url containsString:@".html"]) {
        url = [url stringByReplacingOccurrencesOfString:@".html" withString:@".js"];
    }
    
    if ([URL.scheme isEqualToString:@"file"]) {
        return url;
    }
    
    NSURLRequest* urlRequest = nil;
    WXRequestTemplateMode requestTmpMode = WXRequestTemplateMode_Unknown;
    BOOL isValidURL = [self isValidWeexURL:[NSURL URLWithString:url] request:&urlRequest mode:&requestTmpMode];
    if (isValidURL) {
        return urlRequest.URL.absoluteString;
    }else{
        return url;
    }
    
}

+ (NSURL *)getAbsoluteURL:(NSString *)url currentURL:(NSURL *)currentURL
{
    if ([url hasPrefix:@"file:///"] || [url hasPrefix:@"http"]) {
        return [NSURL URLWithString:url];
    } else if([url hasPrefix:@"//"]) {
        return [NSURL URLWithString:[NSString stringWithFormat:@"http:%@", url]];
    } else {
        return [NSURL URLWithString:url relativeToURL:currentURL];
    }
}

#pragma mark
#pragma mark debug

+ (BOOL)handleWeexDebugger:(NSURL *)url {
    NSDictionary* queryDictionary = [[url query] queryDictionaryUsingEncoding:NSUTF8StringEncoding];
    NSString* debugUrlStr = [queryDictionary objectForKey:@"_wx_debug"];
    NSString *devToolStr = [queryDictionary objectForKey:@"_wx_devtool"];
    if (debugUrlStr && debugUrlStr.length > 0) {
        [WXSDKEngine connectDebugServer:debugUrlStr];
        return YES;
    }else if (devToolStr && devToolStr.length > 0) {
        SEL selector = NSSelectorFromString(@"launchDevToolDebugWithUrl:");
        Class WXDevTool = NSClassFromString(@"WXDevTool");
        if (!WXDevTool || ![WXDevTool methodForSelector:selector]) {
            return NO;
        }
        IMP imp = [WXDevTool methodForSelector:selector];
        void (*func)(id, SEL, NSString *str) = (void *)imp;
        func(WXDevTool, selector, devToolStr);
        return YES;
    }
    return NO;
}

+ (BOOL)weexReplaceJS:(NSURL*)url {
    NSString * path = [url relativePath];
    NSString * host = [url host];
    if (!path || ![host isEqualToString:@"weex-remote-debugger"]){
        return NO;
    }
    //replace bundle js path : /dynamic/replace/bundle
    if ([path isEqualToString:@"/dynamic/replace/bundle"]){
        for (NSString * param in [[url query] componentsSeparatedByString:@"&"]) {
            NSArray* elts = [param componentsSeparatedByString:@"="];
            if ([elts count] < 2) {
                continue;
            }
            if ([[elts firstObject] isEqualToString:@"bundle"]){
                [WXDebugTool setReplacedBundleJS:[NSURL URLWithString:[elts lastObject]]];
            }
        }
    }
    
    //replace jsframework path:/dynamic/replace/framework
    if ([path isEqualToString:@"/dynamic/replace/framework"]){
        for (NSString * param in [[url query] componentsSeparatedByString:@"&"]) {
            NSArray* elts = [param componentsSeparatedByString:@"="];
            if ([elts count] < 2) {
                continue;
            }
            if ([[elts firstObject] isEqualToString:@"framework"]){
                [WXDebugTool setReplacedJSFramework:[NSURL URLWithString:[elts lastObject]]];
            }
        }
    }
    return YES;
}

@end
