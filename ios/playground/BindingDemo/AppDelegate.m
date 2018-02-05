//
//  AppDelegate.m
//  BindingDemo
//
//  Created by 对象 on 2018/1/4.
//  Copyright © 2018年 Alibaba. All rights reserved.
//

#import "AppDelegate.h"
#import <WeexSDK/WeexSDK.h>
#import "WXViewController.h"
//#import <Binding/WXEBModule.h>

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [WXAppConfiguration setAppGroup:@"Binding"];
    [WXAppConfiguration setAppName:@"BindingPlayground"];
    [WXAppConfiguration setExternalUserAgent:@"ExternalUA"];
    [WXSDKEngine initSDKEnvironment];
    
//    [WXSDKEngine registerModule:@"binding" withClass:[WXEBModule class]];
    
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.window.backgroundColor = [UIColor whiteColor];
//    NSString* path = [NSString stringWithFormat:@"file://%@/bundlejs/index.js?wh_weex=true",[NSBundle mainBundle].bundlePath];
//    NSString* path = @"http://dotwe.org/raw/dist/a02b3b3d6e0983be733b3bb2f1e4140f.bundle.wx?_wx_tpl=http://dotwe.org/raw/dist/a02b3b3d6e0983be733b3bb2f1e4140f.bundle.wx";//timing
    
//    NSString* path = @"http://dotwe.org/raw/dist/19229fdaff971b2e29682d4c3d13f29d.bundle.wx?_wx_tpl=http://dotwe.org/raw/dist/19229fdaff971b2e29682d4c3d13f29d.bundle.wx";//pan
    
//    NSString* path = @"http://rax.alibaba-inc.com/bundle/ce009739-b14d-4a06-b239-7977452c5232/bundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=http://rax.alibaba-inc.com/bundle/ce009739-b14d-4a06-b239-7977452c5232/bundle.js";//scroller
    
    NSString* path = @"http://rax.alibaba-inc.com/bundle/178b55d8-f64f-434f-89b0-007ea5740d8a/bundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=http://rax.alibaba-inc.com/bundle/178b55d8-f64f-434f-89b0-007ea5740d8a/bundle.js";//gyro
    
    WXViewController *controller = [[WXViewController alloc] initWithURL:[NSURL URLWithString:path]];
    WXRootViewController *rootController = [[WXRootViewController alloc] initWithRootViewController:controller];
    self.window.rootViewController = rootController;
    [self.window makeKeyAndVisible];
    
    
    
    
    return YES;
}


@end
