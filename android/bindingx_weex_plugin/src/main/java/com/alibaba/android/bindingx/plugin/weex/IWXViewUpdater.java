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

import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.android.bindingx.core.PlatformManager;
import com.taobao.weex.ui.component.WXComponent;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public interface IWXViewUpdater {

    /**
     * 执行视图变换
     *
     * @param component 组件名称
     * @param targetView 组件对应的native view
     * @param cmd 值
     * @param translator 分辨率计算
     * @param config 扩展配置
     *
     * */
    void invoke(@NonNull WXComponent component,
                @NonNull View targetView,
                @NonNull Object cmd,
                @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                @NonNull Map<String, Object> config);
}
