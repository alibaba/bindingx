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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BindingXTimingHandlerTest {

    @Mock
    AnimationFrame mAnimationFrame;

    @Mock
    PlatformManager mPlatformManager;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private BindingXTimingHandler mFakeTimingHandler;

    @Before
    public void setUp() throws Exception {
        mFakeTimingHandler = new BindingXTimingHandler(
                RuntimeEnvironment.systemContext,
                mPlatformManager,
                mAnimationFrame
        );
    }

    @After
    public void tearDown() throws Exception {
        mFakeTimingHandler = null;
    }

    @Test
    public void onBindExpression() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        mFakeTimingHandler.onBindExpression(
                BindingXEventType.TYPE_TIMING,
                new HashMap<String, Object>(),
                ExpressionPair.create("_origin","_transformed"),
                new ArrayList<Map<String, Object>>(),
                callback
        );

        verify(callback).callback(any(Object.class));
        verify(mAnimationFrame).clear();
        verify(mAnimationFrame).requestAnimationFrame(mFakeTimingHandler);
    }

    @Test
    public void onDisable() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        mFakeTimingHandler.onBindExpression(
                BindingXEventType.TYPE_TIMING,
                new HashMap<String, Object>(),
                ExpressionPair.create("_origin","_transformed"),
                new ArrayList<Map<String, Object>>(),
                callback
        );

        mFakeTimingHandler.onDisable("_ref",BindingXEventType.TYPE_TIMING);

        verify(callback,times(2)).callback(any(Object.class));
    }

    @Test
    public void onDestroy() throws Exception {
        mFakeTimingHandler.onDestroy();

        verify(mAnimationFrame).terminate();
    }

    @Test
    public void onExit() throws Exception {
        BindingXCore.JavaScriptCallback callback = mock(BindingXCore.JavaScriptCallback.class);
        mFakeTimingHandler.onBindExpression(
                BindingXEventType.TYPE_TIMING,
                new HashMap<String, Object>(),
                ExpressionPair.create("_origin","_transformed"),
                new ArrayList<Map<String, Object>>(),
                callback
        );

        Map<String,Object> map = new HashMap<>();
        map.put("t", 100d);
        mFakeTimingHandler.onExit(map);

        verify(callback,times(2)).callback(any(Object.class));
    }

}