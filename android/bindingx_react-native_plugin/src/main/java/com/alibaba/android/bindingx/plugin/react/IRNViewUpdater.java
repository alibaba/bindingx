package com.alibaba.android.bindingx.plugin.react;

import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.android.bindingx.core.PlatformManager;
import com.facebook.react.uimanager.UIImplementation;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IRNViewUpdater {

    void invoke(int tag,
                @NonNull View targetView,
                @NonNull Object cmd,
                @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                @NonNull Map<String, Object> config,
                @NonNull UIImplementation implementation);
}
