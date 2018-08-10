package com.alibaba.android.bindingx.plugin.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.plugin.android.model.BindingXSpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class NativeBindingX {
    private BindingXCore mBindingXCore;
    private PlatformManager mPlatformManager;

    @SuppressWarnings("unused")
    public static NativeBindingX create() {
        return new NativeBindingX(null, null);
    }

    @SuppressWarnings("unused")
    public static NativeBindingX create(@Nullable NativeViewFinder finder) {
        return new NativeBindingX(finder, null);
    }

    @SuppressWarnings("unused")
    public static NativeBindingX create(@NonNull NativeViewFinder finder, @Nullable PlatformManager.IDeviceResolutionTranslator translator) {
        return new NativeBindingX(finder, translator);
    }

    private NativeBindingX(@Nullable NativeViewFinder finder, @Nullable PlatformManager.IDeviceResolutionTranslator translator) {
        if(finder == null) {
            finder = new NativeViewFinder() {
                @Override
                public View findViewBy(View rootView, String ref) {
                    if(rootView == null || TextUtils.isEmpty(ref)) {
                        return null;
                    }
                    Context c = rootView.getContext();
                    int id = c.getResources().getIdentifier(ref, "id",c.getPackageName());
                    return id > 0 ? rootView.findViewById(id) : null;
                }
            };
        }
        if(translator == null) {
            translator = new PlatformManager.IDeviceResolutionTranslator() {
                @Override
                public double webToNative(double rawSize, Object... extension) {
                    return rawSize;
                }

                @Override
                public double nativeToWeb(double rawSize, Object... extension) {
                    return rawSize;
                }
            };
        }

        mPlatformManager = createPlatformManager(new ViewFinderProxy(finder), translator);
        mBindingXCore = new BindingXCore(mPlatformManager);
    }

    @SuppressWarnings({"unchecked","unused"})
    public Map<String,String> bind(View rootView, BindingXSpec spec, NativeCallback callback) {
        if(spec == null) {
            LogProxy.e("params invalid. bindingX spec is null");
            return Collections.emptyMap();
        }
        return bind(rootView, resolveParams(spec), callback);
    }

    @SuppressWarnings({"unchecked","unused"})
    public Map<String,String> bind(View rootView, Map<String,Object> params,final NativeCallback callback) {
        if(rootView == null) {
            LogProxy.e("params invalid. view is null");
            return Collections.emptyMap();
        }
        params = (params == null ? Collections.<String, Object>emptyMap() : params);
        String token = mBindingXCore.doBind(rootView.getContext(), null, params, new BindingXCore.JavaScriptCallback() {
                    @Override
                    public void callback(Object params) {
                        if (callback != null && params instanceof Map) {
                            callback.callback((Map<String, Object>) params);
                        }
                    }
                },rootView);
        Map<String, String> result = new HashMap<>(2);
        result.put(BindingXConstants.KEY_TOKEN, token);
        return result;
    }

    @SuppressWarnings("unused")
    public void unbind(Map<String,Object> params) {
        if(mBindingXCore != null) {
            mBindingXCore.doUnbind(params);
        }
    }

    @SuppressWarnings("unused")
    public void unbindAll() {
        if(mBindingXCore != null) {
            mBindingXCore.doRelease();
        }
    }

    @SuppressWarnings("unused")
    public void onActivityPause() {
        if (mBindingXCore != null) {
            mBindingXCore.onActivityPause();
        }
    }

    @SuppressWarnings("unused")
    public void onActivityResume() {
        if (mBindingXCore != null) {
            mBindingXCore.onActivityResume();
        }
    }

    @SuppressWarnings("unused")
    public void onDestroy() {
        if(mBindingXCore != null) {
            mBindingXCore.doRelease();
            mBindingXCore = null;
            NativeViewUpdateService.clearCallbacks();
        }
    }

    private PlatformManager createPlatformManager(@NonNull PlatformManager.IViewFinder finder, @NonNull PlatformManager.IDeviceResolutionTranslator translator) {
        return new PlatformManager.Builder()
                .withViewFinder(finder)
                .withDeviceResolutionTranslator(translator)
                .withViewUpdater(new PlatformManager.IViewUpdater() {
                    @Override
                    public void synchronouslyUpdateViewOnUIThread(@NonNull View targetView, @NonNull String propertyName, @NonNull Object propertyValue, @NonNull PlatformManager.IDeviceResolutionTranslator translator, @NonNull Map<String, Object> config, Object... extension) {
                        NativeViewUpdateService.findUpdater(propertyName).update(
                                targetView,
                                propertyValue,
                                translator,
                                config);
                    }
                })
                .build();
    }

    private Map<String,Object> resolveParams(BindingXSpec spec) {
        // TODO
        return null;
    }

    static class ViewFinderProxy implements PlatformManager.IViewFinder {
        private NativeViewFinder mNativeViewFinder;
        ViewFinderProxy(@NonNull NativeViewFinder finder) {
            this.mNativeViewFinder = finder;
        }

        @Nullable
        @Override
        public View findViewBy(String ref, Object... extension) {
            if(extension == null || extension.length <= 0 || !(extension[0] instanceof View)) {
                return null;
            }
            return mNativeViewFinder.findViewBy((View) extension[0], ref);
        }
    }

}
