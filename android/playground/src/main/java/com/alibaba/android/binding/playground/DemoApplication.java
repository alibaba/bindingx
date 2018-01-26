package com.alibaba.android.binding.playground;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.android.bindingx.plugin.weex.BindingX;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.appfram.navigator.IActivityNavBarSetter;
import com.taobao.weex.common.WXException;

import org.json.JSONObject;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

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
                        MainActivity.start(DemoApplication.this, url);
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

        try {
            BindingX.register();
        } catch (WXException e) {
        }
    }
}
