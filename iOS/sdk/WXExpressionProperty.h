//
//  WXExpressionProperty.h
//  Pods
//
//  Created by xiayun on 16/12/23.
//
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface WXExpressionProperty : NSObject

@property (nonatomic, assign) BOOL isTransformChanged;
@property (nonatomic, assign) BOOL isTranslateChanged;
@property (nonatomic, assign) BOOL isRotateChanged;
@property (nonatomic, assign) BOOL isScaleChagned;

@property (nonatomic, assign) CGFloat tx;
@property (nonatomic, assign) CGFloat ty;
@property (nonatomic, assign) CGFloat sx;
@property (nonatomic, assign) CGFloat sy;
@property (nonatomic, assign) CGFloat angle;

@property (nonatomic, assign) BOOL isLeftChanged;
@property (nonatomic, assign) BOOL isTopChanged;
@property (nonatomic, assign) BOOL isWidthChanged;
@property (nonatomic, assign) BOOL isHeightChanged;

@property (nonatomic, assign) CGFloat left;
@property (nonatomic, assign) CGFloat top;
@property (nonatomic, assign) CGFloat width;
@property (nonatomic, assign) CGFloat height;

@property (nonatomic, assign) BOOL isBackgroundColorChanged;
@property (nonatomic, strong) NSString *backgroundColor;

//仅支持TextComponent
@property (nonatomic, assign) BOOL isColorChanged;
@property (nonatomic, strong) NSString *color;

@property (nonatomic, assign) BOOL isAlphaChanged;
@property (nonatomic, assign) CGFloat alpha;

@property (nonatomic, assign) BOOL isContentOffsetXChanged;
@property (nonatomic, assign) BOOL isContentOffsetYChanged;
@property (nonatomic, assign) CGFloat contentOffsetX;
@property (nonatomic, assign) CGFloat contentOffsetY;

@property (nonatomic, assign) BOOL isRotateXChanged;
@property (nonatomic, assign) BOOL isRotateYChanged;
@property (nonatomic, assign) BOOL isPerspectiveChanged;
@property (nonatomic, assign) BOOL isTransformOriginChanged;
@property (nonatomic, assign) CGFloat rotateX;
@property (nonatomic, assign) CGFloat rotateY;
@property (nonatomic, assign) CGFloat perspective;
@property (nonatomic, strong) NSString *transformOrigin;
@end
