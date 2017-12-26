package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.Nullable;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * 旧版本ExpressionBinding
 *
 * Created by rowandjj(chuyi)<br/>
 */

public final class WXExpressionBindingModule extends WXSDKEngine.DestroyableModule implements IExpressionBinding {

    private ExpressionBindingCore mExpressionBindingCore;

    @Override
    @JSMethod
    @Deprecated
    public void enableBinding(@Nullable String sourceRef, @Nullable String eventType) {
        if(mExpressionBindingCore == null) {
            mExpressionBindingCore = new ExpressionBindingCore();
        }

        //空实现。 此方法仅为了与iOS兼容
    }

    @Override
    @JSMethod
    @Deprecated
    public void createBinding(@Nullable String sourceRef, @Nullable String eventType, @Nullable String exitExpression,
                              @Nullable List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback) {
        if(mExpressionBindingCore == null) {
            mExpressionBindingCore = new ExpressionBindingCore();
        }

        ExpressionPair exitExpressionPair = ExpressionPair.create(null,exitExpression);
        mExpressionBindingCore.doBind(sourceRef,null, eventType,null,exitExpressionPair,expressionArgs,callback,mWXSDKInstance);
    }

    @Override
    @JSMethod
    @Deprecated
    public void disableBinding(@Nullable String sourceRef, @Nullable String eventType) {
        if(mExpressionBindingCore != null) {
            mExpressionBindingCore.doUnbind(sourceRef,eventType);
        }
    }

    @Override
    @JSMethod
    @Deprecated
    public void disableAll() {
        if(mExpressionBindingCore != null) {
            mExpressionBindingCore.doRelease();
        }
    }

    @Override
    public void destroy() {
        if(mExpressionBindingCore != null) {
            mExpressionBindingCore.doRelease();
            mExpressionBindingCore = null;
        }
    }

    ///////// Lifecycle Callbacks

    @Override
    public void onActivityPause() {
        if(mExpressionBindingCore != null) {
            mExpressionBindingCore.onActivityPause();
        }
    }

    @Override
    public void onActivityResume() {
        if(mExpressionBindingCore != null) {
            mExpressionBindingCore.onActivityResume();
        }
    }

}
