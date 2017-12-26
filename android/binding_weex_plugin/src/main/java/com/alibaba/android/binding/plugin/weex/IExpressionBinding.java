package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.Nullable;

import com.taobao.weex.bridge.JSCallback;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IExpressionBinding {

    /**
     * 注册手势监听器
     *
     * {View:{eventType:{PanListener},...}}
     *
     * */
    @Deprecated
    void enableBinding(@Nullable String sourceRef, @Nullable String eventType);

    /**
     * 绑定表达式
     *
     * 当满足exit边界条件或者手势到达cancel/up时，需要移除expressionArgs
     *
     * @param expressionArgs 格式形如{targetView:{property:expression},...}
     * */
    @Deprecated
    void createBinding(@Nullable String sourceRef, @Nullable String eventType, @Nullable String exitExpression,
                       @Nullable List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback);

    /**取消注册手势监听*/
    @Deprecated
    void disableBinding(@Nullable String sourceRef, @Nullable String eventType);

    /**取消所有手势监听器*/
    @Deprecated
    void disableAll();
}
