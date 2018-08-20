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
package com.alibaba.android.bindingx.plugin.android;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.WeakRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

final class NativeViewUpdateService {
    private static final Map<String,INativeViewUpdater> sTransformPropertyUpdaterMap;
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

//    private static final List<String> LAYOUT_PROPERTIES = Arrays.asList(
//             LAYOUT_PROPERTY_WIDTH, LAYOUT_PROPERTY_HEIGHT,
//             LAYOUT_PROPERTY_MARGIN_LEFT, LAYOUT_PROPERTY_MARGIN_RIGHT, LAYOUT_PROPERTY_MARGIN_TOP,LAYOUT_PROPERTY_MARGIN_BOTTOM,
//             LAYOUT_PROPERTY_PADDING_LEFT, LAYOUT_PROPERTY_PADDING_RIGHT, LAYOUT_PROPERTY_PADDING_TOP, LAYOUT_PROPERTY_PADDING_BOTTOM
//    );

    private static final Handler sUIHandler = new Handler(Looper.getMainLooper());

    static {
        sTransformPropertyUpdaterMap = new HashMap<>();
        sTransformPropertyUpdaterMap.put("opacity",new OpacityUpdater());
        sTransformPropertyUpdaterMap.put("transform.translate",new TranslateUpdater());
        sTransformPropertyUpdaterMap.put("transform.translateX",new TranslateXUpdater());
        sTransformPropertyUpdaterMap.put("transform.translateY",new TranslateYUpdater());

//        sTransformPropertyUpdaterMap.put("transform.scale",new ScaleUpdater());
//        sTransformPropertyUpdaterMap.put("transform.scaleX",new ScaleXUpdater());
//        sTransformPropertyUpdaterMap.put("transform.scaleY",new ScaleYUpdater());
//
//        sTransformPropertyUpdaterMap.put("transform.rotate",new RotateUpdater());
//        sTransformPropertyUpdaterMap.put("transform.rotateZ",new RotateUpdater());
//        sTransformPropertyUpdaterMap.put("transform.rotateX",new RotateXUpdater());
//        sTransformPropertyUpdaterMap.put("transform.rotateY",new RotateYUpdater());

        sTransformPropertyUpdaterMap.put("background-color",new BackgroundUpdater());
        sTransformPropertyUpdaterMap.put("color", new ColorUpdater());

//        sTransformPropertyUpdaterMap.put("scroll.contentOffset", new ContentOffsetUpdater());
        sTransformPropertyUpdaterMap.put("scroll.contentOffsetX", new ContentOffsetXUpdater());
        sTransformPropertyUpdaterMap.put("scroll.contentOffsetY", new ContentOffsetYUpdater());
//
//        sTransformPropertyUpdaterMap.put("border-top-left-radius", new BorderRadiusTopLeftUpdater());
//        sTransformPropertyUpdaterMap.put("border-top-right-radius", new BorderRadiusTopRightUpdater());
//        sTransformPropertyUpdaterMap.put("border-bottom-left-radius", new BorderRadiusBottomLeftUpdater());
//        sTransformPropertyUpdaterMap.put("border-bottom-right-radius", new BorderRadiusBottomRightUpdater());
//
//        sTransformPropertyUpdaterMap.put("border-radius", new BorderRadiusUpdater());
    }

    @NonNull
    static INativeViewUpdater findUpdater(@NonNull String prop) {
        INativeViewUpdater updater = sTransformPropertyUpdaterMap.get(prop);
        if(updater != null) {
            return updater;
        } else {
//           if(LAYOUT_PROPERTIES.contains(prop)) {
//               sLayoutUpdater.setPropertyName(prop);
//               return sLayoutUpdater;
//           } else {
//               LogProxy.e("unknown property [" + prop + "]");
//                return EMPTY_INVOKER;
//           }
            return EMPTY_INVOKER;
        }
    }

    private static final class NOpUpdater implements INativeViewUpdater {
        @Override
        public void update(
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




    private static final class OpacityUpdater implements INativeViewUpdater {

        @Override
        public void update(
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

    private static final class TranslateUpdater implements INativeViewUpdater {

        @Override
        public void update(
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

    private static final class TranslateXUpdater implements INativeViewUpdater {

        @Override
        public void update(
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

    private static final class TranslateYUpdater implements INativeViewUpdater {

        @Override
        public void update(
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

//    private static final class ScaleUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
//                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);
//
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(perspective != 0) {
//                        targetView.setCameraDistance(perspective);
//                    }
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    if(cmd instanceof Double) {
//                        final double val = (double) cmd;
//                        targetView.setScaleX((float) val);
//                        targetView.setScaleY((float) val);
//                    } else if(cmd instanceof ArrayList) {
//                        ArrayList<Object> list = (ArrayList<Object>) cmd;
//                        if(list.size() >= 2 && list.get(0) instanceof Double && list.get(1) instanceof Double) {
//                            final double x = (double) list.get(0);
//                            final double y = (double) list.get(1);
//                            targetView.setScaleX((float) x);
//                            targetView.setScaleY((float) y);
//                        }
//
//                    }
//                }
//            });
//        }
//    }
//
//    private static final class ScaleXUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//            if(!(cmd instanceof Double)) {
//                return;
//            }
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    final double d3 = (double) cmd;
//                    targetView.setScaleX((float) d3);
//                }
//            });
//        }
//    }

//    private static final class ScaleYUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//            if(!(cmd instanceof Double)) {
//                return;
//            }
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    final double d = (double) cmd;
//                    targetView.setScaleY((float) d);
//                }
//            });
//        }
//    }

//    private static final class RotateUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//
//            if(!(cmd instanceof Double)) {
//                return;
//            }
//
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
//                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);
//
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(perspective != 0) {
//                        targetView.setCameraDistance(perspective);
//                    }
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    final double d = (double) cmd;
//                    targetView.setRotation((float) d);
//                }
//            });
//        }
//    }

//    private static final class RotateXUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//            if(!(cmd instanceof Double)) {
//                return;
//            }
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
//                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);
//
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(perspective != 0) {
//                        targetView.setCameraDistance(perspective);
//                    }
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    final double d = (double) cmd;
//                    targetView.setRotationX((float) d);
//                }
//            });
//        }
//    }


//    private static final class RotateYUpdater implements IWXViewUpdater {
//
//        @Override
//        public void update(
//                           @NonNull final View targetView,
//                           @NonNull final Object cmd,
//                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
//                           @NonNull final Map<String,Object> config) {
//            if(!(cmd instanceof Double)) {
//                return;
//            }
//            runOnUIThread(new Runnable() {
//                @Override
//                public void run() {
//                    int perspective = WXUtils.getInt(config.get(PERSPECTIVE));
//                    perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);
//
//                    Pair<Float,Float> pivot = Utils.parseTransformOrigin(
//                            WXUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);
//
//                    if(perspective != 0) {
//                        targetView.setCameraDistance(perspective);
//                    }
//                    if(pivot != null) {
//                        targetView.setPivotX(pivot.first);
//                        targetView.setPivotY(pivot.second);
//                    }
//
//                    final double d = (double) cmd;
//                    targetView.setRotationY((float) d);
//                }
//            });
//        }
//    }


    private static final class BackgroundUpdater implements INativeViewUpdater {

        @Override
        public void update(
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
                    } else if(drawable instanceof ColorDrawable) {
                        ColorDrawable colorDrawable = (ColorDrawable) drawable;
                        colorDrawable.setColor(d);
                    }
                }
            });
        }
    }

    private static final class ColorUpdater implements INativeViewUpdater {

        @Override
        public void update(
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
                    }
                }
            });
        }
    }

    private static final class ContentOffsetXUpdater implements INativeViewUpdater {

        @Override
        public void update(
                @NonNull final View targetView,
                @NonNull Object cmd,
                @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }

            final double val = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    targetView.setScrollX((int) getRealSize(val,translator));
                }
            });
        }
    }

    private static final class ContentOffsetYUpdater implements INativeViewUpdater {

        @Override
        public void update(
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config) {
            if(!(cmd instanceof Double)) {
                return;
            }

            final double val = (double) cmd;
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    targetView.setScrollY((int) getRealSize(val,translator));
                }
            });
        }
    }


    private static double getRealSize(double size,@NonNull PlatformManager.IDeviceResolutionTranslator translator) {
        return translator.webToNative(size);
    }

}
