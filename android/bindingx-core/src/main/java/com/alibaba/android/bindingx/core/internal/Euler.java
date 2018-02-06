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

import android.text.TextUtils;

/**
 * Description:
 * an euler data structure
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
