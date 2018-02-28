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
        assertNotNull(WXViewUpdateService.findUpdater("opacity"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.translate"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.translateX"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.translateY"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.scale"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.scaleX"));

        assertNotNull(WXViewUpdateService.findUpdater("transform.scaleY"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.rotate"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.rotateZ"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.rotateX"));
        assertNotNull(WXViewUpdateService.findUpdater("transform.rotateY"));
        assertNotNull(WXViewUpdateService.findUpdater("width"));
        assertNotNull(WXViewUpdateService.findUpdater("height"));

        assertNotNull(WXViewUpdateService.findUpdater("background-color"));
        assertNotNull(WXViewUpdateService.findUpdater("color"));
        assertNotNull(WXViewUpdateService.findUpdater("scroll.contentOffset"));
        assertNotNull(WXViewUpdateService.findUpdater("scroll.contentOffsetX"));
        assertNotNull(WXViewUpdateService.findUpdater("scroll.contentOffsetY"));
    }

}