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

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.PlatformManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BindingXOrientationHandlerTest {

    private BindingXOrientationHandler mFakeOrientationHandler;

    @Mock
    PlatformManager platformManager;

    @Mock
    OrientationDetector orientationDetector;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakeOrientationHandler = new BindingXOrientationHandler(
                RuntimeEnvironment.systemContext,
                platformManager,
                orientationDetector
        );

    }

    @After
    public void tearDown() throws Exception {
        mFakeOrientationHandler = null;
    }

    @Test
    public void onCreate() throws Exception {
        when(orientationDetector.start(anyInt())).thenReturn(true);
        boolean success = mFakeOrientationHandler.onCreate("_ref", BindingXEventType.TYPE_ORIENTATION);

        assertEquals(true, success);
        verify(orientationDetector).addOrientationChangedListener(mFakeOrientationHandler);
        verify(orientationDetector).start(anyInt());
    }

    @Test
    public void onStart() throws Exception {
        // nope
    }

    @Test
    public void onBindExpression() throws Exception {
        // nope
    }

    @Test
    public void onDisable() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        bindExpressionTo(mFakeOrientationHandler, callback);
        when(orientationDetector.removeOrientationChangedListener(mFakeOrientationHandler))
                .thenReturn(true);

        boolean success = mFakeOrientationHandler.onDisable("_ref", BindingXEventType.TYPE_ORIENTATION);

        assertEquals(true, success);
        verify(callback).callback(Matchers.any(Object.class));
        verify(orientationDetector).removeOrientationChangedListener(mFakeOrientationHandler);
    }



    @Test
    public void onDestroy() throws Exception {
        mFakeOrientationHandler.onDestroy();

        verify(orientationDetector).removeOrientationChangedListener(mFakeOrientationHandler);
        verify(orientationDetector).stop();
    }

    @Test
    public void onOrientationChanged() throws Exception {
        // nope
    }

    @Test
    public void onExit() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        bindExpressionTo(mFakeOrientationHandler, callback);

        Map<String,Object> params = new HashMap<>();
        params.put("alpha", 1d);
        params.put("beta", 2d);
        params.put("gamma", 3d);

        mFakeOrientationHandler.onExit(params);

        verify(callback).callback(Matchers.any(Object.class));
    }

    @Test
    public void onActivityPause() throws Exception {
        mFakeOrientationHandler.onActivityPause();
        verify(orientationDetector).stop();
    }

    @Test
    public void onActivityResume() throws Exception {
        mFakeOrientationHandler.onActivityResume();
        verify(orientationDetector).start(anyInt());
    }

    private void bindExpressionTo(BindingXOrientationHandler handler, BindingXCore.JavaScriptCallback callback) {
        handler.onBindExpression(
                "fake_event_type",
                new HashMap<String, Object>(),
                ExpressionPair.create("_origin","_transformed"),
                new ArrayList<Map<String, Object>>(),
                callback
        );
    }
}