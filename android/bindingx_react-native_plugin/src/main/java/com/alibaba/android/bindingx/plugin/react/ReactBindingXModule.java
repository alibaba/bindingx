package com.alibaba.android.bindingx.plugin.react;

import android.support.annotation.Nullable;

import com.alibaba.android.bindingx.core.LogProxy;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

@ReactModule(name = ReactBindingXModule.NAME)
public final class ReactBindingXModule extends ReactContextBaseJavaModule implements LifecycleEventListener{

    /*package*/ static final String NAME = "bindingX";

    public ReactBindingXModule(ReactApplicationContext reactContext) {
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

    @ReactMethod
    public void prepare(ReadableMap params) {
        //TODO
    }

    public void bind(ReadableMap params) {
        //TODO
    }

    public void unbind(ReadableMap params) {
        //TODO
    }

    public void unbindAll() {
        //TODO
    }

    //TODO
    public Map<String, Object> getComputedStyle(@Nullable String ref) {
        //TODO
        return null;
    }

    @Override
    public void onHostResume() {
        //TODO
        LogProxy.d("host resume called");
    }

    @Override
    public void onHostPause() {
        //TODO
        LogProxy.d("host pause called");
    }

    @Override
    public void onHostDestroy() {
        //TODO
        LogProxy.d("host destroy called");
    }


    //TODO
//    @Override
//    public void onCatalystInstanceDestroy() {
//        super.onCatalystInstanceDestroy();
//    }

    //TODO
//    public List<String> supportFeatures() {
//        return Arrays.asList("pan", "orientation", "timing", "scroll");
//    }


}
