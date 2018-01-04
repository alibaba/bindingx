//
//  WXExpressionProperty.m
//  Pods
//
//  Created by xiayun on 16/12/23.
//
//

#import "WXExpressionProperty.h"

@implementation WXExpressionProperty

- (instancetype)init {
    if (self = [super init]) {
        _sx = 1;
        _sy = 1;
    }
    return self;
}

#pragma mark - Setters
- (void)setTx:(CGFloat)tx {
    _tx = tx;
    _isTranslateChanged = YES;
    _isTransformChanged = YES;
}

- (void)setTy:(CGFloat)ty {
    _ty = ty;
    _isTranslateChanged = YES;
    _isTransformChanged = YES;
}

- (void)setSx:(CGFloat)sx {
    _sx = sx;
    _isScaleChagned = YES;
    _isTransformChanged = YES;
}

- (void)setSy:(CGFloat)sy {
    _sy = sy;
    _isScaleChagned = YES;
    _isTransformChanged = YES;
}

- (void)setAngle:(CGFloat)angle {
    _angle = angle;
    _isRotateChanged = YES;
    _isTransformChanged = YES;
}

- (void)setLeft:(CGFloat)left {
    _left = left;
    _isLeftChanged = YES;
}

- (void)setTop:(CGFloat)top {
    _top = top;
    _isTopChanged = YES;
}

- (void)setWidth:(CGFloat)width {
    _width = width;
    _isWidthChanged = YES;
}

- (void)setHeight:(CGFloat)height {
    _height = height;
    _isHeightChanged = YES;
}

- (void)setBackgroundColor:(NSString *)backgroundColor {
    _backgroundColor = backgroundColor;
    _isBackgroundColorChanged = YES;
}

- (void)setColor:(NSString *)color {
    _color = color;
    _isColorChanged = YES;
}

- (void)setAlpha:(CGFloat)alpha {
    _alpha = alpha > 0 ? alpha : 0;
    _isAlphaChanged = YES;
}

- (void)setContentOffsetX:(CGFloat)contentOffsetX {
    _contentOffsetX = contentOffsetX;
    _isContentOffsetXChanged = YES;
}

- (void)setContentOffsetY:(CGFloat)contentOffsetY {
    _contentOffsetY = contentOffsetY;
    _isContentOffsetYChanged = YES;
}

- (void)setPerspective:(CGFloat)perspective{
    _perspective = perspective;
    _isPerspectiveChanged = YES;
    _isTransformChanged = YES;
}

- (void)setRotateX:(CGFloat)rotateX{
    _rotateX = rotateX;
    _isRotateXChanged = YES;
    _isTransformChanged = YES;
}

- (void)setRotateY:(CGFloat)rotateY{
    _rotateY = rotateY;
    _isRotateYChanged = YES;
    _isTransformChanged = YES;
}

- (void)setTransformOrigin:(NSString *)transformOrigin{
    _transformOrigin = transformOrigin;
    _isTransformOriginChanged = YES;
}

@end
