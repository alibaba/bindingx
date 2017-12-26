package com.alibaba.android.binding.plugin.weex.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.binding.plugin.weex.EventType;
import com.alibaba.android.binding.plugin.weex.ExpressionConstants;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理手势相关
 */
public class ExpressionTouchHandler extends AbstractEventHandler implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private float mDownX;
    private float mDownY;

    private float mDx;
    private float mDy;

    private GestureDetector mGestureDetector;

    private boolean isPanGestureAvailable;
    private boolean isFlickGestureAvailable;

    public ExpressionTouchHandler(@NonNull WXSDKInstance instance) {
        super(instance);
        Context context = instance.getContext();
        mGestureDetector = new GestureDetector(context, this);
    }

    void setPanGestureAvailable(boolean available) {
        this.isPanGestureAvailable = available;
    }

    void setFlickGestureAvailable(boolean available) {
        this.isFlickGestureAvailable = available;
    }

    boolean isPanGestureAvailable() {
        return isPanGestureAvailable;
    }

    boolean isFlickGestureAvailable() {
        return isFlickGestureAvailable;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getRawX();
                    mDownY = event.getRawY();
                    fireEventByState(ExpressionConstants.STATE_START, 0, 0);
//                    if(WXEnvironment.isApkDebugable()) {
//                        WXLogUtils.d(TAG, "ACTION_DOWN | (downX:"+mDownX+",downY:"+mDownY+")");
//                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mDownX == 0 && mDownY == 0) {
                        mDownX = event.getRawX();
                        mDownY = event.getRawY();
                        fireEventByState(ExpressionConstants.STATE_START, 0, 0);
//                        if (WXEnvironment.isApkDebugable()) {
//                            WXLogUtils.d(TAG, "ACTION_MOVE | (downX:"+mDownX+",downY:"+mDownY+")");
//                        }
                        break;
                    }
                    mDx = event.getRawX() - mDownX;
                    mDy = event.getRawY() - mDownY;
//                    if(WXEnvironment.isApkDebugable()) {
//                        WXLogUtils.d(TAG, "ACTION_MOVE | (dx:" + mDx + ",dy:" + mDy+")");
//                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mDownX = 0;
                    mDownY = 0;
                    clearExpressions();
                    fireEventByState(ExpressionConstants.STATE_END, mDx, mDy);

                    //bugFixed:we must reset dx & dy every time.
                    mDx = 0;
                    mDy = 0;
//                    if(WXEnvironment.isApkDebugable()) {
//                        WXLogUtils.d(TAG, "ACTION_UP");
//                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mDownX = 0;
                    mDownY = 0;
                    clearExpressions();
                    fireEventByState(ExpressionConstants.STATE_CANCEL, mDx, mDy);
//                    if(WXEnvironment.isApkDebugable()) {
//                        WXLogUtils.d(TAG, "ACTION_CANCEL");
//                    }
                    break;
            }
        } catch (Exception e) {
            WXLogUtils.e(TAG, "runtime error\n" + e.getMessage());
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (!isPanGestureAvailable) {
            WXLogUtils.d(TAG, "pan gesture is not enabled");
            return false;
        }

        float downX, downY;
        if (e1 == null) {
            //the first time e1 is null.
            downX = mDownX;
            downY = mDownY;
        } else {
            downX = e1.getRawX();
            downY = e1.getRawY();
        }

        if (e2 == null) {
            return false;
        }

        float curX = e2.getRawX();
        float curY = e2.getRawY();

        float deltaX = curX - downX;
        float deltaY = curY - downY;
        try {
            //消费所有的表达式
            JSMath.applyXYToScope(mScope, deltaX, deltaY);
            if(!evaluateExitExpression(mExitExpressionPair,mScope)) {
                consumeExpression(mExpressionHoldersMap, mScope, EventType.TYPE_PAN);
            }
        } catch (Exception e) {
            WXLogUtils.e(TAG, "runtime error\n" + e.getMessage());
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isFlickGestureAvailable) {
            return false;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
        View sourceView = WXModuleUtils.findViewByRef(instanceId, sourceRef);
        if (sourceView == null) {
            WXLogUtils.e(TAG, "[ExpressionTouchHandler] onCreate failed. sourceView not found:" + sourceRef);
            return false;
        }
        sourceView.setOnTouchListener(this);
        if (WXEnvironment.isApkDebugable()) {
            WXLogUtils.d(TAG, "[ExpressionTouchHandler] onCreate success. {source:" + sourceRef + ",type:" + eventType + "}");
        }
        return true;
    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        switch (eventType) {
            case EventType.TYPE_PAN:
                this.setPanGestureAvailable(true);
                break;
            case EventType.TYPE_FLICK:
                this.setFlickGestureAvailable(true);
                break;
        }
    }

    @Override
    public void onBindExpression(@NonNull String eventType, @Nullable Map<String,Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        switch (eventType) {
            case EventType.TYPE_PAN:
                this.setPanGestureAvailable(false);
                break;
            case EventType.TYPE_FLICK:
                this.setFlickGestureAvailable(false);
                break;
        }

        //如果所有手势都不支持的话，那么可以清除touchHandler
        if (!this.isPanGestureAvailable() && !this.isFlickGestureAvailable()) {
            String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
            View hostView = WXModuleUtils.findViewByRef(instanceId, sourceRef);
            if (hostView != null) {
                hostView.setOnTouchListener(null);
            }
            WXLogUtils.d(TAG, "remove touch listener success.[" + sourceRef + "," + eventType + "]");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        if (mExpressionHoldersMap != null) {
            mExpressionHoldersMap.clear();
            mExpressionHoldersMap = null;
        }
        mExitExpressionPair = null;
        mCallback = null;
        isFlickGestureAvailable = false;
        isPanGestureAvailable = false;
        mCachedExpressionMap.clear();
    }

    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        float deltaX = (float) scope.get("internal_x");
        float deltaY = (float) scope.get("internal_y");
        fireEventByState(ExpressionConstants.STATE_EXIT, deltaX, deltaY);
    }

    private void fireEventByState(@ExpressionConstants.State String state, float dx, float dy) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            double x = dx * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            double y = dy * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            param.put("deltaX", x);
            param.put("deltaY", y);
            mCallback.invokeAndKeepAlive(param);
            if (WXEnvironment.isApkDebugable()) {
                WXLogUtils.d(TAG, ">>>>>>>>>>>fire event:(" + state + "," + x + "," + y + ")");
            }
        }
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

}