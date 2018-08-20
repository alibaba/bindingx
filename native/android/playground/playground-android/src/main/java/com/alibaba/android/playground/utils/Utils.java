package com.alibaba.android.playground.utils;

import android.content.Context;
import android.util.Pair;
import android.util.TypedValue;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class Utils {
    private Utils(){}

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Pair<Integer,Integer> getScreenSize(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        return Pair.create(width, height);
    }
}
