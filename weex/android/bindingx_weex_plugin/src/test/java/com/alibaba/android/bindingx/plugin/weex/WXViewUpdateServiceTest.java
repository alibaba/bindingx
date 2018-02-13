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

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class WXViewUpdateServiceTest {

    @Test
    public void findInvoker() throws Exception {
        assertNotNull(WXViewUpdateService.findInvoker("opacity"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.translate"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.translateX"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.translateY"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.scale"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.scaleX"));

        assertNotNull(WXViewUpdateService.findInvoker("transform.scaleY"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.rotate"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.rotateZ"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.rotateX"));
        assertNotNull(WXViewUpdateService.findInvoker("transform.rotateY"));
        assertNotNull(WXViewUpdateService.findInvoker("width"));
        assertNotNull(WXViewUpdateService.findInvoker("height"));

        assertNotNull(WXViewUpdateService.findInvoker("background-color"));
        assertNotNull(WXViewUpdateService.findInvoker("color"));
        assertNotNull(WXViewUpdateService.findInvoker("scroll.contentOffset"));
        assertNotNull(WXViewUpdateService.findInvoker("scroll.contentOffsetX"));
        assertNotNull(WXViewUpdateService.findInvoker("scroll.contentOffsetY"));
    }

}