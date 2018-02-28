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
        assertNotNull(RNViewUpdateService.findUpdater("opacity"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.translate"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.translateX"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.translateY"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.scale"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.scaleX"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.scaleY"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.rotate"));

        assertNotNull(RNViewUpdateService.findUpdater("transform.rotateZ"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.rotateX"));
        assertNotNull(RNViewUpdateService.findUpdater("transform.rotateY"));
        assertNotNull(RNViewUpdateService.findUpdater("background-color"));

        assertNotNull(RNViewUpdateService.findUpdater("color"));
        assertNotNull(RNViewUpdateService.findUpdater("scroll.contentOffset"));
        assertNotNull(RNViewUpdateService.findUpdater("scroll.contentOffsetX"));
        assertNotNull(RNViewUpdateService.findUpdater("scroll.contentOffsetY"));
    }

}