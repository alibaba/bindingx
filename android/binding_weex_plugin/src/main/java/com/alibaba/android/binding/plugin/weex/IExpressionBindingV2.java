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

public interface IExpressionBindingV2 {

    /**
     * 开启绑定，此方法仅用于`eventType`为`pan`的情况。
     * 其他类型的`eventType`可不调用此方法。
     *
     * @param params JSON格式参数
     *                - anchor: 锚点，待绑定的view引用(**可选**)
     *                - eventType: 事件类型*
     * */
    void prepare(Map<String, Object> params);

    /**
     * 创建一个支持特定事件类型的ExpressionBinding实例。
     * 当特定事件触发时，将会执行相应的表达式集合，并对指定的视图元素进行视图变换。
     *
     * @param params JSON格式参数
     *           - anchor: 锚点(**可选**, 目前仅`pan`/`scroll`需要指定锚点)
     *           - eventType: 事件类型(如`pan`),必选。
     *           - exitExpression: 边界条件,可选
     *               - transformed(`String`): 转化后的表达式
     *               - origin(`String`): 原始表达式
     *           - props: 运行时参数列表(`JSONArray`)，必选
     *               - element(`String`): 作用的元素(View)
     *               - property(`String`): 作用的属性
     *               - expression(`String`): 运行时的表达式
     *                   - transformed(`String`): 转化后的表达式
     *                   - origin(`String`): 原始表达式
     *               - config: 额外配置
     *                   - perspective(`Int`): 透视
     *                   - transformOrigin(`String`): 轴心
     * @param callback 状态回调
     *
     * @return 结果
     *          - token: bind成功则返回token值否则返回null
     * */
    Map<String,String> bind(Map<String, Object> params, JSCallback callback);

    /**
     * 解绑指定的ExpressionBinding实例。
     *
     * @param params JSON格式参数
     *        - token: 由`bind`方法返回的特定ExpressionBinding实例
     *        - eventType: 事件类型(如`pan`)
     * */
    void unbind(Map<String, Object> params);

    /**
     * 解绑所有的ExpressionBinding实例。
     *
     * */
    void unbindAll();

    /**
     * 返回当前组件支持的EventType列表
     * */
    List<String> supportFeatures();

    /**
     * 返回目标元素的transform信息和opacity信息
     *
     * */
    Map<String,Object> getComputedStyle(@Nullable String ref);
}
