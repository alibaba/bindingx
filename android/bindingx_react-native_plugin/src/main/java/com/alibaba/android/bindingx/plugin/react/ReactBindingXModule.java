package com.alibaba.android.bindingx.plugin.react;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.IEventHandler;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.UIManagerModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

@ReactModule(name = ReactBindingXModule.NAME)
public final class ReactBindingXModule extends ReactContextBaseJavaModule implements LifecycleEventListener{

    /*package*/ static final String NAME = "bindingX";

    private BindingXCore mBindingXCore;
    private PlatformManager mPlatformManager;

    private ExecutorService mExecutorService;

    /*package*/ ReactBindingXModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        if(getReactApplicationContext() != null) {
            getReactApplicationContext().addLifecycleEventListener(this);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void prepareInternal() {
        if(mPlatformManager == null) {
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

    //TODO 测试下callback是否可以使用多次
    @ReactMethod
    //A native module is supposed to invoke its callback only once. It can, however, store the callback and invoke it later.
    public void bind(final ReadableMap params,final Callback callback) {
        executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                prepareInternal();

                String token = mBindingXCore.doBind(
                        getReactApplicationContext(),
                        null,// react native don't need it
                        params == null ? Collections.<String, Object>emptyMap() : params.toHashMap(),
                        new BindingXCore.JavaScriptCallback() {
                            @Override
                            public void callback(Object params) {
                                if (callback != null) {
                                    callback.invoke(params);
                                }
                            }
                        });
                //        Map<String, String> result = new HashMap<>(2);
                //        result.put(BindingXConstants.KEY_TOKEN, token);
                //        return result;
            }
        });
    }

    @ReactMethod
    public void unbind(final ReadableMap params) {
        if(params == null) {
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


    /**
     *
     * notice: using default mqt_js thread
     * */
    @ReactMethod
    @SuppressWarnings("unused")
    public WritableMap getComputedStyle(@Nullable String ref) {
        //TODO
        return null;
    }

    /**
     *
     * notice: using default mqt_js thread
     * */
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
                        if(host == null || TextUtils.isEmpty(ref)) {
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
                    public void synchronouslyUpdateViewOnUIThread(@NonNull View targetView,
                                                                  @NonNull String propertyName,
                                                                  @NonNull Object propertyValue,
                                                                  @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                                                                  @NonNull Map<String, Object> config,
                                                                  Object... extension) {
                        if(reactContext != null) {
                            UIManagerModule module = reactContext.getNativeModule(UIManagerModule.class);
                            if(module != null) {
                                UIImplementation implementation = module.getUIImplementation();
                                if(implementation != null) {
                                    UiThreadUtil.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //TODO
//                                           implementation.synchronouslyUpdateViewOnUIThread();
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


    private void executeAsynchronously(@Nullable final Runnable runnable) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, "bindingX-thread");
                }
            });
        }
        if(runnable != null) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        LogProxy.e("unexpected internal error.", e);
                    }
                }
            });
        }
    }
}
