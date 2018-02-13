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

import com.alibaba.android.bindingx.core.PlatformManager;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JSMathTest {


    @Test
    public void applyToScope() throws Exception {
        Map<String,Object> scope = new HashMap<>();
        JSMath.applyToScope(scope);

        assertThat(scope.get("sin"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));

        assertThat(scope.get("cos"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("tan"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("asin"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("acos"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));

        assertThat(scope.get("atan"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("atan2"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("pow"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("exp"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));

        assertThat(scope.get("sqrt"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("cbrt"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("log"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("abs"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));

        assertThat(scope.get("sign"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("ceil"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("floor"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));

        assertThat(scope.get("round"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("max"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("min"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("PI"), notNullValue());

        assertThat(scope.get("E"), notNullValue());
        assertThat(scope.get("rgb"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("rgba"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("evaluateColor"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
    }

    @Test
    public void applyXYToScope() throws Exception{
        Map<String,Object> scope = new HashMap<>();
        PlatformManager.IDeviceResolutionTranslator translator = mock(PlatformManager.IDeviceResolutionTranslator.class);
        when(translator.nativeToWeb(anyDouble())).thenReturn(400d);

        JSMath.applyXYToScope(scope,100,200, translator);

        assertEquals(400d, scope.get("x"));
        assertEquals(400d, scope.get("y"));

    }

    @Test
    public void applyOrientationValuesToScope() throws Exception {
        Map<String,Object> scope = new HashMap<>();

        JSMath.applyOrientationValuesToScope(scope, 100,200,300,50,150,250,1,1,1);

        assertEquals(100d, scope.get("alpha"));
        assertEquals(200d, scope.get("beta"));
        assertEquals(300d, scope.get("gamma"));

        assertEquals(50d, scope.get("dalpha"));
        assertEquals(50d, scope.get("dbeta"));
        assertEquals(50d, scope.get("dgamma"));

        assertEquals(1d, scope.get("x"));
        assertEquals(1d, scope.get("y"));
        assertEquals(1d, scope.get("z"));

    }

    @Test
    public void applyTimingValuesToScope() throws Exception{
        Map<String,Object> scope = new HashMap<>();

        JSMath.applyTimingValuesToScope(scope, 1000d);
        assertEquals(1000d, scope.get("t"));
    }

    @Test
    public  void applyScrollValuesToScope() throws Exception{
        Map<String,Object> scope = new HashMap<>();

        PlatformManager.IDeviceResolutionTranslator translator = mock(PlatformManager.IDeviceResolutionTranslator.class);
        when(translator.nativeToWeb(anyDouble())).thenReturn(1d);

        JSMath.applyScrollValuesToScope(scope, 100, 0,10,0,0,0, translator);

        assertEquals(1d, scope.get("x"));
        assertEquals(1d, scope.get("y"));
        assertEquals(1d, scope.get("dx"));
        assertEquals(1d, scope.get("dy"));

        assertEquals(1d, scope.get("tdx"));
        assertEquals(1d, scope.get("tdy"));
    }

}