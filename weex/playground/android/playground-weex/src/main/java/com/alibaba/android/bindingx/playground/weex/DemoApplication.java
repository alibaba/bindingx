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
package com.alibaba.android.bindingx.playground.weex;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXPropertyInterceptor;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.playground.weex.extension.lottie.LottieProgressInterceptor;
import com.alibaba.android.bindingx.playground.weex.extension.lottie.WXLottieComponent;
import com.alibaba.android.bindingx.plugin.weex.BindingX;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.appfram.navigator.IActivityNavBarSetter;
import com.taobao.weex.common.WXException;

import org.json.JSONObject;

import java.util.Map;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WXSDKEngine.initialize(this, new InitConfig.Builder()
                .setImgAdapter(new PicassoImageAdapter())
                .build());

        WXSDKEngine.setActivityNavBarSetter(new IActivityNavBarSetter() {
            @Override
            public boolean push(String param) {
                try {
                    JSONObject object = new JSONObject(param);
                    String url = object.optString("url");
                    if(!TextUtils.isEmpty(url)) {
                        WXActivity.start(DemoApplication.this, url);
                        return true;
                    } else {
                        return false;
                    }
                }catch (Throwable e){
                    return false;
                }
            }

            @Override
            public boolean pop(String param) {
                return false;
            }

            @Override
            public boolean setNavBarRightItem(String param) {
                return false;
            }

            @Override
            public boolean clearNavBarRightItem(String param) {
                return false;
            }

            @Override
            public boolean setNavBarLeftItem(String param) {
                return false;
            }

            @Override
            public boolean clearNavBarLeftItem(String param) {
                return false;
            }

            @Override
            public boolean setNavBarMoreItem(String param) {
                return false;
            }

            @Override
            public boolean clearNavBarMoreItem(String param) {
                return false;
            }

            @Override
            public boolean setNavBarTitle(String param) {
                return false;
            }
        });

        // register bindingx module manually
        try {
            BindingX.register();
            LogProxy.sEnableLog = true;

            WXSDKEngine.registerComponent("lottie",WXLottieComponent.class);
        } catch (WXException e) {
        }

        // Custom Properties
        BindingXPropertyInterceptor.getInstance().addInterceptor(new LottieProgressInterceptor());

        BindingXPropertyInterceptor.getInstance()
                .addInterceptor(new BindingXPropertyInterceptor.IPropertyUpdateInterceptor() {
            @Override
            public boolean updateView(@NonNull View targetView,
                                      @NonNull String propertyName,
                                      @NonNull Object propertyValue,
                                      @NonNull PlatformManager.IDeviceResolutionTranslator translator,
                                      @NonNull Map<String, Object> config,
                                      Object... extension) {
                //  TODO you can intercept or override default behavior
                return false;
            }
        });

        // register bindingx module automatically by annotation processor
//        WeexPluginContainer.loadAll(getApplicationContext());
    }
}
