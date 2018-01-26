package com.alibaba.android.bindingx.plugin.weex.internal;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.alibaba.android.bindingx.plugin.weex.BindingXEventType;
import com.alibaba.android.bindingx.plugin.weex.LogProxy;
import com.alibaba.android.bindingx.plugin.weex.PlatformManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * An abstract scroll event handler. Because there are some difference between Weex and ReactNative.
 * In Weex, both Scroller and List are scrollable. However in ReactNative, only ScrollView is scrollable.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public abstract class AbstractScrollEventHandler extends AbstractEventHandler {

    protected int mContentOffsetX, mContentOffsetY;
    private boolean isStart = false;

    public AbstractScrollEventHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        clearExpressions();
        isStart = false;
        fireEventByState(BindingXConstants.STATE_END, mContentOffsetX, mContentOffsetY,0,0,0,0);
        return true;
    }

    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        float contentOffsetX = (float) scope.get("internal_x");
        float contentOffsetY = (float) scope.get("internal_y");
        this.fireEventByState(BindingXConstants.STATE_EXIT, contentOffsetX, contentOffsetY,0,0,0,0);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        isStart = false;
    }

    /**
     * @param contentOffsetX 横向绝对偏移(px)
     * @param contentOffsetY 纵向绝对偏移(px)
     * @param dx 相对上一次onScroll事件的横向的偏移
     * @param dy 相对上一次onScroll事件的纵向的偏移
     * @param tdx 距离最近一次"拐点"的横向偏移
     * @param tdy 距离最近一次"拐点"的纵向偏移
     * */
    protected void handleScrollEvent(int contentOffsetX, int contentOffsetY, int dx, int dy,
                                   int tdx, int tdy) {
        LogProxy.d(String.format(Locale.CHINA,
                "[ExpressionScrollHandler] scroll changed. (contentOffsetX:%d,contentOffsetY:%d,dx:%d,dy:%d,tdx:%d,tdy:%d)",
                contentOffsetX,contentOffsetY,dx,dy,tdx,tdy));

        this.mContentOffsetX = contentOffsetX;
        this.mContentOffsetY = contentOffsetY;

        if(!isStart) {
            isStart = true;
            fireEventByState(BindingXConstants.STATE_START,contentOffsetX,contentOffsetY,dx,dy,tdx,tdy);
        }

        try {
            //消费所有的表达式
            JSMath.applyScrollValuesToScope(mScope, contentOffsetX, contentOffsetY, dx, dy, tdx, tdy, mPlatformManager.getResolutionTranslator());
            if(!evaluateExitExpression(mExitExpressionPair,mScope)) {
                consumeExpression(mExpressionHoldersMap, mScope, BindingXEventType.TYPE_SCROLL);
            }
        } catch (Exception e) {
            LogProxy.e("runtime error", e);
        }
    }

    protected void fireEventByState(@BindingXConstants.State String state, float contentOffsetX, float contentOffsetY,
                                    float dx, float dy, float tdx, float tdy) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            double x = mPlatformManager.getResolutionTranslator().nativeToWeb(contentOffsetX);
            double y = mPlatformManager.getResolutionTranslator().nativeToWeb(contentOffsetY);
            param.put("x", x);
            param.put("y", y);

            double _dx = mPlatformManager.getResolutionTranslator().nativeToWeb(dx);
            double _dy = mPlatformManager.getResolutionTranslator().nativeToWeb(dy);
            param.put("dx", _dx);
            param.put("dy", _dy);

            double _tdx = mPlatformManager.getResolutionTranslator().nativeToWeb(tdx);
            double _tdy = mPlatformManager.getResolutionTranslator().nativeToWeb(tdy);
            param.put("tdx", _tdx);
            param.put("tdy", _tdy);

            mCallback.callback(param);
            LogProxy.d(">>>>>>>>>>>fire event:(" + state + "," + x + "," + y + ","+ _dx  +","+ _dy +"," + _tdx +"," + _tdy +")");
        }
    }

}
