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

import android.util.Log;

import com.alibaba.android.bindingx.core.internal.BindingXConstants;

import java.util.Map;

/**
 * Description:
 *
 * a wrapper for {@link Log}.
 * In release mode, you can set {@link LogProxy#sEnableLog} to false.
 *
 * Created by rowandjj(chuyi)<br/>
 */
public final class LogProxy {

    private static final String KEY_DEBUG = "debug";
    public static boolean sEnableLog = false;

    public static void enableLogIfNeeded(Map<String, Object> params) {
        if(params == null) {
            return;
        }
        Object obj = params.get(KEY_DEBUG);
        if(obj == null) {
            return;
        }
        boolean result = false;
        if(obj instanceof Boolean) {
            result = (boolean) obj;
        } else if(obj instanceof String) {
            String s = (String) obj;
            result = "true".equals(s);
        }
        sEnableLog = result;
    }

    public static void i(String message) {
        if(sEnableLog) {
            Log.i(BindingXConstants.TAG, message);
        }
    }

    public static void i(String message, Throwable e) {
        if(sEnableLog) {
            Log.i(BindingXConstants.TAG,message, e);
        }
    }

    public static void v(String message) {
        if(sEnableLog) {
            Log.v(BindingXConstants.TAG, message);
        }
    }

    public static void v(String message, Throwable e) {
        if(sEnableLog) {
            Log.v(BindingXConstants.TAG,message, e);
        }
    }

    public static void d(String message) {
        if(sEnableLog) {
            Log.d(BindingXConstants.TAG, message);
        }
    }

    public static void d(String message, Throwable e) {
        if(sEnableLog) {
            Log.d(BindingXConstants.TAG,message, e);
        }
    }

    public static void w(String message) {
        if(sEnableLog) {
            Log.w(BindingXConstants.TAG, message);
        }
    }

    public static void w(String message, Throwable e) {
        if(sEnableLog) {
            Log.w(BindingXConstants.TAG,message, e);
        }
    }

    public static void e(String message) {
        if(sEnableLog) {
            Log.e(BindingXConstants.TAG, message);
        }
    }

    public static void e(String message, Throwable e) {
        if(sEnableLog) {
            Log.e(BindingXConstants.TAG,message, e);
        }
    }

}












