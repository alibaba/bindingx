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

/**
 * Description:
 *
 * a wrapper for {@link Log}
 *
 * Created by rowandjj(chuyi)<br/>
 */
public final class LogProxy {

    public static boolean sEnableLog = true;

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












