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
package com.alibaba.android.bindingx.core;

import android.content.Context;

import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.core.internal.ExpressionPair;
import com.alibaba.android.bindingx.core.internal.Utils;

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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BindingXCoreTest {

    private static final String DUMMY_EVENT_TYPE = "dummy_event_type";

    @Mock PlatformManager mFakePlatformManager;
    @Mock IEventHandler mDummyEventHandler;


    private BindingXCore mFakeBindingXCore;
    private String mFakeInstanceId = "123";
    private String mAnchorInstanceId = "456";
    private String mAnchor = "789";
    private Map<String,Object> mParams = new HashMap<>();
    private BindingXCore.JavaScriptCallback mCallback = new BindingXCore.JavaScriptCallback() {
        @Override
        public void callback(Object params) {
        }
    };

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakeBindingXCore = new BindingXCore(mFakePlatformManager);

        Map<String,Object> prop = new HashMap<>();
        prop.put(BindingXConstants.KEY_ELEMENT,"element");
        prop.put(BindingXConstants.KEY_PROPERTY,"transform.opacity");
        prop.put(BindingXConstants.KEY_EXPRESSION, new ExpressionPair("x+1","{\"type\":\"+\",\"children\":[{\"type\":\"Identifier\",\"value\":\"x\"},{\"type\":\"NumericLiteral\",\"value\":1}]}"));

        List<Map<String,Object>> props = new ArrayList<>();
        props.add(prop);

        mParams.put(BindingXConstants.KEY_INSTANCE_ID, mAnchorInstanceId);
        mParams.put(BindingXConstants.KEY_ANCHOR, mAnchor);
        mParams.put(BindingXConstants.KEY_RUNTIME_PROPS, props);
    }

    @After
    public void tearDown() throws Exception {
        mFakeBindingXCore = null;
    }

    @Test
    public void doBind() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });

        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        String token = mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );
        assertEquals(mAnchor, token);
        verify(mDummyEventHandler).
                onBindExpression(BindingXEventType.TYPE_PAN, null, null, Utils.getRuntimeProps(mParams), mCallback);

    }

    @Test
    public void doUnbind() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });

        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        String token = mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );

        mFakeBindingXCore.doUnbind(token, BindingXEventType.TYPE_PAN);
        verify(mDummyEventHandler).onDisable(token, BindingXEventType.TYPE_PAN);
    }

    @Test
    public void doUnbind1() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });

        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        String token = mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );

        Map<String,Object> params = new HashMap<>();
        params.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        params.put(BindingXConstants.KEY_TOKEN, token);
        mFakeBindingXCore.doUnbind(params);
        verify(mDummyEventHandler).onDisable(token, BindingXEventType.TYPE_PAN);
    }

    @Test
    public void doRelease() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });
        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );

        mFakeBindingXCore.doRelease();
        verify(mDummyEventHandler).onDestroy();
    }

    @Test
    public void doPrepare() throws Exception {
        // given
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, DUMMY_EVENT_TYPE);
        mFakeBindingXCore.registerEventHandler(DUMMY_EVENT_TYPE, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context c, PlatformManager p, Object... extension) {
                return mDummyEventHandler;
            }
        });

        // when
        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);

        mFakeBindingXCore.doPrepare(RuntimeEnvironment.systemContext, mFakeInstanceId, mAnchor, mAnchorInstanceId, DUMMY_EVENT_TYPE);

        // then
        verify(mDummyEventHandler).setAnchorInstanceId(mAnchorInstanceId);
        verify(mDummyEventHandler).setToken(mAnchor);
        verify(mDummyEventHandler).onCreate(mAnchor, DUMMY_EVENT_TYPE);
        verify(mDummyEventHandler).onStart(mAnchor, DUMMY_EVENT_TYPE);
    }

    @Test
    public void onActivityPause() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });
        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );

        mFakeBindingXCore.onActivityPause();
        verify(mDummyEventHandler).onActivityPause();
    }

    @Test
    public void onActivityResume() throws Exception {
        mFakeBindingXCore.registerEventHandler(BindingXEventType.TYPE_PAN, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context p1, PlatformManager p2, Object... extension) {
                return mDummyEventHandler;
            }
        });
        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, BindingXEventType.TYPE_PAN);
        mFakeBindingXCore.doBind(
                RuntimeEnvironment.systemContext,
                mFakeInstanceId,
                mParams,
                mCallback
        );

        mFakeBindingXCore.onActivityResume();
        verify(mDummyEventHandler).onActivityResume();
    }

    @Test
    public void registerEventHandler() throws Exception {
        // given
        mParams.put(BindingXConstants.KEY_EVENT_TYPE, DUMMY_EVENT_TYPE);
        mFakeBindingXCore.registerEventHandler(DUMMY_EVENT_TYPE, new BindingXCore.ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(Context c, PlatformManager p, Object... extension) {
                return mDummyEventHandler;
            }
        });

        // when
        when(mDummyEventHandler.onCreate(anyString(),anyString())).thenReturn(true);

        mFakeBindingXCore.doPrepare(RuntimeEnvironment.systemContext, mFakeInstanceId, mAnchor, mAnchorInstanceId, DUMMY_EVENT_TYPE);

        // then
        verify(mDummyEventHandler).setAnchorInstanceId(mAnchorInstanceId);
        verify(mDummyEventHandler).setToken(mAnchor);
        verify(mDummyEventHandler).onCreate(mAnchor, DUMMY_EVENT_TYPE);
        verify(mDummyEventHandler).onStart(mAnchor, DUMMY_EVENT_TYPE);
    }

}