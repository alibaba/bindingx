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

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class OrientationDetectorTest {

    private OrientationDetector mOrientationDetector;

    @Mock
    SensorManagerProxy mDummySensorManagerProxy;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mOrientationDetector = OrientationDetector.getInstance(RuntimeEnvironment.systemContext);
        mOrientationDetector.mSensorManagerProxy = mDummySensorManagerProxy;
    }

    @After
    public void tearDown() throws Exception {
        mOrientationDetector = null;
    }

    @Test
    public void getInstance() throws Exception {
        assertThat(mOrientationDetector,equalTo(OrientationDetector.getInstance(RuntimeEnvironment.systemContext)));
    }

    @Test
    public void addOrientationChangedListener() throws Exception {
        OrientationDetector.OnOrientationChangedListener listener = mock(OrientationDetector.OnOrientationChangedListener.class);
        mOrientationDetector.addOrientationChangedListener(listener);
        mOrientationDetector.gotOrientation(1,2,3);
        verify(listener).onOrientationChanged(1,2,3);
    }

    @Test
    public void removeOrientationChangedListener() throws Exception {
        OrientationDetector.OnOrientationChangedListener listener = mock(OrientationDetector.OnOrientationChangedListener.class);
        mOrientationDetector.addOrientationChangedListener(listener);
        mOrientationDetector.removeOrientationChangedListener(listener);
        mOrientationDetector.gotOrientation(1,2,3);
        verify(listener, times(0)).onOrientationChanged(anyDouble(),anyDouble(),anyDouble());
    }

    @Test
    public void start() throws Exception {
        when(mDummySensorManagerProxy.registerListener(
                any(SensorEventListener.class),
                anyInt(),
                anyInt(),
                any(Handler.class)
        )).thenReturn(true);

        boolean result = mOrientationDetector.start(SensorManager.SENSOR_DELAY_GAME);

        assertEquals(true, result);

        verify(mDummySensorManagerProxy).registerListener(
                any(SensorEventListener.class),
                anyInt(),
                anyInt(),
                any(Handler.class)
        );
    }

    @Test
    public void stop() throws Exception {
        when(mDummySensorManagerProxy.registerListener(
                any(SensorEventListener.class),
                anyInt(),
                anyInt(),
                any(Handler.class)
        )).thenReturn(true);
        mOrientationDetector.start(SensorManager.SENSOR_DELAY_GAME);

        mOrientationDetector.stop();
        verify(mDummySensorManagerProxy).unregisterListener(any(SensorEventListener.class), anyInt());
    }

}