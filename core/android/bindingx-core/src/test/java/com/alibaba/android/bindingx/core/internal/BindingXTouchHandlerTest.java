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

import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.PlatformManager;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BindingXTouchHandlerTest {

    private BindingXTouchHandler mFakeTouchHandler;

    @Mock
    PlatformManager manager;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakeTouchHandler = new BindingXTouchHandler(
                RuntimeEnvironment.systemContext,
                manager
        );

    }

    @After
    public void tearDown() throws Exception {
        mFakeTouchHandler = null;
    }

    @Test
    public void onCreate() throws Exception {
        PlatformManager.IViewFinder finder = mock(PlatformManager.IViewFinder.class);
        View view = mock(View.class);
        when(manager.getViewFinder()).thenReturn(finder);
        when(finder.findViewBy(anyString(),anyString())).thenReturn(view);

        mFakeTouchHandler.onCreate("_ref", BindingXEventType.TYPE_PAN);

        verify(view).setOnTouchListener(mFakeTouchHandler);
    }


    @Test
    public void onDisable() throws Exception {
        PlatformManager.IViewFinder finder = mock(PlatformManager.IViewFinder.class);
        View view = mock(View.class);
        when(manager.getViewFinder()).thenReturn(finder);
        when(finder.findViewBy(anyString(),anyString())).thenReturn(view);

        mFakeTouchHandler.setFlickGestureAvailable(false);
        mFakeTouchHandler.setPanGestureAvailable(false);

        mFakeTouchHandler.onDisable("_ref", BindingXEventType.TYPE_PAN);

        verify(view).setOnTouchListener(null);
    }

    @Test
    public void onExit() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        mFakeTouchHandler.onBindExpression(
                BindingXEventType.TYPE_PAN,
                new HashMap<String, Object>(),
                ExpressionPair.create("_origin","_transformed"),
                new ArrayList<Map<String, Object>>(),
                callback
        );

        PlatformManager.IDeviceResolutionTranslator translator = mock(PlatformManager.IDeviceResolutionTranslator.class);
        when(manager.getResolutionTranslator()).thenReturn(translator);
        when(translator.nativeToWeb(anyDouble())).thenReturn(1d);

        Map<String,Object> map = new HashMap<>();
        map.put("internal_x", 1d);
        map.put("internal_y", 1d);

        mFakeTouchHandler.onExit(map);

        verify(callback).callback(any(Object.class));
    }

}