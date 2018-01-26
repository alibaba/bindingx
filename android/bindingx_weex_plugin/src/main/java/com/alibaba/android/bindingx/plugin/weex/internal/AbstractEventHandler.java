package com.alibaba.android.bindingx.plugin.weex.internal;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.plugin.weex.BindingXCore;
import com.alibaba.android.bindingx.plugin.weex.IEventHandler;
import com.alibaba.android.bindingx.plugin.weex.LogProxy;
import com.alibaba.android.bindingx.plugin.weex.PlatformManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public abstract class AbstractEventHandler implements IEventHandler {

    protected volatile Map<String/*targetRef*/, List<ExpressionHolder>> mExpressionHoldersMap;
    protected BindingXCore.JavaScriptCallback mCallback;
    protected final Map<String, Object> mScope = new HashMap<>();
    protected String mInstanceId;
    protected String mAnchorInstanceId;
    protected Context mContext;
    protected PlatformManager mPlatformManager;

    protected ExpressionPair mExitExpressionPair;

    private Cache<String, Expression> mCachedExpressionMap = new Cache<>(16);

    public AbstractEventHandler(Context context, PlatformManager manager, Object... extension) {
        mContext = context;
        mPlatformManager = manager;
        mInstanceId = (extension != null && extension.length > 0 && extension[0] instanceof String) ? ((String)extension[0]) : null;
    }

    @Override
    public void setAnchorInstanceId(String anchorInstanceId) {
        this.mAnchorInstanceId = anchorInstanceId;
    }

    @Override
    public void onBindExpression(@NonNull String eventType,
                                 @Nullable Map<String,Object> globalConfig,
                                 @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs,
                                 @Nullable BindingXCore.JavaScriptCallback callback) {
        clearExpressions();
        transformArgs(eventType, expressionArgs);
        this.mCallback = callback;
        this.mExitExpressionPair = exitExpressionPair;

        if(!mScope.isEmpty()) {
            mScope.clear();
        }
        applyFunctionsToScope();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        mCachedExpressionMap.clear();
    }

    private void applyFunctionsToScope() {
        JSMath.applyToScope(mScope);
        TimingFunctions.applyToScope(mScope);
    }

    private void transformArgs(@NonNull String eventType, @NonNull List<Map<String, Object>> originalArgs) {
        if (mExpressionHoldersMap == null) {
            mExpressionHoldersMap = new HashMap<>();
        }
        for (Map<String, Object> arg : originalArgs) {
            String targetRef = Utils.getStringValue(arg, BindingXConstants.KEY_ELEMENT);
            String targetInstanceId = Utils.getStringValue(arg, BindingXConstants.KEY_INSTANCE_ID);
            String property = Utils.getStringValue(arg, BindingXConstants.KEY_PROPERTY);

            ExpressionPair expressionPair = Utils.getExpressionPair(arg, BindingXConstants.KEY_EXPRESSION);

            String config = Utils.getStringValue(arg, BindingXConstants.KEY_CONFIG);
            Map<String,Object> configMap = null;
            if(!TextUtils.isEmpty(config)) {
                try {
                    configMap = Utils.toMap(new JSONObject(config));
                }catch (Exception e) {
                    LogProxy.e("parse config failed", e);
                }
            }

            if (TextUtils.isEmpty(targetRef) || TextUtils.isEmpty(property) || expressionPair == null) {
                LogProxy.e("skip illegal binding args[" + targetRef + "," + property + "," + expressionPair + "]");
                continue;
            }
            ExpressionHolder holder = new ExpressionHolder(targetRef,targetInstanceId, expressionPair, property, eventType, configMap);

            List<ExpressionHolder> holders = mExpressionHoldersMap.get(targetRef);
            if (holders == null) {
                holders = new ArrayList<>(4);
                mExpressionHoldersMap.put(targetRef, holders);
                holders.add(holder);
            } else if (!holders.contains(holder)) {
                holders.add(holder);
            }
        }
    }

    /**
     * 执行边界条件
     *
     * @param exitExpression 边界条件表达式
     * @param scope 已经赋值过的scope(内部包含未知变量的当前值，比如x/y等)
     *
     * @return true代表边界条件满足
     * */
    boolean evaluateExitExpression(ExpressionPair exitExpression, @NonNull Map<String,Object> scope) {
        boolean exit = false;
        if (exitExpression != null
                && !TextUtils.isEmpty(exitExpression.transformed)
                && !"{}".equals(exitExpression.transformed)) {
            Expression expression = new Expression(exitExpression.transformed);
            try {
                exit = (boolean) expression.execute(scope);
            } catch (Exception e) {
                LogProxy.e("evaluateExitExpression failed. ", e);
            }
        }
        if (exit) {
            //发送事件、清空所有表达式
            clearExpressions();
            try {
                onExit(scope);
            }catch (Exception e) {
                LogProxy.e("execute exit expression failed: ", e);
            }
            LogProxy.d("exit = true,consume finished");
        }

        return exit;
    }

    /**
     * 消费表达式
     *
     * Notice: 确保scope已经赋值
     *
     * @param args 表达式集合
     * @param scope 已经赋值过的scope(内部包含未知变量的当前值，比如x/y等)
     * @param currentState 当前eventType
     *
     * */
    void consumeExpression(@Nullable Map<String, List<ExpressionHolder>> args, @NonNull Map<String,Object> scope,
                           @NonNull String currentState) throws IllegalArgumentException, JSONException {
        //https://developer.mozilla.org/zh-CN/docs/Web/CSS/transform
        if (args == null) {
            LogProxy.e("expression args is null");
            return;
        }
        if (args.isEmpty()) {
            LogProxy.e("no expression need consumed");
            return;
        }

        //执行表达式
        LogProxy.d(String.format(Locale.CHINA, "consume expression with %d tasks. event type is %s",args.size(),currentState));
        for (List<ExpressionHolder> holderList : args.values()) {
            for (ExpressionHolder holder : holderList) {
                if (!currentState.equals(holder.eventType)) {
                    LogProxy.d("skip expression with wrong event type.[expected:" + currentState + ",found:" + holder.eventType + "]");
                    continue;
                }
                String instanceId = TextUtils.isEmpty(holder.targetInstanceId)? mInstanceId : holder.targetInstanceId;

                View targetView = mPlatformManager.getViewFinder().findViewBy(holder.targetRef, instanceId);
                if (targetView == null) {
                    LogProxy.e("failed to execute expression,target view not found.[ref:" + holder.targetRef + "]");
                    continue;
                }

                ExpressionPair expressionPair = holder.expressionPair;
                if(expressionPair == null
                        || TextUtils.isEmpty(expressionPair.transformed)
                        || "{}".equals(expressionPair.transformed)) {
                    continue;
                }
                Expression expression = mCachedExpressionMap.get(expressionPair.transformed);
                if (expression == null) {
                    expression = new Expression(expressionPair.transformed);
                    mCachedExpressionMap.put(expressionPair.transformed, expression);
                }

                Object obj = expression.execute(scope);
                if (obj == null) {
                    LogProxy.e("failed to execute expression,expression result is null");
                    continue;
                }
                if((obj instanceof Double) && Double.isNaN((Double) obj) ||
                        (obj instanceof Float && Float.isNaN((Float)obj))) {
                    LogProxy.e("failed to execute expression,expression result is NaN");
                    continue;
                }
                //apply transform to target view.
                mPlatformManager.getViewUpdater().synchronouslyUpdateViewOnUIThread(
                        targetView,
                        holder.prop,
                        obj,
                        mPlatformManager.getResolutionTranslator(),
                        holder.config,
                        holder.targetRef,/*additional params for weex*/
                        instanceId       /*additional params for weex*/
                );
            }
        }

    }

    protected abstract void onExit(@NonNull Map<String, Object> scope);

    void clearExpressions() {
        LogProxy.d("all expression are cleared");
        if (mExpressionHoldersMap != null) {
            mExpressionHoldersMap.clear();
            mExpressionHoldersMap = null;
        }
        mExitExpressionPair = null;
    }

    static class Cache<K, V> extends LinkedHashMap<K, V> {
        private int maxSize;

        Cache(int maxSize) {
            super(4, 0.75f, true);
            this.maxSize = Math.max(maxSize, 4);
        }

        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > maxSize;
        }
    }
}
