//
//  BindingxWeexPluginModule.m
//  WeexPluginTemp
//
//  Created by  on 17/3/14.
//  Copyright © 2017年 . All rights reserved.
//

#import "BindingxWeexPluginModule.h"
#import <WeexPluginLoader/WeexPluginLoader.h>

@implementation BindingxWeexPluginModule

WX_PlUGIN_EXPORT_MODULE(bindingxWeexPlugin, BindingxWeexPluginModule)
WX_EXPORT_METHOD(@selector(show))

/**
 create actionsheet
 
 @param options items
 @param callback
 */
-(void)show
{
    UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:@"title" message:@"module bindingxWeexPlugin is created sucessfully" delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:@"ok", nil];
    [alertview show];
    
}

@end
