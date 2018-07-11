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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.BindingXJSFunctionRegister;
import com.alibaba.android.bindingx.core.BindingXPropertyInterceptor;
import com.alibaba.android.bindingx.core.IEventHandler;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;

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
 * An abstract class which implement {@link IEventHandler} interface.
 * This class handles general logic of 'bind expression' and 'consume expression'
 * and so on. The specific implementation of {@link IEventHandler} should inherit this class.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public abstract class AbstractEventHandler implements IEventHandler {

    protected volatile Map<String/*targetRef*/, List<ExpressionHolder>> mExpressionHoldersMap;
    protected volatile Map<String/*interceptorName*/, ExpressionPair> mInterceptorsMap;
    protected BindingXCore.JavaScriptCallback mCallback;
    protected final Map<String, Object> mScope = new HashMap<>();
    protected String mInstanceId;
    protected String mAnchorInstanceId;
    protected String mToken;
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
        BindingXPropertyInterceptor.getInstance().clearCallbacks();
    }

    private void applyFunctionsToScope() {
        JSMath.applyToScope(mScope);
        TimingFunctions.applyToScope(mScope);
        // register custom js functions
        Map<String,JSFunctionInterface> customFunctions = BindingXJSFunctionRegister.getInstance().getJSFunctions();
        if(customFunctions != null && !customFunctions.isEmpty()) {
            mScope.putAll(customFunctions);
        }
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

            Object configObj = arg.get(BindingXConstants.KEY_CONFIG);
            Map<String,Object> configMap = null;
            if(configObj != null && configObj instanceof Map) {
                try {
                    configMap = Utils.toMap(new JSONObject((Map) configObj));
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
     * evaluate exit expression.
     * If expression returns true, then all expressions will be clear.
     *
     * @param exitExpression exit expression
     * @param scope variables which has been assigned
     *
     * @return true if expression return true and false otherwise
     * */
    protected boolean evaluateExitExpression(ExpressionPair exitExpression, @NonNull Map<String,Object> scope) {
        boolean exit = false;
        if (ExpressionPair.isValid(exitExpression)) {
            Expression expression = new Expression(exitExpression.transformed);
            try {
                exit = (boolean) expression.execute(scope);
            } catch (Exception e) {
                LogProxy.e("evaluateExitExpression failed. ", e);
            }
        }
        if (exit) {
            // clear expressions
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

    @Override
    public void setInterceptors(@Nullable Map<String, ExpressionPair> params) {
        this.mInterceptorsMap = params;
    }

    @Override
    public void performInterceptIfNeeded(@NonNull String interceptorName, @NonNull ExpressionPair condition, @NonNull Map<String,Object> scope) {
        if(!ExpressionPair.isValid(condition)) {
            return;
        }
        Expression expression = new Expression(condition.transformed);
        boolean shouldIntercept = false;
        try {
            shouldIntercept = (boolean) expression.execute(scope);
        } catch (Exception e) {
            LogProxy.e("evaluate interceptor ["+ interceptorName+"] expression failed. ", e);
        }
        if(shouldIntercept) {
            onUserIntercept(interceptorName, scope);
        }
    }

    private void tryInterceptAllIfNeeded(@NonNull Map<String,Object> scope) {
        if(this.mInterceptorsMap == null || this.mInterceptorsMap.isEmpty()) {
            return;
        }
        for(Map.Entry<String, ExpressionPair> entry : this.mInterceptorsMap.entrySet()) {
            String interceptorName = entry.getKey();
            ExpressionPair interceptCondition = entry.getValue();
            if(!TextUtils.isEmpty(interceptorName) && interceptCondition != null) {
                performInterceptIfNeeded(interceptorName, interceptCondition, scope);
            }
        }
    }

    /**
     * consume all the expressions that bind before.
     *
     * @param args an list which holds an array of {@link ExpressionHolder}
     * @param scope variables which has been assigned
     * @param currentType current event type
     *
     * */
    protected void consumeExpression(@Nullable Map<String, List<ExpressionHolder>> args, @NonNull Map<String,Object> scope,
                           @NonNull String currentType) throws IllegalArgumentException, JSONException {
        tryInterceptAllIfNeeded(scope);

        if (args == null) {
            LogProxy.e("expression args is null");
            return;
        }
        if (args.isEmpty()) {
            LogProxy.e("no expression need consumed");
            return;
        }

        if(LogProxy.sEnableLog) {
            LogProxy.d(String.format(Locale.getDefault(), "consume expression with %d tasks. event type is %s",args.size(),currentType));
        }
        for (List<ExpressionHolder> holderList : args.values()) {
            for (ExpressionHolder holder : holderList) {
                if (!currentType.equals(holder.eventType)) {
                    LogProxy.d("skip expression with wrong event type.[expected:" + currentType + ",found:" + holder.eventType + "]");
                    continue;
                }
                String instanceId = TextUtils.isEmpty(holder.targetInstanceId)? mInstanceId : holder.targetInstanceId;

                ExpressionPair expressionPair = holder.expressionPair;
                if(!ExpressionPair.isValid(expressionPair)) {
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
                //apply transformation/layout change ... to target view.

                View targetView = mPlatformManager.getViewFinder().findViewBy(holder.targetRef, instanceId);
                BindingXPropertyInterceptor.getInstance().performIntercept(
                        targetView,
                        holder.prop,
                        obj,
                        mPlatformManager.getResolutionTranslator(),
                        holder.config,
                        holder.targetRef,
                        instanceId
                );

                if (targetView == null) {
                    LogProxy.e("failed to execute expression,target view not found.[ref:" + holder.targetRef + "]");
                    continue;
                }

                // default behavior
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

    protected abstract void onUserIntercept(String interceptorName, @NonNull Map<String,Object> scope);

    protected void clearExpressions() {
        LogProxy.d("all expression are cleared");
        if (mExpressionHoldersMap != null) {
            mExpressionHoldersMap.clear();
            mExpressionHoldersMap = null;
        }
        mExitExpressionPair = null;
    }

    @Override
    public void setToken(String token) {
        this.mToken = token;
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
