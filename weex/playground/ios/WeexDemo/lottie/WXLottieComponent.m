/**
 * Created by Weex.
 * Copyright (c) 2016, Alibaba, Inc. All rights reserved.
 *
 * This source code is licensed under the Apache Licence 2.0.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

#import "WXLottieComponent.h"
#import "WXLottieBindingXHandler.h"
#import <Lottie/Lottie.h>

typedef void (^WXLottieCompletionBlock)(void);

@interface WXLottieView : UIView
@property (nonatomic, assign) BOOL loop;
@property (nonatomic, assign) BOOL autoplay;
@property (nonatomic, assign) CGFloat speed;
@property (nonatomic, assign) CGFloat progress;
@property (nonatomic, strong) NSDictionary *sourcejson;
@property (nonatomic, strong) NSString *sourceName;

- (void)play:(WXLottieCompletionBlock)completion;
- (void)pause;
- (void)reset;
- (void)setProgress:(CGFloat)progress;
- (void)setValue:(NSString *)keyPath atFrame:(NSNumber *)frame withColor:(UIColor *)color;

@end

@implementation WXLottieView
{
    LOTAnimationView *_animationView;
}
- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    if (_animationView != nil) {
        [_animationView setFrame:frame];
    }
}

- (void)play:(WXLottieCompletionBlock)completion
{
    if (_animationView != nil) {
        [_animationView playWithCompletion:^(BOOL animationFinished) {
            if (completion) {
                completion();
            }
        }];
    }
}

- (void)pause
{
    if (_animationView != nil) {
        [_animationView pause];
    }
}

- (void)reset
{
    if (_animationView) {
        _animationView.animationProgress = 0;
        [_animationView stop];
    }
}

-(void)setValue:(NSString *)keyPath atFrame:(NSNumber *)frame withColor:(UIColor *)color{
    
    if(_animationView != nil){
        //        [_animationView setValue:color forKeypath:keyPath atFrame:frame];
    }
    
}

- (void)setProgress:(CGFloat)progress
{
    _progress = progress;
    if (_animationView != nil) {
        _animationView.animationProgress = progress;
    }
}

- (void)setLoop:(BOOL)loop
{
    _loop = loop;
    if (_animationView != nil) {
        _animationView.loopAnimation = loop;
    }
}

- (void)setSourceName:(NSString *)src
{
    NSURL * url = [NSURL URLWithString:src];
    if (url.isFileURL) {
        // local url
        _sourceName = url.path;
        [self updateAnimationView:[LOTAnimationView animationNamed:_sourceName]];
    } else {
        // remote url
        WXResourceRequest * resourceRequest = [WXResourceRequest requestWithURL:url resourceType:WXResourceTypeLink referrer:@"" cachePolicy:NSURLRequestUseProtocolCachePolicy];
        WXResourceLoader * loader = [[WXResourceLoader alloc] initWithRequest:resourceRequest];
        __weak typeof(self) weakSelf = self;
        loader.onFinished = ^(const WXResourceResponse * response, NSData * data) {
            NSError * error = nil;
            NSHTTPURLResponse * httpResponse = (NSHTTPURLResponse*)response;
            if (200 == httpResponse.statusCode) {
                NSDictionary * dictionary = [WXUtility JSONObject:data error: &error];
                if (error) {
                    WXLogError(@"convert json object failed with error: %@", error.description);
                }
                [weakSelf setSourcejson:dictionary];
            } else {
                WXLogError(@"server return with status code: %ld", (long)httpResponse.statusCode);
            }
        };
        
        loader.onFailed = ^(NSError * error) {
            WXLogError(@"download src %@ failed with error: %@", url, error.description);
        };
        
        [loader start];
    }
}

- (void)setSpeed:(CGFloat)speed
{
    _speed = speed;
    if (_animationView != nil) {
        _animationView.animationSpeed = speed;
    }
}

- (void)setSourcejson:(NSDictionary *)sourcejson
{
    [self updateAnimationView:[LOTAnimationView animationFromJSON:sourcejson]];
}

- (void)updateAnimationView:(LOTAnimationView *)newAnimationView
{
    if (_animationView != nil) {
        [_animationView removeFromSuperview];
    }
    _animationView = newAnimationView;
    [self addSubview: newAnimationView];
    [_animationView setFrame:self.bounds];
    [self applyAnimationProperties];
}

- (void)applyAnimationProperties
{
    _animationView.animationProgress = _progress;
    _animationView.animationSpeed = _speed;
    _animationView.loopAnimation = _loop;
    if (_autoplay) {
        [_animationView play];
    }
}

@end

@interface WXLottieComponent()
@property (nonatomic, strong) NSDictionary *sourcejson;
@property (nonatomic) CGFloat progress;
@property (nonatomic) BOOL loop;
@property (nonatomic) BOOL autoplay;
@property (nonatomic) CGFloat speed;
@property (nonatomic, copy) NSString *src;
@property (nonatomic, copy) NSString *resize;
@end

@implementation WXLottieComponent
WX_PlUGIN_EXPORT_COMPONENT(lottie, WXLottieComponent)
WX_EXPORT_METHOD(@selector(play))
WX_EXPORT_METHOD(@selector(pause))
WX_EXPORT_METHOD(@selector(reset))
WX_EXPORT_METHOD(@selector(setValue:))
WX_EXPORT_METHOD(@selector(setProgress:))

- (instancetype)initWithRef:(NSString *)ref type:(NSString *)type styles:(NSDictionary *)styles attributes:(NSDictionary *)attributes events:(NSArray *)events weexInstance:(WXSDKInstance *)weexInstance
{
    if (self = [super initWithRef:ref type:type styles:styles attributes:attributes events:events weexInstance:weexInstance]) {
        _loop = NO;
        _sourcejson = nil;
        _src = nil;
        _progress = 0;
        _speed = 1.0;
        _autoplay = false;
        _resize = @"";
        [self fillLottieAttributes:attributes];
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [EBHandlerFactory addHandler:[WXLottieBindingXHandler new]];
    });
    
    return self;
}

- (void)play
{
    if (self.isViewLoaded) {
        [(WXLottieView*)self.view play:^() {
            [self fireEvent:@"complete" params:nil];
        }];
    }
}

- (void)pause
{
    if (self.isViewLoaded) {
        [(WXLottieView*)self.view pause];
    }
}

- (void)reset
{
    if (self.isViewLoaded) {
        [(WXLottieView*)self.view reset];
    }
}

- (void)setProgress:(CGFloat)progress
{
    if (self.isViewLoaded) {
        [(WXLottieView*)self.view setProgress:progress];
    }
}

- (void)setValue:(NSDictionary *)options{
    if(self.isViewLoaded){
        if(options[@"keyPath"] && options[@"frame"] && options[@"color"]){
            
            NSNumber *frame = @([WXConvert NSInteger:options[@"frame"]]);
            NSString *keyPath = [WXConvert NSString:options[@"keyPath"]];
            UIColor *color = [UIColor
                              colorWithRed: [WXConvert CGFloat:options[@"color"][@"r"]]
                              green: [WXConvert CGFloat:options[@"color"][@"g"]]
                              blue: [WXConvert CGFloat:options[@"color"][@"b"]]
                              alpha: 1.0
                              ];
            [(WXLottieView*)self.view setValue:keyPath atFrame:frame withColor:color];
        }
    }
}

- (void)fillLottieAttributes:(NSDictionary*)attributes
{
    if (attributes[@"sourcejson"]) {
        if ([attributes[@"sourcejson"] isKindOfClass:NSDictionary.class]) {
            _sourcejson = attributes[@"sourcejson"];
        } else if ([attributes[@"sourcejson"] isKindOfClass:NSString.class]) {
            _sourcejson = [WXUtility objectFromJSON:attributes[@"sourcejson"]];
        }
    }
    if (attributes[@"src"]) {
        _src = [WXConvert NSString:attributes[@"src"]];
        NSString * newURL = _src;
        WX_REWRITE_URL(_src, WXResourceTypeLink, self.weexInstance);
        _src = newURL;
    }
    if (attributes[@"progress"]) {
        _progress = [WXConvert CGFloat:attributes[@"progress"]];
    }
    if (attributes[@"loop"]) {
        _loop = [WXConvert BOOL:attributes[@"loop"]];
    }
    if (attributes[@"speed"]) {
        _speed = [WXConvert CGFloat:attributes[@"speed"]];
    }
    if (attributes[@"autoplay"]) {
        _autoplay = [WXConvert BOOL:attributes[@"autoplay"]];
    }
    if (attributes[@"resize"]) {
        _resize = [attributes[@"resize"] string];
    }
}

- (void)updateAttributes:(NSDictionary *)attributes
{
    [self fillLottieAttributes:attributes];
    WXLottieView * lottieView = (WXLottieView*)self.view;
    if (attributes[@"loop"]) {
        [lottieView setLoop:_loop];
    }
    if (attributes[@"sourcejson"]) {
        [lottieView setSourcejson:_sourcejson];
    }
    if (attributes[@"src"]) {
        NSString * newURL = _src;
        WX_REWRITE_URL(_src, WXResourceTypeLink, self.weexInstance);
        _src = newURL;
        [lottieView setSourceName:_src];
    }
    
    if (attributes[@"progress"]) {
        [lottieView setProgress:_progress];
    }
    if (attributes[@"speed"]) {
        [lottieView setSpeed:_speed];
    }
}

- (void)viewDidLoad
{
    WXLottieView * lottieView = (WXLottieView*)self.view;
    
    [lottieView setSourceName: _src];
    [lottieView setSpeed:_speed];
    [lottieView setProgress:_progress];
    [lottieView setSourcejson:_sourcejson];
    [lottieView setLoop:_loop];
    [lottieView setAutoplay:_autoplay];
}

- (UIView *)loadView
{
    return [WXLottieView new];
}

@end
