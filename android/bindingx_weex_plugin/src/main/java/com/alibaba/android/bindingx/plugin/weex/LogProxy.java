package com.alibaba.android.bindingx.plugin.weex;

import android.util.Log;

import com.alibaba.android.bindingx.plugin.weex.internal.BindingXConstants;

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












