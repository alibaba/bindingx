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
package com.alibaba.android.bindingx.core.internal;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 *
 * global constants
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXConstants {
    public static final String TAG = "BindingX";

    public static final String STATE_READY = "ready";
    public static final String STATE_START = "start";
    public static final String STATE_END = "end";
    public static final String STATE_CANCEL = "cancel";
    public static final String STATE_EXIT = "exit";
    public static final String STATE_TURNING = "turn";
    public static final String STATE_INTERCEPTOR = "interceptor";

    public static final String KEY_ELEMENT = "element";
    public static final String KEY_PROPERTY = "property";
    public static final String KEY_EXPRESSION = "expression";
    public static final String KEY_CONFIG = "config";
    public static final String KEY_OPTIONS = "options";


    public static final String KEY_ANCHOR = "anchor";
    public static final String KEY_EVENT_TYPE = "eventType";
    public static final String KEY_INSTANCE_ID = "instanceId";
    public static final String KEY_EXIT_EXPRESSION = "exitExpression";
    public static final String KEY_RUNTIME_PROPS = "props";
    public static final String KEY_INTERCEPTORS = "interceptors";
    public static final String KEY_TOKEN = "token";

    public static final String KEY_SCENE_TYPE = "sceneType";

    public static final String KEY_TRANSFORMED = "transformed";
    public static final String KEY_ORIGIN = "origin";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({BindingXConstants.STATE_START, BindingXConstants.STATE_END,
            BindingXConstants.STATE_CANCEL, BindingXConstants.STATE_EXIT, BindingXConstants.STATE_TURNING, BindingXConstants.STATE_INTERCEPTOR})
    public @interface State {
    }
}
