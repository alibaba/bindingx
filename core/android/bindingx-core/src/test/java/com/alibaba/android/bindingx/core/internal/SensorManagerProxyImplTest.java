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
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SensorManagerProxyImplTest {

    private SensorManagerProxy mFakeSensorManagerProxy;

    @Mock SensorManager dummySensorManager;
    @Mock Sensor dummySensor;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakeSensorManagerProxy = new SensorManagerProxyImpl(dummySensorManager);
    }

    @After
    public void tearDown() throws Exception {
        mFakeSensorManagerProxy = null;
    }

    @Test
    public void registerListener() throws Exception {
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        Handler handler = new Handler();

        List<Sensor> targetSensorList = Collections.singletonList(dummySensor);
        when(dummySensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).thenReturn(targetSensorList);
        mFakeSensorManagerProxy.registerListener(listener, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME, handler);
        verify(dummySensorManager).registerListener(
                listener,
                targetSensorList.get(0),
                SensorManager.SENSOR_DELAY_GAME,
                handler);
    }

    @Test
    public void unregisterListener() throws Exception {

        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        List<Sensor> targetSensorList = Collections.singletonList(dummySensor);
        when(dummySensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)).thenReturn(targetSensorList);
        mFakeSensorManagerProxy.unregisterListener(listener,Sensor.TYPE_ACCELEROMETER);
        verify(dummySensorManager).unregisterListener(listener, targetSensorList.get(0));
    }

}