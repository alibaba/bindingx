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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.BindingXTouchHandler;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.view.gesture.WXGesture;
import com.taobao.weex.ui.view.gesture.WXGestureObservable;
import com.taobao.weex.utils.WXUtils;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXGestureHandler extends BindingXTouchHandler{

    // whether open experimental features or not
    private boolean experimental = false;
    private WXGesture mWeexGestureHandler = null;

    public BindingXGestureHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
    }

    @Override
    public void setGlobalConfig(@Nullable Map<String, Object> globalConfig) {
        super.setGlobalConfig(globalConfig);
        if(globalConfig != null) {
            experimental = WXUtils.getBoolean(globalConfig.get("experimentalGestureFeatures"), false);
        }
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        if(!experimental) {
            //short road
            return super.onCreate(sourceRef, eventType);
        }

        String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
        WXComponent sourceComponent = WXModuleUtils.findComponentByRef(instanceId, sourceRef);
        if(sourceComponent == null) {
            return super.onCreate(sourceRef, eventType);
        }
        View view = sourceComponent.getHostView();
        if(!(view instanceof ViewGroup) || !(view instanceof WXGestureObservable)) {
            return super.onCreate(sourceRef, eventType);
        }

        try {
            WXGestureObservable gestureTarget = (WXGestureObservable) view;
            mWeexGestureHandler = gestureTarget.getGestureListener();
            if(mWeexGestureHandler != null) {
                mWeexGestureHandler.addOnTouchListener(this);
                LogProxy.d("[ExpressionGestureHandler] onCreate success. {source:" + sourceRef + ",type:" + eventType + "}");
                return true;
            } else {
                return super.onCreate(sourceRef, eventType);
            }
        }catch (Throwable e) {
            // fallback
            LogProxy.e("experimental gesture features open failed." + e.getMessage());
            return super.onCreate(sourceRef, eventType);
        }
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        boolean result = super.onDisable(sourceRef, eventType);
        if(experimental && mWeexGestureHandler != null) {
            try {
                result |= mWeexGestureHandler.removeTouchListener(this);
            }catch (Throwable e) {
                LogProxy.e("[ExpressionGestureHandler]  disabled failed." + e.getMessage());
            }
        }
        return result;
    }
}
