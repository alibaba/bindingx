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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 *
 *  you may occur below problem when run unit test.
 *  Try to delete .gradle folder and run again.
 *
 *  Invalid default: public abstract java.lang.Class org.robolectric.annotation.Config.application()
 *
 * */
@RunWith(RobolectricTestRunner.class)
public class PlatformManagerTest {

    private PlatformManager mFakePlatformManager;

    @Mock
    PlatformManager.IViewFinder mFakeFinder;
    @Mock
    PlatformManager.IDeviceResolutionTranslator mFakeTranslator;
    @Mock
    PlatformManager.IViewUpdater mFakeUpdater;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mFakePlatformManager = new PlatformManager
                .Builder()
                .withViewFinder(mFakeFinder)
                .withViewUpdater(mFakeUpdater)
                .withDeviceResolutionTranslator(mFakeTranslator)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mFakePlatformManager = null;
    }

    @Test
    public void getResolutionTranslator() throws Exception {
        assertThat(mFakePlatformManager.getResolutionTranslator(),equalTo(mFakeTranslator));
    }

    @Test
    public void getViewFinder() throws Exception {
        assertThat(mFakePlatformManager.getViewFinder(), equalTo(mFakeFinder));
    }

    @Test
    public void getViewUpdater() throws Exception {
        assertThat(mFakePlatformManager.getViewUpdater(), equalTo(mFakeUpdater));
    }

}