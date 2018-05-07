/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.android.bindingx.plugin.weex;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.WeakRunnable;
import com.alibaba.android.bindingx.core.internal.Utils;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.CSSShorthand;
import com.taobao.weex.dom.transition.WXTransition;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXScroller;
import com.taobao.weex.ui.component.WXText;
import com.taobao.weex.ui.view.WXTextView;
import com.taobao.weex.ui.view.border.BorderDrawable;
import com.taobao.weex.utils.WXUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

final class WXViewUpdateService {
    private static final Map<String,IWXViewUpdater> sTransformPropertyUpdaterMap;
    private static final LayoutUpdater sLayoutUpdater = new LayoutUpdater();
    private static final NOpUpdater EMPTY_INVOKER = new NOpUpdater();

    private static final String PERSPECTIVE = "perspective";
    private static final String TRANSFORM_ORIGIN = "transformOrigin";

    private static final String LAYOUT_PROPERTY_WIDTH = "width";
    private static final String LAYOUT_PROPERTY_HEIGHT = "height";

    private static final String LAYOUT_PROPERTY_MARGIN_LEFT = "margin-left";
    private static final String LAYOUT_PROPERTY_MARGIN_RIGHT = "margin-right";
    private static final String LAYOUT_PROPERTY_MARGIN_TOP = "margin-top";
    private static final String LAYOUT_PROPERTY_MARGIN_BOTTOM = "margin-bottom";

    private static final String LAYOUT_PROPERTY_PADDING_LEFT = "padding-left";
    private static final String LAYOUT_PROPERTY_PADDING_RIGHT = "padding-right";
    private static final String LAYOUT_PROPERTY_PADDING_TOP = "padding-top";
    private static final String LAYOUT_PROPERTY_PADDING_BOTTOM = "padding-bottom";

    private static final List<String> LAYOUT_PROPERTIES = Arrays.asList(
             LAYOUT_PROPERTY_WIDTH, LAYOUT_PROPERTY_HEIGHT,
             LAYOUT_PROPERTY_MARGIN_LEFT, LAYOUT_PROPERTY_MARGIN_RIGHT, LAYOUT_PROPERTY_MARGIN_TOP,LAYOUT_PROPERTY_MARGIN_BOTTOM,
             LAYOUT_PROPERTY_PADDING_LEFT, LAYOUT_PROPERTY_PADDING_RIGHT, LAYOUT_PROPERTY_PADDING_TOP, LAYOUT_PROPERTY_PADDING_BOTTOM
    );

    private static final Handler sUIHandler = new Handler(Looper.getMainLooper());

    static {
        sTransformPropertyUpdaterMap = new HashMap<>();
        sTransformPropertyUpdaterMap.put("opacity",new OpacityUpdater());
        sTransformPropertyUpdaterMap.put("transform.translate",new TranslateUpdater());
        sTransformPropertyUpdaterMap.put("transform.translateX",new TranslateXUpdater());
        sTransformPropertyUpdaterMap.put("transform.translateY",new TranslateYUpdater());

        sTransformPropertyUpdaterMap.put("transform.scale",new ScaleUpdater());
        sTransformPropertyUpdaterMap.put("transform.scaleX",new ScaleXUpdater());
        sTransformPropertyUpdaterMap.put("transform.scaleY",new ScaleYUpdater());

        sTransformPropertyUpdaterMap.put("transform.rotate",new RotateUpdater());
        sTransformPropertyUpdaterMap.put("transform.rotateZ",new RotateUpdater());
        sTransformPropertyUpdaterMap.put("transform.rotateX",new RotateXUpdater());
        sTransformPropertyUpdaterMap.put("transform.rotateY",new RotateYUpdater());

        sTransformPropertyUpdaterMap.put("background-color",new BackgroundUpdater());
        sTransformPropertyUpdaterMap.put("color", new ColorUpdater());

        sTransformPropertyUpdaterMap.put("scroll.contentOffset", new ContentOffsetUpdater());
        sTransformPropertyUpdaterMap.put("scroll.contentOffsetX", new ContentOffsetXUpdater());
        sTransformPropertyUpdaterMap.put("scroll.contentOffsetY", new ContentOffsetYUpdater());

        sTransformPropertyUpdaterMap.put("border-top-left-radius", new BorderRadiusTopLeftUpdater());
        sTransformPropertyUpdaterMap.put("border-top-right-radius", new BorderRadiusTopRightUpdater());
        sTransformPropertyUpdaterMap.put("border-bottom-left-radius", new BorderRadiusBottomLeftUpdater());
        sTransformPropertyUpdaterMap.put("border-bottom-right-radius", new BorderRadiusBottomRightUpdater());

        sTransformPropertyUpdaterMap.put("border-radius", new BorderRadiusUpdater());
    }

    @NonNull
    static IWXViewUpdater findUpdater(@NonNull String prop) {
        IWXViewUpdater updater = sTransformPropertyUpdaterMap.get(prop);
        if(updater != null) {
            return updater;
        } else {
           if(LAYOUT_PROPERTIES.contains(prop)) {
               sLayoutUpdater.setPropertyName(prop);
               return sLayoutUpdater;
           } else {
               LogProxy.e("unknown property [" + prop + "]");
                return EMPTY_INVOKER;
           }
        }
    }

    private static final class NOpUpdater implements IWXViewUpdater {
        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            //oops
        }
    }

    private static void runOnUIThread(Runnable runnable) {
        sUIHandler.post(new WeakRunnable(runnable));
    }

    public static void clearCallbacks() {
        sUIHandler.removeCallbacksAndMessages(null);
    }

    private static final class ContentOffsetUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            if(cmd instanceof Double) {
                final double val = (double) cmd;
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.setScrollX((int) getRealSize(val,translator));
                        scrollView.setScrollY((int) getRealSize(val,translator));
                    }
                });
            } else if(cmd instanceof ArrayList) {
                ArrayList<Object> l = (ArrayList<Object>) cmd;
                if(l.size() >= 2 && l.get(0) instanceof Double && l.get(1) instanceof Double) {
                    final double x = (double) l.get(0);
                    final double y = (double) l.get(1);
                    runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setScrollX((int) getRealSize(x,translator));
                            scrollView.setScrollY((int) getRealSize(y,translator));
                        }
                    });
                }

            }
        }
    }

    private static final class ContentOffsetXUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            if(!(cmd instanceof Double)) {
                return;
            }
            final double val = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    scrollView.setScrollX((int) getRealSize(val,translator));
                }
            });
        }
    }

    private static final class ContentOffsetYUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final View scrollView = findScrollTarget(component);
            if(scrollView == null) {
                return;
            }
            final double val = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    scrollView.setScrollY((int) getRealSize(val,translator));
                }
            });
        }
    }

    private static final class OpacityUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double val = (double) cmd;
            final float alpha = (float) (val);
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    targetView.setAlpha(alpha);
                }
            });
        }
    }

    private static final class TranslateUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {

            if(!(cmd instanceof ArrayList)) {
                return;
            }

            ArrayList<Object> list = (ArrayList<Object>) cmd;
            if(list.size() >= 2 && list.get(0) instanceof Double && list.get(1) instanceof Double) {
                final double x1 = (double) list.get(0);
                final double y1 = (double) list.get(1);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        targetView.setTranslationX((float) getRealSize(x1,translator));
                        targetView.setTranslationY((float) getRealSize(y1,translator));
                    }
                });
            }
        }
    }

    private static final class TranslateXUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    targetView.setTranslationX((float) getRealSize(d,translator));
                }
            });
        }
    }

    private static final class TranslateYUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    targetView.setTranslationY((float) getRealSize(d,translator));
                }
            });
        }
    }

    private static final class ScaleUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    if(cmd instanceof Double) {
                        final double val = (double) cmd;
                        targetView.setScaleX((float) val);
                        targetView.setScaleY((float) val);
                    } else if(cmd instanceof ArrayList) {
                        ArrayList<Object> list = (ArrayList<Object>) cmd;
                        if(list.size() >= 2 && list.get(0) instanceof Double && list.get(1) instanceof Double) {
                            final double x = (double) list.get(0);
                            final double y = (double) list.get(1);
                            targetView.setScaleX((float) x);
                            targetView.setScaleY((float) y);
                        }

                    }
                }
            });
        }
    }

    private static final class ScaleXUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d3 = (double) cmd;
                    targetView.setScaleX((float) d3);
                }
            });
        }
    }

    private static final class ScaleYUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d = (double) cmd;
                    targetView.setScaleY((float) d);
                }
            });
        }
    }

    private static final class RotateUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {

            if(!(cmd instanceof Double)) {
                return;
            }

            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d = (double) cmd;
                    targetView.setRotation((float) d);
                }
            });
        }
    }

    private static final class RotateXUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d = (double) cmd;
                    targetView.setRotationX((float) d);
                }
            });
        }
    }


    private static final class RotateYUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull final Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

                    if(perspective != 0) {
                        targetView.setCameraDistance(perspective);
                    }
                    if(pivot != null) {
                        targetView.setPivotX(pivot.first);
                        targetView.setPivotY(pivot.second);
                    }

                    final double d = (double) cmd;
                    targetView.setRotationY((float) d);
                }
            });
        }
    }

    static final class LayoutUpdater implements IWXViewUpdater {

        private String propertyName;

        void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Double) || TextUtils.isEmpty(propertyName)) {
                return;
            }
            double d = (double) cmd;
            String property = null;
            switch (propertyName) {
                case LAYOUT_PROPERTY_WIDTH:
                    property = Constants.Name.WIDTH;
                    break;
                case LAYOUT_PROPERTY_HEIGHT:
                    property = Constants.Name.HEIGHT;
                    break;
                case LAYOUT_PROPERTY_MARGIN_LEFT:
                    property = Constants.Name.MARGIN_LEFT;
                    break;
                case LAYOUT_PROPERTY_MARGIN_RIGHT:
                    property = Constants.Name.MARGIN_RIGHT;
                    break;
                case LAYOUT_PROPERTY_MARGIN_TOP:
                    property = Constants.Name.MARGIN_TOP;
                    break;
                case LAYOUT_PROPERTY_MARGIN_BOTTOM:
                    property = Constants.Name.MARGIN_BOTTOM;
                    break;
                case LAYOUT_PROPERTY_PADDING_LEFT:
                    property = Constants.Name.PADDING_LEFT;
                    break;
                case LAYOUT_PROPERTY_PADDING_RIGHT:
                    property = Constants.Name.PADDING_RIGHT;
                    break;
                case LAYOUT_PROPERTY_PADDING_TOP:
                    property = Constants.Name.PADDING_TOP;
                    break;
                case LAYOUT_PROPERTY_PADDING_BOTTOM:
                    property = Constants.Name.PADDING_BOTTOM;
                    break;
                default:
                    break;
            }
            if(TextUtils.isEmpty(property)) {
                return;
            }
            WXTransition.asynchronouslyUpdateLayout(component, property, (float) getRealSize(d,translator));
            propertyName = null;
        }
    }


    private static final class BackgroundUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d = (int) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = targetView.getBackground();
                    if(drawable == null) {
                        targetView.setBackgroundColor(d);
                    } else if(drawable instanceof BorderDrawable) {
                        BorderDrawable borderDrawable = (BorderDrawable) drawable;
                        borderDrawable.setColor(d);
                    } else if(drawable instanceof ColorDrawable) {
                        ColorDrawable colorDrawable = (ColorDrawable) drawable;
                        colorDrawable.setColor(d);
                    }
                }
            });
        }
    }

    private static final class ColorUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull final WXComponent component,
                           @NonNull final View targetView,
                           @NonNull final Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d = (int) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if(targetView instanceof TextView) {
                        ((TextView) targetView).setTextColor(d);
                    } else if(component instanceof WXText && targetView instanceof WXTextView) {
                        Layout layout = ((WXTextView) targetView).getTextLayout();
                        if(layout != null) {
                            CharSequence sequence = layout.getText();
                            if(sequence != null && sequence instanceof SpannableString) {
                                /*kind of ugly*/
                                ForegroundColorSpan[] spans = ((SpannableString) sequence).getSpans(
                                        0,
                                        sequence.length(),
                                        ForegroundColorSpan.class);
                                if(spans != null && spans.length == 1) {/*仅处理纯色text*/
                                    ((SpannableString) sequence).removeSpan(spans[0]);
                                    ((SpannableString) sequence).setSpan(new ForegroundColorSpan(d),
                                            0,
                                            sequence.length(),
                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                                    targetView.invalidate();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private static final class BorderRadiusTopLeftUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = targetView.getBackground();
                    if(drawable != null && drawable instanceof BorderDrawable) {
                        BorderDrawable borderDrawable = (BorderDrawable) drawable;
                        borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, (float) getRealSize(d,translator));
                    }
                }
            });
        }
    }

    private static final class BorderRadiusTopRightUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = targetView.getBackground();
                    if(drawable != null && drawable instanceof BorderDrawable) {
                        BorderDrawable borderDrawable = (BorderDrawable) drawable;
                        borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, (float) getRealSize(d,translator));
                    }
                }
            });
        }
    }

    private static final class BorderRadiusBottomLeftUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = targetView.getBackground();
                    if(drawable != null && drawable instanceof BorderDrawable) {
                        BorderDrawable borderDrawable = (BorderDrawable) drawable;
                        borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_LEFT, (float) getRealSize(d,translator));
                    }
                }
            });
        }
    }

    private static final class BorderRadiusBottomRightUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Drawable drawable = targetView.getBackground();
                    if(drawable != null && drawable instanceof BorderDrawable) {
                        BorderDrawable borderDrawable = (BorderDrawable) drawable;
                        borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_RIGHT, (float) getRealSize(d,translator));
                    }
                }
            });
        }
    }

    private static final class BorderRadiusUpdater implements IWXViewUpdater {

        @Override
        public void update(@NonNull WXComponent component,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config) {

            if(cmd instanceof ArrayList) {
                final ArrayList<Object> l = (ArrayList<Object>) cmd;
                if(l.size() != 4) {
                    return;
                }

                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable = targetView.getBackground();
                        if(drawable != null && drawable instanceof BorderDrawable) {
                            double topLeft = 0,topRight = 0,bottomLeft = 0,bottomRight = 0;
                            if(l.get(0) instanceof Double) {
                                topLeft = (double) l.get(0);
                            }
                            if(l.get(1) instanceof Double) {
                                topRight = (double) l.get(1);
                            }
                            if(l.get(2) instanceof Double) {
                                bottomLeft = (double) l.get(2);
                            }
                            if(l.get(3) instanceof Double) {
                                bottomRight = (double) l.get(3);
                            }

                            BorderDrawable borderDrawable = (BorderDrawable) drawable;
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, (float) getRealSize(topLeft,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, (float) getRealSize(topRight,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_LEFT, (float) getRealSize(bottomLeft,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_RIGHT, (float) getRealSize(bottomRight,translator));
                        }
                    }
                });
            } else if(cmd instanceof Double) {
                final double value = (double) cmd;
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable = targetView.getBackground();
                        if(drawable != null && drawable instanceof BorderDrawable) {
                            BorderDrawable borderDrawable = (BorderDrawable) drawable;
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, (float) getRealSize(value,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, (float) getRealSize(value,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_LEFT, (float) getRealSize(value,translator));
                            borderDrawable.setBorderRadius(CSSShorthand.CORNER.BORDER_BOTTOM_RIGHT, (float) getRealSize(value,translator));
                        }
                    }
                });
            }
        }
    }

    private static double getRealSize(double size,@NonNull PlatformManager.IDeviceResolutionTranslator translator) {
        return translator.webToNative(size);
    }

    @Nullable
    private static View findScrollTarget(@NonNull WXComponent component) {
        if(!(component instanceof WXScroller)) {
            LogProxy.e("scroll offset only support on Scroller Component");
            return null;
        }
        WXScroller scroller = (WXScroller) component;
        return scroller.getInnerView();
    }
}
