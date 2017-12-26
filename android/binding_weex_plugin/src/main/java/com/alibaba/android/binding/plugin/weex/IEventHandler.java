package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.binding.plugin.weex.internal.ExpressionPair;
import com.taobao.weex.bridge.JSCallback;

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

//    void onBindExpression(@NonNull String eventType, @Nullable String exitExpression,
//                          @NonNull List<Map<String, String>> expressionArgs, @Nullable JSCallback callback);

    void onBindExpression(@NonNull String eventType, @Nullable Map<String, Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                          @NonNull List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback);

    boolean onDisable(@NonNull String sourceRef, @NonNull String eventType);

    void onDestroy();

    void onActivityPause();

    void onActivityResume();

    void setAnchorInstanceId(String anchorInstanceId);
}
