package com.alibaba.android.bindingx.plugin.weex;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.taobao.weex.common.WXModule;
import com.taobao.weex.ui.IExternalModuleGetter;


public class WXBindingXModuleService extends Service implements IExternalModuleGetter {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public Class<? extends WXModule> getExternalModuleClass(String type, Context context) {
        if ("bindingx".equals(type)){
            return WXBindingXModule.class;
        }else if ("binding".equals(type)){
            return WXBindingXModule.class;
        }else if ("expressionBinding".equals(type)){
            return WXExpressionBindingModule.class;
        }
        return null;
    }
}
