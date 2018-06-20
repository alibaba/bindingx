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

import android.text.TextUtils;

import com.alibaba.android.bindingx.core.internal.JSFunctionInterface;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description:
 *
 * this class allow you to register your own js function
 * which can be executed by expression engine
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXJSFunctionRegister {
    private final static BindingXJSFunctionRegister sInstance = new BindingXJSFunctionRegister();

    private final LinkedHashMap<String,JSFunctionInterface> mJSFunctionMap = new LinkedHashMap<>(8);

    public static BindingXJSFunctionRegister getInstance() {
        return sInstance;
    }

    @SuppressWarnings("unused")
    public void registerJSFunction(String functionName, JSFunctionInterface function) {
        if(!TextUtils.isEmpty(functionName) && function != null) {
            mJSFunctionMap.put(functionName, function);
        }
    }

    @SuppressWarnings("unused")
    public boolean unregisterJSFunction(String functionName) {
        if(!TextUtils.isEmpty(functionName)) {
            return mJSFunctionMap.remove(functionName) != null;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void clear() {
        mJSFunctionMap.clear();
    }

    public Map<String,JSFunctionInterface> getJSFunctions() {
        return Collections.unmodifiableMap(mJSFunctionMap);
    }
}
