package com.alibaba.android.bindingx.core.internal;

import android.text.TextUtils;

/**
 * Description:
 * 欧拉角
 *
 * Created by rowandjj(chuyi)<br/>
 */

class Euler {

    private static final String DEFAULT_ORDER = "XYZ";

    String order;

    double x;
    double y;
    double z;

    boolean isEuler = true;

    Euler() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    void setValue(double x, double y, double z, String order) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.order = TextUtils.isEmpty(order) ? DEFAULT_ORDER : order;
    }

}
