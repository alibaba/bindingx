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
package com.alibaba.android.bindingx.plugin.react;

import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.Utils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.UIImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

final class RNViewUpdateService {
    private static final Map<String,IRNViewUpdater> sExpressionInvokerMap;
    private static final NOpInvoker EMPTY_INVOKER = new NOpInvoker();

    private static final String PERSPECTIVE = "perspective";
    private static final String TRANSFORM_ORIGIN = "transformOrigin";

    static {
        sExpressionInvokerMap = new HashMap<>();
        sExpressionInvokerMap.put("opacity",new OpacityInvoker());
        sExpressionInvokerMap.put("transform.translate",new TranslateInvoker());
        sExpressionInvokerMap.put("transform.translateX",new TranslateXInvoker());
        sExpressionInvokerMap.put("transform.translateY",new TranslateYInvoker());

        sExpressionInvokerMap.put("transform.scale",new ScaleInvoker());
        sExpressionInvokerMap.put("transform.scaleX",new ScaleXInvoker());
        sExpressionInvokerMap.put("transform.scaleY",new ScaleYInvoker());

        sExpressionInvokerMap.put("transform.rotate",new RotateInvoker());
        sExpressionInvokerMap.put("transform.rotateZ",new RotateInvoker());
        sExpressionInvokerMap.put("transform.rotateX",new RotateXInvoker());
        sExpressionInvokerMap.put("transform.rotateY",new RotateYInvoker());

        sExpressionInvokerMap.put("background-color",new BackgroundInvoker());
        sExpressionInvokerMap.put("color", new ColorInvoker());

        sExpressionInvokerMap.put("scroll.contentOffset", new ContentOffsetInvoker());
        sExpressionInvokerMap.put("scroll.contentOffsetX", new ContentOffsetXInvoker());
        sExpressionInvokerMap.put("scroll.contentOffsetY", new ContentOffsetYInvoker());

        // dangerous. Not Recommended.
        sExpressionInvokerMap.put("width",new WidthInvoker());
        sExpressionInvokerMap.put("height",new HeightInvoker());
    }

    @NonNull
    static IRNViewUpdater findInvoker(@NonNull String prop) {
        final IRNViewUpdater invoker = sExpressionInvokerMap.get(prop);
        if(invoker == null) {
            LogProxy.e("unknown property [" + prop + "]");
            return EMPTY_INVOKER;
        }
        return new IRNViewUpdater() {
            @Override
            public void invoke(int tag,
                               @NonNull View targetView,
                               @NonNull Object cmd,
                               @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                               @NonNull Map<String, Object> config,
                               @NonNull UIImplementation implementation) {
                invoker.invoke(tag,targetView,cmd,translator,config,implementation);
                implementation.synchronouslyUpdateViewOnUIThread(tag, new ReactStylesDiffMap(Arguments.createMap()));
            }
        };
    }

    private static final class NOpInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String, Object> config,
                           @NonNull UIImplementation implementation) {
            // no-op
        }
    }

    private static final class OpacityInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double val = (double) cmd;
            final float alpha = (float) (val);
            targetView.setAlpha(alpha);
        }
    }

    private static final class TranslateInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {

            if(!(cmd instanceof ArrayList)) {
                return;
            }

            ArrayList<Object> l1 = (ArrayList<Object>) cmd;
            if(l1.size() >= 2 && l1.get(0) instanceof Double && l1.get(1) instanceof Double) {
                final double x1 = (double) l1.get(0);
                final double y1 = (double) l1.get(1);
                targetView.setTranslationX((float) getRealSize(x1,translator));
                targetView.setTranslationY((float) getRealSize(y1,translator));
            }
        }
    }

    private static final class TranslateXInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d1 = (double) cmd;
            targetView.setTranslationX((float) getRealSize(d1,translator));

        }
    }

    private static final class TranslateYInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            final double d2 = (double) cmd;
            targetView.setTranslationY((float) getRealSize(d2,translator));
        }
    }

    private static final class ScaleInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {

            int perspective = RNUtils.getInt(config.get(PERSPECTIVE),0);
            perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

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
                ArrayList<Object> l2 = (ArrayList<Object>) cmd;
                if(l2.size() >= 2 && l2.get(0) instanceof Double && l2.get(1) instanceof Double) {
                    final double x2 = (double) l2.get(0);
                    final double y2 = (double) l2.get(1);
                    targetView.setScaleX((float) x2);
                    targetView.setScaleY((float) y2);
                }

            }
        }
    }

    private static final class ScaleXInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

            if(pivot != null) {
                targetView.setPivotX(pivot.first);
                targetView.setPivotY(pivot.second);
            }

            final double d3 = (double) cmd;
            targetView.setScaleX((float) d3);
        }
    }

    private static final class ScaleYInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

            if(pivot != null) {
                targetView.setPivotX(pivot.first);
                targetView.setPivotY(pivot.second);
            }

            final double d4 = (double) cmd;
            targetView.setScaleY((float) d4);
        }
    }

    private static final class RotateInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {

            if(!(cmd instanceof Double)) {
                return;
            }

            int perspective = RNUtils.getInt(config.get(PERSPECTIVE), 0);
            perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

            if(perspective != 0) {
                targetView.setCameraDistance(perspective);
            }
            if(pivot != null) {
                targetView.setPivotX(pivot.first);
                targetView.setPivotY(pivot.second);
            }

            final double d5 = (double) cmd;
            targetView.setRotation((float) d5);
        }
    }

    private static final class RotateXInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            int perspective = RNUtils.getInt(config.get(PERSPECTIVE),0);
            perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

            if(perspective != 0) {
                targetView.setCameraDistance(perspective);
            }
            if(pivot != null) {
                targetView.setPivotX(pivot.first);
                targetView.setPivotY(pivot.second);
            }

            final double d6 = (double) cmd;
            targetView.setRotationX((float) d6);
        }
    }


    private static final class RotateYInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            int perspective = RNUtils.getInt(config.get(PERSPECTIVE), 0);
            perspective = Utils.normalizedPerspectiveValue(targetView.getContext(),perspective);

            Pair<Float,Float> pivot = Utils.parseTransformOrigin(
                    RNUtils.getString(config.get(TRANSFORM_ORIGIN),null),targetView);

            if(perspective != 0) {
                targetView.setCameraDistance(perspective);
            }
            if(pivot != null) {
                targetView.setPivotX(pivot.first);
                targetView.setPivotY(pivot.second);
            }

            final double d7 = (double) cmd;
            targetView.setRotationY((float) d7);
        }
    }


    private static final class WidthInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double d8 = (double) cmd;
            final ViewGroup.LayoutParams params1 = targetView.getLayoutParams();
            params1.width = (int) getRealSize(d8,translator);
            targetView.setLayoutParams(params1);

        }
    }

    private static final class HeightInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Double)) {
                return;
            }
            double d9 = (double) cmd;
            final ViewGroup.LayoutParams params2 = targetView.getLayoutParams();
            params2.height = (int) getRealSize(d9,translator);
            targetView.setLayoutParams(params2);

        }
    }

    private static final class BackgroundInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d10 = (int) cmd;
            targetView.setBackgroundColor(d10);
        }
    }

    private static final class ColorInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(cmd instanceof Integer)) {
                return;
            }
            final int d = (int) cmd;
            if(targetView instanceof TextView) {
                ((TextView) targetView).setTextColor(d);
            }
        }
    }


    private static final class ContentOffsetInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {

            if(!(targetView instanceof ScrollView)) {
                return;
            }

            ScrollView scrollView = (ScrollView) targetView;
            if(cmd instanceof Double) {
                final double val = (double) cmd;
                scrollView.setScrollX((int) getRealSize(val,translator));
                scrollView.setScrollY((int) getRealSize(val,translator));
            } else if(cmd instanceof ArrayList) {
                ArrayList<Object> l = (ArrayList<Object>) cmd;
                if(l.size() >= 2 && l.get(0) instanceof Double && l.get(1) instanceof Double) {
                    final double x = (double) l.get(0);
                    final double y = (double) l.get(1);
                    scrollView.setScrollX((int) getRealSize(x,translator));
                    scrollView.setScrollY((int) getRealSize(y,translator));
                }

            }
        }
    }

    private static final class ContentOffsetXInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {
            if(!(targetView instanceof ScrollView)) {
                return;
            }

            ScrollView scrollView = (ScrollView) targetView;
            if(!(cmd instanceof Double)) {
                return;
            }
            final double val = (double) cmd;
            scrollView.setScrollX((int) getRealSize(val,translator));
        }
    }

    private static final class ContentOffsetYInvoker implements IRNViewUpdater {

        @Override
        public void invoke(int tag,
                           @NonNull final View targetView,
                           @NonNull Object cmd,
                           @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                           @NonNull Map<String,Object> config,
                           @NonNull UIImplementation implementation) {

            if(!(targetView instanceof ScrollView)) {
                return;
            }

            ScrollView scrollView = (ScrollView) targetView;
            if(!(cmd instanceof Double)) {
                return;
            }
            final double val = (double) cmd;
            scrollView.setScrollY((int) getRealSize(val,translator));
        }
    }

    private static double getRealSize(double size,@NonNull PlatformManager.IDeviceResolutionTranslator translator) {
        return translator.webToNative(size);
    }

}
