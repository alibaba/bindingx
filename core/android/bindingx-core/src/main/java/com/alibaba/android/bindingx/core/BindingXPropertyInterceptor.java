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

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * If you need to intercept the default behavior of {@link com.alibaba.android.bindingx.core.PlatformManager.IViewUpdater},
 * then you can use this class to override or intercept it.
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXPropertyInterceptor {

    private final Handler sUIHandler = new Handler(Looper.getMainLooper());

    private final LinkedList<IPropertyUpdateInterceptor> mPropertyInterceptors = new LinkedList<>();

    private static BindingXPropertyInterceptor sInstance = new BindingXPropertyInterceptor();
    private BindingXPropertyInterceptor() {}

    @NonNull
    public static BindingXPropertyInterceptor getInstance() {
        return sInstance;
    }

    public void addInterceptor(@Nullable IPropertyUpdateInterceptor interceptor) {
        if(interceptor != null) {
            this.mPropertyInterceptors.add(interceptor);
        }
    }

    public boolean removeInterceptor(@Nullable IPropertyUpdateInterceptor interceptor) {
        if(interceptor != null) {
            return mPropertyInterceptors.remove(interceptor);
        }
        return false;
    }

    public void clear() {
        mPropertyInterceptors.clear();
    }

    public void performIntercept(@Nullable final View targetView,
                                 @NonNull final String propertyName,
                                 @NonNull final Object propertyValue,
                                 @NonNull final PlatformManager.IDeviceResolutionTranslator translator,
                                 @NonNull final Map<String, Object> config,
                                 final Object... extension) {
        if(mPropertyInterceptors.isEmpty()) {
            return;
        }

        sUIHandler.post(new WeakRunnable(new Runnable() {
            @Override
            public void run() {
                for(BindingXPropertyInterceptor.IPropertyUpdateInterceptor interceptor : mPropertyInterceptors) {
                    interceptor.updateView(
                            targetView,
                            propertyName,
                            propertyValue,
                            translator,
                            config,
                            extension);
                }
            }
        }));
    }

    public void clearCallbacks() {
        sUIHandler.removeCallbacksAndMessages(null);
    }

    @NonNull
    public List<IPropertyUpdateInterceptor> getInterceptors() {
        return Collections.unmodifiableList(mPropertyInterceptors);
    }


    public interface IPropertyUpdateInterceptor {
        /**
         * @param targetView target view that will be updated. Maybe null if component/node is virtual.
         * @param propertyName the property that will be changed by property value
         * @param propertyValue the property value that will changed to
         * @param translator handle device resolution for different platforms
         * @param config additional configuration such as transform origin
         * @param extension extension params. For example, weex instanceId.
         *
         * @return true if intercepted (which means that the property is accepted and consumed), false otherwise
         * */
         boolean updateView(@Nullable View targetView,
                                               @NonNull String propertyName,
                                               @NonNull Object propertyValue,
                                               @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                                               @NonNull Map<String, Object> config,
                                               Object... extension);
    }
}
