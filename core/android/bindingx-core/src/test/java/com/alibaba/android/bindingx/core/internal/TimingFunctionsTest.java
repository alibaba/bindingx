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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


public class TimingFunctionsTest {

    @Test
    public void applyToScope() throws Exception {
        Map<String,Object> scope = new HashMap<>();
        TimingFunctions.applyToScope(scope);

        assertThat(scope.get("linear"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInQuad"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutQuad"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInQuad"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutQuad"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInCubic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutCubic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutCubic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInQuart"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutQuart"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutQuart"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInQuint"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutQuint"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutQuint"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInSine"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutSine"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutSine"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInExpo"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutExpo"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutExpo"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInCirc"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutCirc"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutCirc"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInElastic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutElastic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutElastic"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInBack"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutBack"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutBack"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInBounce"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeOutBounce"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("easeInOutBounce"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
        assertThat(scope.get("cubicBezier"),allOf(notNullValue(),instanceOf(JSFunctionInterface.class)));
    }

}