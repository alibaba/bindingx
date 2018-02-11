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

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import com.alibaba.android.bindingx.core.LogProxy;

import java.util.List;

class SensorManagerProxyImpl implements SensorManagerProxy {
    private final SensorManager mSensorManager;

    SensorManagerProxyImpl(SensorManager sensorManager) {
        mSensorManager = sensorManager;
    }

    @Override
    public boolean registerListener(SensorEventListener listener, int sensorType, int rate,
                                    Handler handler) {
        List<Sensor> sensors = mSensorManager.getSensorList(sensorType);
        if (sensors.isEmpty()) {
            return false;
        }
        return mSensorManager.registerListener(listener, sensors.get(0), rate, handler);
    }

    @Override
    public void unregisterListener(SensorEventListener listener, int sensorType) {
        List<Sensor> sensors = mSensorManager.getSensorList(sensorType);
        if (sensors.isEmpty()) {
            return;
        }
        try {
            mSensorManager.unregisterListener(listener, sensors.get(0));
        } catch (Throwable e) {
            // Suppress occasional exception on Digma iDxD* devices:
            // Receiver not registered: android.hardware.SystemSensorManager$1
            LogProxy.w("Failed to unregister device sensor " + sensors.get(0).getName());
        }
    }
}