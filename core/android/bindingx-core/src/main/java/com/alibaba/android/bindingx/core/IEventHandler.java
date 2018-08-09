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
 * Interface for handle a specific event such as user's pan gesture.
 * The implementation of {@link IEventHandler} should register callbacks or listeners of that specific
 * event. And every implementation of this class must have a unique name and registered to {@link BindingXCore}
 * by calling the method {@link BindingXCore#registerEventHandler(String, BindingXCore.ObjectCreator)}.
 * {@link BindingXCore} will call function {@link IEventHandler#onBindExpression(String, Map, ExpressionPair, List, BindingXCore.JavaScriptCallback)}
 * to bind multiple expressions. When the event occurs, the implementation of {@link IEventHandler} should
 * execute all expressions that bind before and update views according to the result.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IEventHandler extends IEventInterceptor{

    /**
     * the lifecycle of create event handler which will only be called once if not created
     *
     * @param sourceRef the reference or tag of {@link android.view.View}
     * @param eventType the unique event type
     * @return boolean value whether create success or not
     * */
    boolean onCreate(@NonNull String sourceRef, @NonNull String eventType);

    /**
     * the lifecycle of start event handle which will be called after {@link IEventHandler#onCreate(String, String)}.
     * This class maybe called several times.
     *
     * @param sourceRef the reference or tag of {@link android.view.View}
     * @param eventType the unique event type
     * */
    void onStart(@NonNull String sourceRef, @NonNull String eventType);

    /**
     * the lifecycle of bind expressions
     *
     * @param eventType the unique event type
     * @param globalConfig global config
     * @param exitExpressionPair the exit expression
     * @param expressionArgs the expressions to update view
     * @param callback state callback
     *
     * */
    void onBindExpression(@NonNull String eventType,
                          @Nullable Map<String, Object> globalConfig,
                          @Nullable ExpressionPair exitExpressionPair,
                          @NonNull List<Map<String, Object>> expressionArgs,
                          @Nullable BindingXCore.JavaScriptCallback callback);

    /**
     * the lifecycle of handler been disabled
     *
     * @param sourceRef the reference or tag of {@link android.view.View}
     * @param eventType the unique event type
     *
     * @return boolean value whether disable success or not
     * */
    boolean onDisable(@NonNull String sourceRef, @NonNull String eventType);

    /**
     * the lifycycle of handler been destroyed
     * */
    void onDestroy();

    /**
     * {@link android.app.Activity} pause callback
     * */
    void onActivityPause();

    /**
     * {@link android.app.Activity} resume callback
     * */
    void onActivityResume();

    void setAnchorInstanceId(String anchorInstanceId);

    void setToken(String token);

    void setExtensionParams(Object[] params);
}
