package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.view.animation.AnimationUtils;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.utils.WXLogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

class ExpressionTimingHandler extends AbstractEventHandler implements AnimationFrame.Callback {

    private long mStartTime = 0;

    private AnimationFrame mAnimationFrame;
    private boolean isFinish = false;

    ExpressionTimingHandler(@NonNull WXSDKInstance instance) {
        super(instance);
        if(mAnimationFrame == null) {
            mAnimationFrame = AnimationFrame.newInstance();
        } else {
            mAnimationFrame.clear();
        }
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        return true;
    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        //nope
    }

    @Override
    public void onBindExpression(@NonNull String eventType, @Nullable Map<String,Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);

        if(mAnimationFrame == null) {
            mAnimationFrame = AnimationFrame.newInstance();
        }

        fireEventByState(ExpressionConstants.STATE_START, 0);

        //先清空消息
        mAnimationFrame.clear();
        mAnimationFrame.requestAnimationFrame(this);
    }

    @WorkerThread
    private void handleTimingCallback() {
        long deltaT;
        if(mStartTime == 0) {
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            deltaT = 0;
            isFinish = false;
        } else {
            deltaT = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
        }

        try {
            //消费所有的表达式
            JSMath.applyTimingValuesToScope(mScope, deltaT);
            //timing与其他类型不一样，需要先消费表达式，后执行边界条件,否则最后一帧可能无法执行到
            if(!isFinish) {
                consumeExpression(mExpressionHoldersMap, mScope, EventType.TYPE_TIMING);
            }
            isFinish = evaluateExitExpression(mExitExpressionPair,mScope);
        } catch (Exception e) {
            WXLogUtils.e(TAG, "runtime error\n" + e.getMessage());
        }
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        fireEventByState(ExpressionConstants.STATE_END, (System.currentTimeMillis() - mStartTime));
        clearExpressions();
        if(mAnimationFrame != null) {
            mAnimationFrame.clear();
        }
        mStartTime = 0;

        return true;
    }

    @Override
    public void onDestroy() {
        clearExpressions();

        if(mAnimationFrame != null) {
            mAnimationFrame.terminate();
            mAnimationFrame = null;
        }
        mStartTime = 0;
    }

    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        double t = (double) scope.get("t");
        fireEventByState(ExpressionConstants.STATE_EXIT, (long) t);

        //清空消息 防止空转
        if(mAnimationFrame != null) {
            mAnimationFrame.clear();
        }
        mStartTime = 0;
    }

    private void fireEventByState(@ExpressionConstants.State String state, long t) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            param.put("t", t);

            mCallback.invokeAndKeepAlive(param);
            if (WXEnvironment.isApkDebugable()) {
                WXLogUtils.d(TAG, ">>>>>>>>>>>fire event:(" + state + "," + t + ")");
            }
        }
    }

    @Override
    public void doFrame() {
        handleTimingCallback();
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

}
