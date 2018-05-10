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

import android.util.Log;

import com.alibaba.android.bindingx.core.internal.BindingXConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class LogProxyTest {

    private static final String FAKE_MESSAGE = "fake_message";
    private static final String TAG = BindingXConstants.TAG;

    @Mock Throwable mFakeThrowable;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void i() throws Exception {
        LogProxy.sEnableLog = true;
        LogProxy.i(FAKE_MESSAGE);
        verifyStatic();
        Log.i(TAG, FAKE_MESSAGE);

        LogProxy.i(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic();
        Log.i(TAG, FAKE_MESSAGE, mFakeThrowable);
    }

    @Test
    public void i1() throws Exception {
        LogProxy.sEnableLog = false;

        LogProxy.i(FAKE_MESSAGE);
        verifyStatic(Mockito.times(0));
        Log.i(TAG, FAKE_MESSAGE);

        LogProxy.i(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic(Mockito.times(0));
        Log.i(TAG, FAKE_MESSAGE, mFakeThrowable);

        LogProxy.sEnableLog = true;
    }

    @Test
    public void v() throws Exception {
        LogProxy.sEnableLog = true;
        LogProxy.v(FAKE_MESSAGE);
        verifyStatic();
        Log.v(TAG, FAKE_MESSAGE);

        LogProxy.v(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic();
        Log.v(TAG, FAKE_MESSAGE, mFakeThrowable);
    }

    @Test
    public void v1() throws Exception {
        LogProxy.sEnableLog = false;

        LogProxy.v(FAKE_MESSAGE);
        verifyStatic(Mockito.times(0));
        Log.v(TAG, FAKE_MESSAGE);

        LogProxy.v(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic(Mockito.times(0));
        Log.v(TAG, FAKE_MESSAGE, mFakeThrowable);

        LogProxy.sEnableLog = true;
    }

    @Test
    public void d() throws Exception {
        LogProxy.sEnableLog = true;
        LogProxy.d(FAKE_MESSAGE);
        verifyStatic();
        Log.d(TAG, FAKE_MESSAGE);

        LogProxy.d(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic();
        Log.d(TAG, FAKE_MESSAGE, mFakeThrowable);
    }

    @Test
    public void d1() throws Exception {
        LogProxy.sEnableLog = false;

        LogProxy.d(FAKE_MESSAGE);
        verifyStatic(Mockito.times(0));
        Log.d(TAG, FAKE_MESSAGE);

        LogProxy.d(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic(Mockito.times(0));
        Log.d(TAG, FAKE_MESSAGE, mFakeThrowable);

        LogProxy.sEnableLog = true;
    }

    @Test
    public void w() throws Exception {
        LogProxy.sEnableLog = true;
        LogProxy.w(FAKE_MESSAGE);
        verifyStatic();
        Log.w(TAG, FAKE_MESSAGE);

        LogProxy.w(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic();
        Log.w(TAG, FAKE_MESSAGE, mFakeThrowable);
    }

    @Test
    public void w1() throws Exception {
        LogProxy.sEnableLog = false;

        LogProxy.w(FAKE_MESSAGE);
        verifyStatic(Mockito.times(0));
        Log.w(TAG, FAKE_MESSAGE);

        LogProxy.w(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic(Mockito.times(0));
        Log.w(TAG, FAKE_MESSAGE, mFakeThrowable);

        LogProxy.sEnableLog = true;
    }

    @Test
    public void e() throws Exception {
        LogProxy.sEnableLog = true;
        LogProxy.e(FAKE_MESSAGE);
        verifyStatic();
        Log.e(TAG, FAKE_MESSAGE);

        LogProxy.e(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic();
        Log.e(TAG, FAKE_MESSAGE, mFakeThrowable);
    }

    @Test
    public void e1() throws Exception {
        LogProxy.sEnableLog = false;

        LogProxy.e(FAKE_MESSAGE);
        verifyStatic(Mockito.times(0));
        Log.e(TAG, FAKE_MESSAGE);

        LogProxy.e(FAKE_MESSAGE, mFakeThrowable);
        verifyStatic(Mockito.times(0));
        Log.e(TAG, FAKE_MESSAGE, mFakeThrowable);

        LogProxy.sEnableLog = true;
    }

}