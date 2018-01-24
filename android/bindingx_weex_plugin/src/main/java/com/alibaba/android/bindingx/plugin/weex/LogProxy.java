package com.alibaba.android.bindingx.plugin.weex;

import android.util.Log;

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
            Log.i(ExpressionConstants.TAG, message);
        }
    }

    public static void i(String message, Throwable e) {
        if(sEnableLog) {
            Log.i(ExpressionConstants.TAG,message, e);
        }
    }

    public static void v(String message) {
        if(sEnableLog) {
            Log.v(ExpressionConstants.TAG, message);
        }
    }

    public static void v(String message, Throwable e) {
        if(sEnableLog) {
            Log.v(ExpressionConstants.TAG,message, e);
        }
    }

    public static void d(String message) {
        if(sEnableLog) {
            Log.d(ExpressionConstants.TAG, message);
        }
    }

    public static void d(String message, Throwable e) {
        if(sEnableLog) {
            Log.d(ExpressionConstants.TAG,message, e);
        }
    }

    public static void w(String message) {
        if(sEnableLog) {
            Log.w(ExpressionConstants.TAG, message);
        }
    }

    public static void w(String message, Throwable e) {
        if(sEnableLog) {
            Log.w(ExpressionConstants.TAG,message, e);
        }
    }

    public static void e(String message) {
        if(sEnableLog) {
            Log.e(ExpressionConstants.TAG, message);
        }
    }

    public static void e(String message, Throwable e) {
        if(sEnableLog) {
            Log.e(ExpressionConstants.TAG,message, e);
        }
    }

}












