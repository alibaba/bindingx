package com.alibaba.android.binding.plugin.weex.internal;

import android.support.annotation.NonNull;
import android.view.View;

import com.taobao.weex.ui.component.WXComponent;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

interface IExpressionInvoker {

    /**
     * 执行视图变换
     *
     * @param component 组件名称
     * @param targetView 组件对应的native view
     * @param cmd 值
     * @param config 扩展配置
     *
     * */
    void invoke(@NonNull WXComponent component, @NonNull View targetView,
                @NonNull Object cmd, @NonNull Map<String, Object> config);
}
