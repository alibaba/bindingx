package com.alibaba.android.bindingx.plugin.weex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionScrollHandler;
import com.alibaba.android.bindingx.plugin.weex.internal.Utils;
import com.alibaba.android.bindingx.plugin.weex.internal.WXModuleUtils;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.bridge.WXBridgeManager;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXText;
import com.taobao.weex.ui.view.WXTextView;
import com.taobao.weex.ui.view.border.BorderDrawable;
import com.taobao.weex.utils.WXViewUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * ExpressionBinding新接口
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class WXExpressionBindingV2Module extends WXSDKEngine.DestroyableModule {

    private ExpressionBindingCore mExpressionBindingCore;
    private PlatformManager mPlatformManager;

    @JSMethod(uiThread = false)
    public void prepare(Map<String, Object> params) {
        if(mPlatformManager == null) {
            mPlatformManager = createPlatformManager(mWXSDKInstance);
        }
        if (mExpressionBindingCore == null) {
            mExpressionBindingCore = new ExpressionBindingCore(mPlatformManager);

            mExpressionBindingCore.registerEventHandler(EventType.TYPE_SCROLL,
                    new ExpressionBindingCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
                @Override
                public IEventHandler createWith(@NonNull Context context,@NonNull PlatformManager manager, Object... extension) {
                    return new ExpressionScrollHandler(context, manager, extension);
                }
            });
        }

        //空实现。 此方法仅为了与iOS兼容
    }

    @JSMethod(uiThread = false)
    public Map<String, String> bind(Map<String, Object> params, final JSCallback callback) {
        prepare(null);
        String token = mExpressionBindingCore.doBind(
                mWXSDKInstance == null ? null : mWXSDKInstance.getContext(),
                mWXSDKInstance == null ? null : mWXSDKInstance.getInstanceId(),
                params == null ? Collections.<String, Object>emptyMap() : params,
                new ExpressionBindingCore.JavaScriptCallback() {
                    @Override
                    public void callback(Object params) {
                        if (callback != null) {
                            callback.invokeAndKeepAlive(params);
                        }
                    }
                });
        Map<String, String> result = new HashMap<>(2);
        result.put(ExpressionConstants.KEY_TOKEN, token);
        return result;
    }

    @JSMethod(uiThread = false)
    public void unbind(Map<String, Object> params) {
        if (mExpressionBindingCore != null) {
            mExpressionBindingCore.doUnbind(params);
        }
    }

    @JSMethod(uiThread = false)
    public void unbindAll() {
        if (mExpressionBindingCore != null) {
            mExpressionBindingCore.doRelease();
        }
    }

    @JSMethod(uiThread = false)
    public List<String> supportFeatures() {
        return Arrays.asList("pan", "orientation", "timing", "scroll");
    }

    @JSMethod(uiThread = false)
    public Map<String, Object> getComputedStyle(@Nullable String ref) {
        prepare(null);
        PlatformManager.IDeviceResolutionTranslator resolutionTranslator = mPlatformManager.getResolutionTranslator();

        WXComponent component = WXModuleUtils.findComponentByRef(mWXSDKInstance.getInstanceId(), ref);
        if (component == null) {
            return Collections.emptyMap();
        }
        View sourceView = component.getHostView();
        if (sourceView == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>();

        map.put("translateX", resolutionTranslator.nativeToWeb(sourceView.getTranslationX()));
        map.put("translateY", resolutionTranslator.nativeToWeb(sourceView.getTranslationY()));


        map.put("rotateX", Utils.normalizeRotation(sourceView.getRotationX()));
        map.put("rotateY", Utils.normalizeRotation(sourceView.getRotationY()));
        map.put("rotateZ", Utils.normalizeRotation(sourceView.getRotation()));

        map.put("scaleX", sourceView.getScaleX());
        map.put("scaleY", sourceView.getScaleY());

        map.put("opacity", sourceView.getAlpha());

        if (sourceView.getBackground() != null) {
            int backgroundColor = Color.BLACK;
            if (sourceView.getBackground() instanceof ColorDrawable) {
                backgroundColor = ((ColorDrawable) sourceView.getBackground()).getColor();
            } else if (sourceView.getBackground() instanceof BorderDrawable) {
                backgroundColor = ((BorderDrawable) sourceView.getBackground()).getColor();
            }

            double a = Color.alpha(backgroundColor) / 255.0d;
            int r = Color.red(backgroundColor);
            int g = Color.green(backgroundColor);
            int b = Color.blue(backgroundColor);
            map.put("background-color", String.format(Locale.CHINA, "rgba(%d,%d,%d,%f)", r, g, b, a));
        }

        if (component instanceof WXText && sourceView instanceof WXTextView) {
            Layout layout = ((WXTextView) sourceView).getTextLayout();
            if (layout != null) {
                CharSequence sequence = layout.getText();
                if (sequence != null && sequence instanceof SpannableString) {
                    ForegroundColorSpan[] spans = ((SpannableString) sequence).getSpans(0, sequence.length(), ForegroundColorSpan.class);
                    if (spans != null && spans.length == 1) {
                        int fontColor = spans[0].getForegroundColor();

                        double a = Color.alpha(fontColor) / 255.0d;
                        int r = Color.red(fontColor);
                        int g = Color.green(fontColor);
                        int b = Color.blue(fontColor);
                        map.put("color", String.format(Locale.CHINA, "rgba(%d,%d,%d,%f)", r, g, b, a));
                    }
                }
            }
        }

        return map;
    }

    @Override
    public void destroy() {
        WXBridgeManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (mExpressionBindingCore != null) {
                    mExpressionBindingCore.doRelease();
                    mExpressionBindingCore = null;
                }
            }
        }, null);
    }

    @NonNull
    /*package*/ static PlatformManager createPlatformManager(WXSDKInstance instance) {
        final int viewPort = instance == null ? 750 : instance.getInstanceViewPortWidth();

        return new PlatformManager.Builder()
                .withViewFinder(new PlatformManager.IViewFinder() {
                    @Nullable
                    @Override
                    public View findViewBy(String ref, Object... extension) {
                        if(extension.length <= 0 || !(extension[0] instanceof String)) {
                            return null;
                        }
                        String instanceId = (String) extension[0];
                        return WXModuleUtils.findViewByRef(instanceId, ref);
                    }
                })
                .withViewUpdater(new PlatformManager.IViewUpdater() {

                    @Override
                    public void synchronouslyUpdateViewOnUIThread(@NonNull View targetView,
                                                                  @NonNull String propertyName,
                                                                  @NonNull Object propertyValue,
                                                                  @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                                                                  @NonNull Map<String, Object> config,
                                                                  Object... extension) {
                        if(extension == null
                                || extension.length <= 0
                                || extension.length > 2
                                || !(extension[0] instanceof String)
                                || !(extension[1] instanceof String)) {
                            return;
                        }
                        String ref = (String) extension[0];
                        String instanceId = (String) extension[1];

                        WXComponent targetComponent = WXModuleUtils.findComponentByRef(instanceId, ref);
                        if(targetComponent == null) {
                            LogProxy.e("unexpected error. component not found [ref:"+ref+",instanceId:"+instanceId+"]");
                            return;
                        }
                        ExpressionInvokerService.findInvoker(propertyName).invoke(
                                targetComponent,
                                targetView,
                                propertyValue,
                                translator,
                                config);

                    }
                })
                .withDeviceResolutionTranslator(new PlatformManager.IDeviceResolutionTranslator() {
                    @Override
                    public double webToNative(double rawSize, Object... extension) {
                        return WXViewUtils.getRealPxByWidth((float) rawSize, viewPort);
                    }

                    @Override
                    public double nativeToWeb(double rawSize, Object... extension) {
                        return WXViewUtils.getWebPxByWidth((float) rawSize, viewPort);
                    }
                })
                .build();
    }


    ///////// Lifecycle Callbacks

    @Override
    public void onActivityPause() {
        WXBridgeManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (mExpressionBindingCore != null) {
                    mExpressionBindingCore.onActivityPause();
                }
            }
        }, null);
    }

    @Override
    public void onActivityResume() {
        WXBridgeManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (mExpressionBindingCore != null) {
                    mExpressionBindingCore.onActivityResume();
                }
            }
        }, null);
    }
}
