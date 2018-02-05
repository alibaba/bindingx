package com.alibaba.android.bindingx.plugin.react;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class RNUtils {

    public static int getInt(Object value, int defaultValue) {
        if(value == null || !(value instanceof String)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt((String) value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(Object value, String defaultValue) {
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }
}
