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

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [WXAppConfiguration setAppGroup:@"Binding"];
    [WXAppConfiguration setAppName:@"BindingPlayground"];
    [WXAppConfiguration setExternalUserAgent:@"ExternalUA"];
    [WXSDKEngine initSDKEnvironment];
    
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.window.backgroundColor = [UIColor whiteColor];
    NSString* path = [NSString stringWithFormat:@"file://%@/bundlejs/index.js?wh_weex=true",[NSBundle mainBundle].bundlePath];
    WXViewController *controller = [[WXViewController alloc] initWithURL:[NSURL URLWithString:path]];
    WXRootViewController *rootController = [[WXRootViewController alloc] initWithRootViewController:controller];
    self.window.rootViewController = rootController;
    [self.window makeKeyAndVisible];
    
    return YES;
}


@end
