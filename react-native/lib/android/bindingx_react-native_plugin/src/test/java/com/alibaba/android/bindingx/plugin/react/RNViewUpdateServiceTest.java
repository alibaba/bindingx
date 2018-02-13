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
package com.alibaba.android.bindingx.plugin.react;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RNViewUpdateServiceTest {

    @Test
    public void findInvoker() throws Exception {
        assertNotNull(RNViewUpdateService.findInvoker("opacity"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.translate"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.translateX"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.translateY"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.scale"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.scaleX"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.scaleY"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.rotate"));

        assertNotNull(RNViewUpdateService.findInvoker("transform.rotateZ"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.rotateX"));
        assertNotNull(RNViewUpdateService.findInvoker("transform.rotateY"));
        assertNotNull(RNViewUpdateService.findInvoker("background-color"));

        assertNotNull(RNViewUpdateService.findInvoker("color"));
        assertNotNull(RNViewUpdateService.findInvoker("scroll.contentOffset"));
        assertNotNull(RNViewUpdateService.findInvoker("scroll.contentOffsetX"));
        assertNotNull(RNViewUpdateService.findInvoker("scroll.contentOffsetY"));
    }

}