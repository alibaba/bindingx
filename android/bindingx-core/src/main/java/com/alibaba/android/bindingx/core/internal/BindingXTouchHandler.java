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
package com.alibaba.android.bindingx.core.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理手势相关
 */
public class BindingXTouchHandler extends AbstractEventHandler implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private float mDownX;
    private float mDownY;

    private float mDx;
    private float mDy;

    private GestureDetector mGestureDetector;

    private boolean isPanGestureAvailable;
    private boolean isFlickGestureAvailable;

    public BindingXTouchHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
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
                    fireEventByState(BindingXConstants.STATE_START, 0, 0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mDownX == 0 && mDownY == 0) {
                        mDownX = event.getRawX();
                        mDownY = event.getRawY();
                        fireEventByState(BindingXConstants.STATE_START, 0, 0);
                        break;
                    }
                    mDx = event.getRawX() - mDownX;
                    mDy = event.getRawY() - mDownY;
                    break;
                case MotionEvent.ACTION_UP:
                    mDownX = 0;
                    mDownY = 0;
                    clearExpressions();
                    fireEventByState(BindingXConstants.STATE_END, mDx, mDy);
                    //bugFixed:we must reset dx & dy every time.
                    mDx = 0;
                    mDy = 0;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mDownX = 0;
                    mDownY = 0;
                    clearExpressions();
                    fireEventByState(BindingXConstants.STATE_CANCEL, mDx, mDy);
                    break;
            }
        } catch (Exception e) {
            LogProxy.e("runtime error ", e);
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (!isPanGestureAvailable) {
            LogProxy.d("pan gesture is not enabled");
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
            JSMath.applyXYToScope(mScope, deltaX, deltaY, mPlatformManager.getResolutionTranslator());
            if(!evaluateExitExpression(mExitExpressionPair,mScope)) {
                consumeExpression(mExpressionHoldersMap, mScope, BindingXEventType.TYPE_PAN);
            }
        } catch (Exception e) {
            LogProxy.e("runtime error", e);
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
        View sourceView = mPlatformManager.getViewFinder().findViewBy(sourceRef, instanceId);
        if (sourceView == null) {
            LogProxy.e("[ExpressionTouchHandler] onCreate failed. sourceView not found:" + sourceRef);
            return false;
        }
        sourceView.setOnTouchListener(this);
        LogProxy.d("[ExpressionTouchHandler] onCreate success. {source:" + sourceRef + ",type:" + eventType + "}");
        return true;
    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        switch (eventType) {
            case BindingXEventType.TYPE_PAN:
                this.setPanGestureAvailable(true);
                break;
            case BindingXEventType.TYPE_FLICK:
                this.setFlickGestureAvailable(true);
                break;
        }
    }

    @Override
    public void onBindExpression(@NonNull String eventType,
                                 @Nullable Map<String,Object> globalConfig,
                                 @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs,
                                 @Nullable BindingXCore.JavaScriptCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        switch (eventType) {
            case BindingXEventType.TYPE_PAN:
                this.setPanGestureAvailable(false);
                break;
            case BindingXEventType.TYPE_FLICK:
                this.setFlickGestureAvailable(false);
                break;
        }

        //如果所有手势都不支持的话，那么可以清除touchHandler
        if (!this.isPanGestureAvailable() && !this.isFlickGestureAvailable()) {
            String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
            View hostView = mPlatformManager.getViewFinder().findViewBy(sourceRef, instanceId);
            if (hostView != null) {
                hostView.setOnTouchListener(null);
            }
            LogProxy.d("remove touch listener success.[" + sourceRef + "," + eventType + "]");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExpressionHoldersMap != null) {
            mExpressionHoldersMap.clear();
            mExpressionHoldersMap = null;
        }
        mExitExpressionPair = null;
        mCallback = null;
        isFlickGestureAvailable = false;
        isPanGestureAvailable = false;
    }

    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        float deltaX = (float) scope.get("internal_x");
        float deltaY = (float) scope.get("internal_y");
        fireEventByState(BindingXConstants.STATE_EXIT, deltaX, deltaY);
    }

    private void fireEventByState(@BindingXConstants.State String state, float dx, float dy) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            double x = mPlatformManager.getResolutionTranslator().nativeToWeb(dx);
            double y = mPlatformManager.getResolutionTranslator().nativeToWeb(dy);
            param.put("deltaX", x);
            param.put("deltaY", y);
            param.put(BindingXConstants.KEY_TOKEN, mToken);
            mCallback.callback(param);
            LogProxy.d(">>>>>>>>>>>fire event:(" + state + "," + x + "," + y + ")");
        }
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

}