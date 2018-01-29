package com.alibaba.android.bindingx.core.internal;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * 处理设备方向相关
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXOrientationHandler extends AbstractEventHandler implements OrientationDetector.OnOrientationChangedListener {

    private boolean isStarted = false;

    private double mStartAlpha;
    private double mStartBeta;
    private double mStartGamma;

    private double mLastAlpha;
    private double mLastBeta;
    private double mLastGamma;

    private OrientationDetector mOrientationDetector;


    private OrientationEvaluator mEvaluatorX;
    private OrientationEvaluator mEvaluatorY;
    private OrientationEvaluator mEvaluator3D;

    private String mSceneType;

    private LinkedList<Double> mRecordsAlpha = new LinkedList<>();


    public BindingXOrientationHandler(Context context, PlatformManager manager, Object... extension) {
        super(context,manager, extension);
        if (context != null) {
            mOrientationDetector = OrientationDetector.getInstance(context);
        }
    }


    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        if (mOrientationDetector == null) {
            return false;
        }

        mOrientationDetector.addOrientationChangedListener(this);
        return mOrientationDetector.start(SensorManager.SENSOR_DELAY_GAME);
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

        // 获取配置
        String sceneType = null;
        if(globalConfig != null) {
            // 目前有2d和3d两种配置
            sceneType = (String) globalConfig.get(BindingXConstants.KEY_SCENE_TYPE);
            if(TextUtils.isEmpty(sceneType)) {
                sceneType = "2d";
            } else {
                sceneType = sceneType.toLowerCase();
            }
        }
        if(TextUtils.isEmpty(sceneType) || (!"2d".equals(sceneType) && !"3d".equals(sceneType))) {
            sceneType = "2d";
        }

        this.mSceneType = sceneType;
        LogProxy.d("[ExpressionOrientationHandler] sceneType is " + sceneType);

        if("2d".equals(sceneType)) {
            mEvaluatorX = new OrientationEvaluator(null,90.0d,null);
            mEvaluatorY = new OrientationEvaluator(0d,null,90.0d);
        } else if("3d".equals(sceneType)){
            mEvaluator3D = new OrientationEvaluator(null,null,null);
        }
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        clearExpressions();
        if (mOrientationDetector == null) {
            return false;
        }

        fireEventByState(BindingXConstants.STATE_END, mLastAlpha, mLastBeta, mLastGamma);
        return mOrientationDetector.removeOrientationChangedListener(this);
//         不要stop()。如果一个页面多个orientation监听，那么disable一个，会暂停所有的
//        mOrientationDetector.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOrientationDetector != null) {
            mOrientationDetector.removeOrientationChangedListener(this);
            mOrientationDetector.stop();
        }

        if (mExpressionHoldersMap != null) {
            mExpressionHoldersMap.clear();
            mExpressionHoldersMap = null;
        }
    }

    @Override
    public void onOrientationChanged(double alpha, double beta, double gamma) {
        //精度不需要太高，四舍五入即可
        alpha = Math.round(alpha);
        beta = Math.round(beta);
        gamma = Math.round(gamma);

        if(alpha == mLastAlpha && beta == mLastBeta && gamma == mLastGamma) {
            return;
        }

        if(!isStarted) {
            isStarted = true;
            fireEventByState(BindingXConstants.STATE_START, alpha, beta, gamma);
            mStartAlpha = alpha;
            mStartBeta = beta;
            mStartGamma = gamma;
        }

        double x,y,z;
        boolean result = false;
        if("2d".equals(mSceneType)) {
            result = calculate2D(alpha, beta, gamma);
        } else if("3d".equals(mSceneType)) {
            result = calculate3D(alpha, beta, gamma);
        }

        if(!result) {
            return;
        }

        x = mValueHolder.x;
        y = mValueHolder.y;
        z = mValueHolder.z;

        mLastAlpha = alpha;
        mLastBeta = beta;
        mLastGamma = gamma;

        try {
            //消费所有的表达式
            JSMath.applyOrientationValuesToScope(mScope,alpha,beta,gamma,mStartAlpha,mStartBeta,mStartGamma, x,y,z);
            if(!evaluateExitExpression(mExitExpressionPair,mScope)) {
                consumeExpression(mExpressionHoldersMap, mScope, BindingXEventType.TYPE_ORIENTATION);
            }

        } catch (Exception e) {
            LogProxy.e("runtime error", e);
        }
    }


    private Vector3 mVectorX = new Vector3(0,0,1);
    private Vector3 mVectorY = new Vector3(0,1,1);

    private ValueHolder mValueHolder = new ValueHolder(0,0,0);

    private boolean calculate2D(double alpha, double beta, double gamma) {
        if(mEvaluatorX != null && mEvaluatorY != null) {

            mRecordsAlpha.add(alpha);
            if(mRecordsAlpha.size() > 5) {
                mRecordsAlpha.removeFirst();
            }

            formatRecords(mRecordsAlpha,360);
            double formatAlpha = (mRecordsAlpha.get(mRecordsAlpha.size()- 1) - mStartAlpha) % 360;

            Quaternion quaternionX = mEvaluatorX.calculate(alpha, beta, gamma, formatAlpha);
            Quaternion quaternionY = mEvaluatorY.calculate(alpha, beta, gamma, formatAlpha);

            mVectorX.set(0,0,1);
            mVectorX.applyQuaternion(quaternionX);
            mVectorY.set(0,1,1);
            mVectorY.applyQuaternion(quaternionY);

            double x = Math.toDegrees(Math.acos(mVectorX.x)) - 90;
            double y = Math.toDegrees(Math.acos(mVectorY.y)) - 90;

            if(Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
                return false;
            }

            x = Math.round(x);
            y = Math.round(y);

            mValueHolder.x = x;
            mValueHolder.y = y;
        }
        return true;

    }

    private boolean calculate3D(double alpha, double beta, double gamma) {
        if(mEvaluator3D != null) {
            mRecordsAlpha.add(alpha);
            if(mRecordsAlpha.size() > 5) {
                mRecordsAlpha.removeFirst();
            }

            formatRecords(mRecordsAlpha,360);
            double formatAlpha = (mRecordsAlpha.get(mRecordsAlpha.size()- 1) - mStartAlpha) % 360;
            Quaternion q = mEvaluator3D.calculate(alpha, beta, gamma, formatAlpha);

            if(Double.isNaN(q.x) || Double.isNaN(q.y) || Double.isNaN(q.z)
                    || Double.isInfinite(q.x) || Double.isInfinite(q.y) || Double.isInfinite(q.z)) {
                return false;
            }

            mValueHolder.x = q.x;
            mValueHolder.y = q.y;
            mValueHolder.z = q.z;
        }
        return true;
    }

    private void formatRecords(List<Double> records, int threshold) {
        int l = records.size();
        double times = 0;
        if (l > 1) {
            for (int i = 1; i < l; i++) {
                if(records.get(i-1) != null && records.get(i) != null) {
                    if(records.get(i)-records.get(i-1) < -threshold/2) {
                        times = Math.floor(records.get(i-1) / threshold) + 1;
                        records.set(i,records.get(i) + times * threshold);
                    }
                    if(records.get(i)-records.get(i-1) > threshold /2) {
                        records.set(i,records.get(i) - threshold);
                    }
                }
            }
        }
    }


    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        double alpha = (double) scope.get("alpha");
        double beta = (double) scope.get("beta");
        double gamma = (double) scope.get("gamma");
        fireEventByState(BindingXConstants.STATE_EXIT, alpha, beta, gamma);
    }

    private void fireEventByState(@BindingXConstants.State String state, double alpha, double beta, double gamma) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            param.put("alpha", alpha);
            param.put("beta", beta);
            param.put("gamma", gamma);

            mCallback.callback(param);
            LogProxy.d(">>>>>>>>>>>fire event:(" + state + "," + alpha + "," + beta + "," + gamma + ")");
        }
    }

    @Override
    public void onActivityPause() {
        if(mOrientationDetector != null) {
            mOrientationDetector.stop();
        }
    }

    @Override
    public void onActivityResume() {
        if(mOrientationDetector != null) {
            mOrientationDetector.start(SensorManager.SENSOR_DELAY_GAME);
        }
    }

    static class ValueHolder {
        double x;
        double y;
        double z;


        ValueHolder(){}

        ValueHolder(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

}
