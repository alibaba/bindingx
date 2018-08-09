package com.alibaba.android.bindingx.plugin.android;

import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.android.bindingx.core.PlatformManager;

import java.util.Map;

public interface INativeViewUpdater {

    void update(@NonNull View targetView,
                @NonNull Object cmd,
                @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                @NonNull Map<String, Object> config);
}