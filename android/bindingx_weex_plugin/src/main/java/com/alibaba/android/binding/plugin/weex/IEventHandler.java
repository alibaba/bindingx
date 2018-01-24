package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.binding.plugin.weex.internal.ExpressionPair;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IEventHandler {

    boolean onCreate(@NonNull String sourceRef, @NonNull String eventType);

    void onStart(@NonNull String sourceRef, @NonNull String eventType);

    void onBindExpression(@NonNull String eventType, @Nullable Map<String, Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                          @NonNull List<Map<String, Object>> expressionArgs, @Nullable ExpressionBindingCore.JavaScriptCallback callback);

    boolean onDisable(@NonNull String sourceRef, @NonNull String eventType);

    void onDestroy();

    void onActivityPause();

    void onActivityResume();

    void setAnchorInstanceId(String anchorInstanceId);
}
