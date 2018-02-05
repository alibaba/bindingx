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

import android.support.annotation.Nullable;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

class OrientationEvaluator {
    private Quaternion quaternion = new Quaternion(0,0,0,1);

    private Double constraintAlpha = null;
    private Double constraintBeta = null;
    private Double constraintGamma = null;

    private double constraintAlphaOffset = 0;
    private double constraintBetaOffset = 0;
    private double constraintGammaOffset = 0;


    OrientationEvaluator(@Nullable Double constraintAlpha, @Nullable Double constraintBeta, @Nullable Double constraintGamma) {
        this.constraintAlpha = constraintAlpha;
        this.constraintBeta = constraintBeta;
        this.constraintGamma = constraintGamma;
    }

    /**
     * @param deviceAlpha 陀螺仪alpha数据
     * @param deviceBeta 陀螺仪beta数据
     * @param deviceGamma 陀螺仪gamma数据
     * @param normalizedAlpha 校正后的alpha
     * */
    Quaternion calculate(double deviceAlpha, double deviceBeta, double deviceGamma, double normalizedAlpha) {
        double alpha = Math.toRadians(constraintAlpha != null ? constraintAlpha : (normalizedAlpha + constraintAlphaOffset));// Z
        double beta = Math.toRadians(constraintBeta != null ? constraintBeta : (deviceBeta + constraintBetaOffset));// X
        double gamma = Math.toRadians(constraintGamma != null ? constraintGamma : (deviceGamma + constraintGammaOffset));// Y

        // 设备方向写死为0 纵向
        setObjectQuaternion(quaternion, alpha, beta, gamma, 0);
        return quaternion;
    }


    private final Vector3 ZEE = new Vector3(0,0,1);
    private final Euler EULER = new Euler();
    private final Quaternion Q0 = new Quaternion();
    private final Quaternion Q1 = new Quaternion(-Math.sqrt(0.5),0,0,Math.sqrt(0.5));

    private void setObjectQuaternion(Quaternion quaternion, double alpha, double beta, double gamma, double orient) {
        EULER.setValue(beta,alpha,-gamma, "YXZ"); // 'ZXY' for the device, but 'YXZ' for us
        quaternion.setFromEuler(EULER); // orient the device
        quaternion.multiply(Q1); // camera looks out the back of the device, not the top
        quaternion.multiply(Q0.setFromAxisAngle(ZEE, -orient)); // adjust for screen orientation
    }

}
