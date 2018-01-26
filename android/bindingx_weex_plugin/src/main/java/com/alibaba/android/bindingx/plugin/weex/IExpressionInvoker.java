package com.alibaba.android.bindingx.plugin.weex;

import android.support.annotation.NonNull;
import android.view.View;

import com.taobao.weex.ui.component.WXComponent;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IExpressionInvoker {

    /**
     * 执行视图变换
     *
     * @param component 组件名称
     * @param targetView 组件对应的native view
     * @param cmd 值
     * @param translator 分辨率计算
     * @param config 扩展配置
     *
     * */
    void invoke(@NonNull WXComponent component,
                @NonNull View targetView,
                @NonNull Object cmd,
                @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                @NonNull Map<String, Object> config);
}
