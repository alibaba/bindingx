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

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.IEventHandler;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.core.internal.Utils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.view.ReactViewBackgroundDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * BindingX APIs which will expose to JavaScript.
 *
 * Created by rowandjj(chuyi)<br/>
 */

@ReactModule(name = ReactBindingXModule.NAME)
public final class ReactBindingXModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    /*package*/ static final String NAME = "bindingx";

    private BindingXCore mBindingXCore;
    private PlatformManager mPlatformManager;

    private InternalWorkerThread mWorkerThread = null;

    /*package*/ ReactBindingXModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getReactApplicationContext() != null) {
            getReactApplicationContext().addLifecycleEventListener(this);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void prepareInternal() {
        if (mPlatformManager == null) {
            mPlatformManager = createPlatformManager(getReactApplicationContext());
        }
        if (mBindingXCore == null) {
            mBindingXCore = new BindingXCore(mPlatformManager);

            mBindingXCore.registerEventHandler(BindingXEventType.TYPE_SCROLL,
                    new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
                        @Override
                        public IEventHandler createWith(@NonNull Context context,
                                                        @NonNull PlatformManager manager,
                                                        Object... extension) {
                            return new BindingXScrollHandler(context, manager, extension);
                        }
                    });
        }
    }

    @ReactMethod
    @SuppressWarnings("unused")
    public void prepare(ReadableMap params) {
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                prepareInternal();
            }
        });
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    @SuppressWarnings("unchecked")
    public WritableMap bind(final ReadableMap params) {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<String> resultHolder = new ArrayList<>(2);
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    prepareInternal();
                    String token = mBindingXCore.doBind(
                            getReactApplicationContext(),
                            null,// react native don't need it
                            params == null ? Collections.<String, Object>emptyMap() : params.toHashMap(),
                            new BindingXCore.JavaScriptCallback() {
                                @Override
                                public void callback(Object params) {
                                    ReactApplicationContext context = getReactApplicationContext();
                                    if(context != null) {
                                        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                                .emit("bindingx:statechange",Arguments.makeNativeMap((Map<String,Object>)params));
                                    }
                                }
                            });
                    resultHolder.add(token);
                }finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await(2000, TimeUnit.MILLISECONDS);
        }catch (Exception e) {
            //ignore
        }

        String token = resultHolder.size() > 0 ? resultHolder.get(0) : null;
        return Arguments.makeNativeMap(Collections.<String,Object>singletonMap(BindingXConstants.KEY_TOKEN, token));
    }

    @ReactMethod
    public void unbind(final ReadableMap params) {
        if (params == null) {
            return;
        }
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (mBindingXCore != null) {
                    mBindingXCore.doUnbind(params.toHashMap());
                }
            }
        });
    }

    @ReactMethod
    public void unbindAll() {//mqt_native_modules
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (mBindingXCore != null) {
                    mBindingXCore.doRelease();
                }
            }
        });
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    @SuppressWarnings("unused")
    public WritableMap getComputedStyle(int ref) {
        prepareInternal();
        PlatformManager.IDeviceResolutionTranslator resolutionTranslator = mPlatformManager.getResolutionTranslator();
        PlatformManager.IViewFinder viewFinder = mPlatformManager.getViewFinder();
        View sourceView = viewFinder.findViewBy(String.valueOf(ref));
        if (sourceView == null) {
            return Arguments.makeNativeMap(Collections.<String,Object>emptyMap());
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
            if (sourceView.getBackground() instanceof ReactViewBackgroundDrawable) {
                backgroundColor = ((ReactViewBackgroundDrawable) sourceView.getBackground()).getColor();
            }

            double a = Color.alpha(backgroundColor) / 255.0d;
            int r = Color.red(backgroundColor);
            int g = Color.green(backgroundColor);
            int b = Color.blue(backgroundColor);
            map.put("background-color", String.format(Locale.getDefault(), "rgba(%d,%d,%d,%f)", r, g, b, a));
        }

        if (sourceView instanceof TextView) {
            TextView realView = (TextView) sourceView;
            int fontColor = realView.getCurrentTextColor();
            double a = Color.alpha(fontColor) / 255.0d;
            int r = Color.red(fontColor);
            int g = Color.green(fontColor);
            int b = Color.blue(fontColor);
            map.put("color", String.format(Locale.getDefault(), "rgba(%d,%d,%d,%f)", r, g, b, a));
        }

        return Arguments.makeNativeMap(map);
    }

    /**
     * notice: using default mqt_js thread
     */
    @ReactMethod(isBlockingSynchronousMethod = true)
    @SuppressWarnings("unused")
    public WritableArray supportFeatures() {
        return Arguments.makeNativeArray(Arrays.asList("pan", "orientation", "timing", "scroll"));
    }

    @Override
    public void onHostResume() {
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (mBindingXCore != null) {
                    LogProxy.d("host resumed");
                    mBindingXCore.onActivityResume();
                }
            }
        });

    }

    @Override
    public void onHostPause() {
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (mBindingXCore != null) {
                    LogProxy.d("host paused");
                    mBindingXCore.onActivityPause();
                }
            }
        });
    }

    @Override
    public void onHostDestroy() {
        // no-op
    }

    @NonNull
    private static PlatformManager createPlatformManager(final ReactApplicationContext reactContext) {
        return new PlatformManager.Builder()
                .withViewFinder(new PlatformManager.IViewFinder() {
                    @Nullable
                    @Override
                    public View findViewBy(String ref, Object... extension) {
                        Activity host = reactContext.getCurrentActivity();
                        if (host == null || TextUtils.isEmpty(ref)) {
                            return null;
                        }
                        try {
                            ref = ref.trim();
                            double value = Double.valueOf(ref);
                            return host.findViewById((int) value);
                        } catch (NumberFormatException e) {
                            LogProxy.e("number format error", e);
                            return null;
                        }
                    }
                })
                .withViewUpdater(new PlatformManager.IViewUpdater() {
                    @Override
                    public void synchronouslyUpdateViewOnUIThread(@NonNull final View targetView,
                                                                  @NonNull final String propertyName,
                                                                  @NonNull final Object propertyValue,
                                                                  @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                                                                  @NonNull final Map<String, Object> config,
                                                                  Object... extension) {
                        String ref = null;
                        if (extension != null && extension.length >= 1 && extension[0] instanceof String) {
                            ref = (String) extension[0];
                        }
                        if (reactContext != null && !TextUtils.isEmpty(ref)) {
                            int tag = -1;
                            ref = ref.trim();
                            try {
                                double value = Double.valueOf(ref);
                                tag = (int) value;
                            } catch (Exception e) {
                                //ignore
                            }
                            final int finalTag = tag;
                            UIManagerModule module = reactContext.getNativeModule(UIManagerModule.class);
                            if (module != null && tag != -1) {
                                final UIImplementation implementation = module.getUIImplementation();
                                if (implementation != null) {
                                    UiThreadUtil.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            RNViewUpdateService.findInvoker(propertyName).invoke(
                                                    finalTag,
                                                    targetView,
                                                    propertyValue,
                                                    translator,
                                                    config,
                                                    implementation

                                            );
                                        }
                                    });
                                }
                            }
                        }
                    }
                })
                .withDeviceResolutionTranslator(new PlatformManager.IDeviceResolutionTranslator() {
                    @Override
                    public double webToNative(double rawSize, Object... extension) {
                        return rawSize;
                    }

                    @Override
                    public double nativeToWeb(double rawSize, Object... extension) {
                        return rawSize;
                    }
                })
                .build();
    }


    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        if (mWorkerThread != null) {
            mWorkerThread.quit();
            mWorkerThread = null;
        }
    }

    private void executeAsynchronously(@Nullable final Runnable runnable) {
        if (mWorkerThread == null) {
            mWorkerThread = new InternalWorkerThread("bindingX-thread");
        }
        mWorkerThread.postRunnableGuarded(runnable);
    }


    static class InternalWorkerThread extends HandlerThread {

        private Handler mHandler;

        /*package*/ InternalWorkerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(this.getLooper());
        }

        /*package*/ void postRunnableGuarded(final Runnable runnable) {
            if (runnable != null && mHandler != null && isAlive()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runnable.run();
                        } catch (Exception e) {
                            LogProxy.e("unexpected internal error", e);
                        }
                    }
                });
            }
        }

        @Override
        public boolean quit() {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            return super.quit();
        }
    }
}
