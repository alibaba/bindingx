package com.alibaba.android.bindingx.plugin.weex;

import android.support.annotation.Nullable;
import android.view.View;

import com.taobao.weex.WXSDKManager;
import com.taobao.weex.ui.component.WXComponent;

public class WXModuleUtils {
    private WXModuleUtils() {}

    @Nullable
    public static View findViewByRef(@Nullable String instanceId, @Nullable String ref) {
        WXComponent component = findComponentByRef(instanceId, ref);
        if (component == null) {
            return null;
        }
        return component.getHostView();
    }

    @Nullable
    public static WXComponent findComponentByRef(@Nullable String instanceId, @Nullable String ref) {
        return WXSDKManager.getInstance().getWXRenderManager().getWXComponent(instanceId, ref);
    }
}
