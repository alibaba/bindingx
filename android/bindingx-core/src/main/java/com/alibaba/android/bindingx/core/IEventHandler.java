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
package com.alibaba.android.bindingx.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.bindingx.core.internal.ExpressionPair;

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

    void onBindExpression(@NonNull String eventType,
                          @Nullable Map<String, Object> globalConfig,
                          @Nullable ExpressionPair exitExpressionPair,
                          @NonNull List<Map<String, Object>> expressionArgs,
                          @Nullable BindingXCore.JavaScriptCallback callback);

    boolean onDisable(@NonNull String sourceRef, @NonNull String eventType);

    void onDestroy();

    void onActivityPause();

    void onActivityResume();

    void setAnchorInstanceId(String anchorInstanceId);

    void setToken(String token);
}
