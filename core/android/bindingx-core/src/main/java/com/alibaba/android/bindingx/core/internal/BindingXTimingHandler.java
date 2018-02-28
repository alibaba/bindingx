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
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.view.animation.AnimationUtils;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * A built-in implementation of {@link com.alibaba.android.bindingx.core.IEventHandler} which handle timing event.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXTimingHandler extends AbstractEventHandler implements AnimationFrame.Callback {

    private long mStartTime = 0;

    private AnimationFrame mAnimationFrame;
    private boolean isFinish = false;

    public BindingXTimingHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
        if(mAnimationFrame == null) {
            mAnimationFrame = AnimationFrame.newInstance();
        } else {
            mAnimationFrame.clear();
        }
    }

    @VisibleForTesting
    /*package*/ BindingXTimingHandler(Context context, PlatformManager manager, AnimationFrame frame, Object... extension) {
        super(context, manager, extension);
        mAnimationFrame = frame;
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
    public void onBindExpression(@NonNull String eventType,
                                 @Nullable Map<String,Object> globalConfig,
                                 @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs,
                                 @Nullable BindingXCore.JavaScriptCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);

        if(mAnimationFrame == null) {
            mAnimationFrame = AnimationFrame.newInstance();
        }

        fireEventByState(BindingXConstants.STATE_START, 0);

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
            JSMath.applyTimingValuesToScope(mScope, deltaT);
            if(!isFinish) {
                consumeExpression(mExpressionHoldersMap, mScope, BindingXEventType.TYPE_TIMING);
            }
            isFinish = evaluateExitExpression(mExitExpressionPair,mScope);
        } catch (Exception e) {
            LogProxy.e("runtime error", e);
        }
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        fireEventByState(BindingXConstants.STATE_END, (System.currentTimeMillis() - mStartTime));
        clearExpressions();
        if(mAnimationFrame != null) {
            mAnimationFrame.clear();
        }
        mStartTime = 0;

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        fireEventByState(BindingXConstants.STATE_EXIT, (long) t);

        if(mAnimationFrame != null) {
            mAnimationFrame.clear();
        }
        mStartTime = 0;
    }

    private void fireEventByState(@BindingXConstants.State String state, long t) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            param.put("t", t);
            param.put(BindingXConstants.KEY_TOKEN, mToken);

            mCallback.callback(param);
            LogProxy.d(">>>>>>>>>>>fire event:(" + state + "," + t + ")");
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
