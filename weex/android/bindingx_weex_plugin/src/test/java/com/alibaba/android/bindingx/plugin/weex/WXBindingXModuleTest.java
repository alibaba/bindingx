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
package com.alibaba.android.bindingx.plugin.weex;

import android.content.Context;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.taobao.weex.bridge.JSCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WXBindingXModuleTest {

    private WXBindingXModule mFakeModule;

    @Mock
    BindingXCore mCore;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakeModule = new WXBindingXModule(mCore);
    }

    @After
    public void tearDown() throws Exception {
        mFakeModule = null;
    }


    @Test
    public void bind() throws Exception {

        mFakeModule.bind(new HashMap<String, Object>(), new JSCallback() {
            @Override
            public void invoke(Object data) {

            }

            @Override
            public void invokeAndKeepAlive(Object data) {

            }
        });

        verify(mCore).doBind(
                any(Context.class),
                anyString(),
                anyMap(),
                any(BindingXCore.JavaScriptCallback.class)
                );

    }

    @Test
    public void unbind() throws Exception {
        Map<String,Object> map = new HashMap<>();
        mFakeModule.unbind(map);
        verify(mCore).doUnbind(map);
    }

    @Test
    public void unbindAll() throws Exception {
        mFakeModule.unbindAll();
        verify(mCore).doRelease();
    }

    @Test
    public void supportFeatures() throws Exception {
        List<String> result = mFakeModule.supportFeatures();
        assertNotNull(result);
    }

}