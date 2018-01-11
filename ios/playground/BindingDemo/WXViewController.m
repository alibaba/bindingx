/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

#import "WXViewController.h"

@interface WXViewController () <UIWebViewDelegate,UINavigationControllerDelegate>
@property (nonatomic, strong) UIView *weexView;
@property (nonatomic, strong) UIWebView *webView;

@property (nonatomic, assign) CGFloat weexHeight;
@property (nonatomic, assign) CGFloat weexY;
@property (nonatomic, assign) BOOL hideNavBar;
@property (nonatomic, strong) WXErrorView *errorView;

@end

@implementation WXViewController

- (instancetype)initWithURL:(NSURL*)url
{
    if (self = [super init]) {
        self.url = url;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRefreshInstance:) name:@"RefreshInstance" object:nil];
    
    BOOL navBarTransparent = NO;
    _weexY = 0;
    if (_hideNavBar) {
        _weexHeight = [UIScreen mainScreen].bounds.size.height;
    }else{
        if (navBarTransparent) {
            [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
            _weexY = 20;
            _weexHeight = [UIScreen mainScreen].bounds.size.height - 20;
        } else {
            _weexHeight = [UIScreen mainScreen].bounds.size.height - 64;
        }
    }
    [self.navigationController.navigationBar setTranslucent:navBarTransparent];
    
    if (self.navigationController.viewControllers.count == 1) {
        self.navigationController.delegate = self;
    }
    [self render];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self updateInstanceState:WeexInstanceAppear];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self updateInstanceState:WeexInstanceDisappear];
}

- (void)dealloc
{
    
    [_instance destroyInstance];
#ifdef DEBUG
    [_instance forceGarbageCollection];
#endif
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)render
{
    
    [self renderWeex:[_url absoluteString]];
}

- (void)renderWeex: (NSString *)url{
    CGFloat width = self.view.frame.size.width;
    [_instance destroyInstance];
    _instance = [[WXSDKInstance alloc] init];
    if([WXPrerenderManager isTaskExist:[self.url absoluteString]]){
        _instance = [WXPrerenderManager instanceFromUrl:self.url.absoluteString];
    }
    
    _instance.viewController = self;
    _instance.frame = CGRectMake(self.view.frame.size.width-width, _weexY, width, _weexHeight);
    
    __weak typeof(self) weakSelf = self;
    _instance.onCreate = ^(UIView *view) {
        [weakSelf.weexView removeFromSuperview];
        weakSelf.weexView = view;
        [weakSelf.view addSubview:weakSelf.weexView];
        UIAccessibilityPostNotification(UIAccessibilityScreenChangedNotification, weakSelf.weexView);
    };
    
    _instance.onFailed = ^(NSError *error){
        NSString *domain = [NSString stringWithFormat:@"%ld", (long)TemplateErrorType];
        if ([error.domain isEqualToString:WX_ERROR_DOMAIN] && error.code == WX_ERR_NOT_CONNECTED_TO_INTERNET) {
            if (weakSelf.errorView) {
                return ;
            }
            WXErrorView *errorView = [[WXErrorView alloc]initWithFrame:CGRectMake(0.0f, 0.0f, 135.0f, 130.0f)];
            errorView.center = CGPointMake(weakSelf.view.bounds.size.width / 2.0f, weakSelf.view.bounds.size.height / 2.0f);
            errorView.delegate = weakSelf;
            [weakSelf.view addSubview:errorView];
            weakSelf.errorView = errorView;
        } else if ([error.domain isEqualToString:domain] || [error.domain isEqualToString:WX_ERROR_DOMAIN]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf degradeToH5];
            });
        }
    };
    
    _instance.renderFinish = ^(UIView *view) {
        WXLogDebug(@"%@", @"Render Finish...");
        [weakSelf updateInstanceState:WeexInstanceAppear];
    };
    
    _instance.updateFinish = ^(UIView *view) {
        WXLogDebug(@"%@", @"Update Finish...");
    };
    if (!self.url) {
        WXLogError(@"error: render url is nil");
        return;
    }
    if([WXPrerenderManager isTaskExist:[self.url absoluteString]]){
        WX_MONITOR_INSTANCE_PERF_START(WXPTJSDownload, _instance);
        WX_MONITOR_INSTANCE_PERF_END(WXPTJSDownload, _instance);
        WX_MONITOR_INSTANCE_PERF_START(WXPTFirstScreenRender, _instance);
        WX_MONITOR_INSTANCE_PERF_START(WXPTAllRender, _instance);
        [WXPrerenderManager renderFromCache:[self.url absoluteString]];
        return;
    }
    
    NSURL* URL = [NSURL URLWithString:url];
    NSString *randomURL = [NSString stringWithFormat:@"%@%@random=%d",URL.absoluteString,URL.query?@"&":@"?",arc4random()];
    [_instance renderWithURL:[NSURL URLWithString:randomURL] options:@{@"bundleUrl":URL.absoluteString} data:nil];
}

- (void)degradeToH5 {
    if (!_webView) {
        CGRect frame = CGRectMake(0, _weexY, self.view.frame.size.width, _weexHeight);
        _webView = [[UIWebView alloc] initWithFrame:frame];
        //        [(UIScrollView *)[[_webView subviews] objectAtIndex:0] setBounces:NO];
        [self.view addSubview:_webView];
    }
    
    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:_url];
    [request setValue:@"Boat Degrade" forHTTPHeaderField:@"Referer"];
    [_webView loadRequest:request];
}

- (void)updateInstanceState:(WXState)state
{
    if (_instance && _instance.state != state) {
        _instance.state = state;
        
        if (state == WeexInstanceAppear) {
            [[WXSDKManager bridgeMgr] fireEvent:_instance.instanceId ref:WX_SDK_ROOT_REF type:@"viewappear" params:nil domChanges:nil];
        }
        else if (state == WeexInstanceDisappear) {
            [[WXSDKManager bridgeMgr] fireEvent:_instance.instanceId ref:WX_SDK_ROOT_REF type:@"viewdisappear" params:nil domChanges:nil];
        }
    }
}

#pragma mark - refresh
- (void)refresh
{
    [self render];
}

#pragma mark - notification
- (void)notificationRefreshInstance:(NSNotification *)notification {
    [self refresh];
}

- (void)onclickErrorView
{
    if (self.errorView) {
        [self.errorView removeFromSuperview];
        self.errorView = nil;
    }
    [self render];
}

#pragma mark - UINavigationControllerDelegate
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    WXViewController *wxViewController = (WXViewController *)viewController;
    if ([wxViewController isKindOfClass:[self class]]) {
        [wxViewController.navigationController setNavigationBarHidden:wxViewController.hideNavBar animated:YES];
    }
}

@end

