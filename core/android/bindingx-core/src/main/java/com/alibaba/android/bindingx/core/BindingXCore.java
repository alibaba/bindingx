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
package com.alibaba.android.bindingx.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.core.internal.BindingXOrientationHandler;
import com.alibaba.android.bindingx.core.internal.ExpressionPair;
import com.alibaba.android.bindingx.core.internal.BindingXTimingHandler;
import com.alibaba.android.bindingx.core.internal.BindingXTouchHandler;
import com.alibaba.android.bindingx.core.internal.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 *
 * The core class of BindingX. This class is used to create {@link IEventHandler}
 * by name, bind or unbind expression, and update views according to the result of expressions.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXCore {
    private Map<String/*token*/, Map<String/*event type*/, IEventHandler>> mBindingCouples;
    private final Map<String, ObjectCreator<IEventHandler, Context, PlatformManager>> mInternalEventHandlerCreatorMap =
            new HashMap<>(8);
    private final PlatformManager mPlatformManager;

    /**
     * default constructor
     * @param platformManager a class that provide platform-compatible APIs.
     *                        The platform includes Weex and ReactNative.
     * */
    public BindingXCore(@NonNull PlatformManager platformManager) {
        this.mPlatformManager = platformManager;
        registerEventHandler(BindingXEventType.TYPE_PAN, new ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(@NonNull Context context,@NonNull PlatformManager manager, Object... extension) {
                return new BindingXTouchHandler(context, manager, extension);
            }
        });
        registerEventHandler(BindingXEventType.TYPE_ORIENTATION, new ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(@NonNull Context context,@NonNull PlatformManager manager, Object... extension) {
                return new BindingXOrientationHandler(context, manager, extension);
            }
        });
        registerEventHandler(BindingXEventType.TYPE_TIMING, new ObjectCreator<IEventHandler, Context, PlatformManager>() {
            @Override
            public IEventHandler createWith(@NonNull Context context,@NonNull PlatformManager manager, Object... extension) {
                return new BindingXTimingHandler(context, manager, extension);
            }
        });
    }

    /**
     *
     * bind event handler by params.
     *
     * @param context the android {@link Context} instance
     * @param instanceId the additional instance id
     * @param params the params which include expression/properties/elements and so on
     * @param callback the callback that will be invoked later
     * @return return token if success or null otherwise
     */
    public String doBind(@Nullable Context context,
                         @Nullable String instanceId,
                         @NonNull Map<String, Object> params,
                         @NonNull JavaScriptCallback callback) {
        String eventType = Utils.getStringValue(params, BindingXConstants.KEY_EVENT_TYPE);
        String anchorInstanceId = Utils.getStringValue(params, BindingXConstants.KEY_INSTANCE_ID);
        LogProxy.enableLogIfNeeded(params);
        Object configObj = params.get(BindingXConstants.KEY_OPTIONS);
        Map<String, Object> configMap = null;
        if(configObj != null && configObj instanceof Map) {
            try {
                configMap = Utils.toMap(new JSONObject((Map)configObj));
            } catch (Exception e) {
                LogProxy.e("parse external config failed.\n", e);
            }
        }

        ExpressionPair exitExpressionPair = Utils.getExpressionPair(params, BindingXConstants.KEY_EXIT_EXPRESSION);

        String anchor = Utils.getStringValue(params, BindingXConstants.KEY_ANCHOR); // maybe nullable
        List<Map<String, Object>> expressionArgs = Utils.getRuntimeProps(params);

        return doBind(anchor, anchorInstanceId, eventType, configMap, exitExpressionPair, expressionArgs, callback, context, instanceId);
    }

    /**
     * unbind event handler
     *
     * */
    public void doUnbind(@Nullable Map<String, Object> params) {
        if (params == null) {
            return;
        }
        String eventType = Utils.getStringValue(params, BindingXConstants.KEY_EVENT_TYPE);
        String token = Utils.getStringValue(params, BindingXConstants.KEY_TOKEN);

        doUnbind(token, eventType);
    }


    /**
     * unbind event handler
     *
     * */
    public void doUnbind(@Nullable String token, @Nullable String eventType) {
        LogProxy.d("disable binding [" + token + "," + eventType + "]");
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(eventType)) {
            LogProxy.d("disable binding failed(0x1) [" + token + "," + eventType + "]");
            return;
        }
        if (mBindingCouples == null || mBindingCouples.isEmpty()) {
            LogProxy.d("disable binding failed(0x2) [" + token + "," + eventType + "]");
            return;
        }

        Map<String/*eventType*/, IEventHandler> handlerMap = mBindingCouples.get(token);
        if (handlerMap == null || handlerMap.isEmpty()) {
            LogProxy.d("disable binding failed(0x3) [" + token + "," + eventType + "]");
            return;
        }
        IEventHandler handler = handlerMap.get(eventType);
        if (handler == null) {
            LogProxy.d("disable binding failed(0x4) [" + token + "," + eventType + "]");
            return;
        }

        if (handler.onDisable(token, eventType)) {
            mBindingCouples.remove(token);
            LogProxy.d("disable binding success[" + token + "," + eventType + "]");
        } else {
            LogProxy.d("disabled failed(0x4) [" + token + "," + eventType + "]");
        }
    }

    public void doRelease() {
        if (mBindingCouples != null) {
            try {
                for (Map<String/*eventType*/, IEventHandler> handlerMap : mBindingCouples.values()) {
                    if (handlerMap != null && !handlerMap.isEmpty()) {
                        for (IEventHandler h : handlerMap.values()) {
                            if (h != null) {
                                h.onDestroy();
                            }
                        }
                    }
                }
                mBindingCouples.clear();
                mBindingCouples = null;
            } catch (Exception e) {
                LogProxy.e("release failed", e);
            }
        }
    }

    public String doPrepare(@Nullable Context context,
                            @Nullable String instanceId, /*optional default instance id*/
                            @Nullable String anchor,
                            @Nullable String anchorInstanceId,
                            @Nullable String eventType) {
        if (TextUtils.isEmpty(eventType)) {
            LogProxy.e("[doPrepare] failed. can not found eventType");
            return null;
        }

        if (context == null) {
            LogProxy.e("[doPrepare] failed. context or wxInstance is null");
            return null;
        }

        // generate token. If event type is pan or scroll, then the token will be view's ref
        final String token = TextUtils.isEmpty(anchor) ? generateToken() : anchor;

        if (mBindingCouples == null) {
            mBindingCouples = new HashMap<>();
        }

        // look for the collections of eventHandlers by token.
        Map<String/*eventType*/, IEventHandler> handlerMap = mBindingCouples.get(token);
        // look for the target event handler by event type
        IEventHandler targetHandler;
        if (handlerMap != null && (targetHandler = handlerMap.get(eventType)) != null) {/*event handler exists*/
            //notify that event handler
            LogProxy.d("you have already enabled binding,[token:" + token + ",type:" + eventType + "]");
            targetHandler.onStart(token, eventType);
            LogProxy.d("enableBinding success.[token:" + token + ",type:" + eventType + "]");
        } else {/*not exists*/
            // create the collection and insert to it if collections is empty
            if (handlerMap == null) {
                handlerMap = new HashMap<>(4);
                mBindingCouples.put(token, handlerMap);
            }
            // create event handler
            targetHandler = createEventHandler(context, instanceId, eventType);
            if (targetHandler != null) {//create success
                /*maybe anchor is not in current instance*/
                targetHandler.setAnchorInstanceId(anchorInstanceId);
                targetHandler.setToken(token);
                if (targetHandler.onCreate(token, eventType)) {
                    targetHandler.onStart(token, eventType);
                    // put to the handler map
                    handlerMap.put(eventType, targetHandler);
                    LogProxy.d("enableBinding success.[token:" + token + ",type:" + eventType + "]");
                } else {
                    LogProxy.e("expression enabled failed. [token:" + token + ",type:" + eventType + "]");
                    return null;
                }
            } else {
                LogProxy.e("unknown eventType: " + eventType);
                return null;
            }

        }

        return token;
    }


    /**
     * @param anchor             a reference of some view. Maybe null
     * @param anchorInstanceId   optional instance id of anchor
     * @param eventType          event type such as pan
     * @param globalConfig       global config
     * @param exitExpressionPair exit expression
     * @param expressionArgs     runtime props
     * @param callback           result callback
     * @param context            android context
     * @param instanceId         optional instance id
     */
    public String doBind(@Nullable String anchor,
                         @Nullable String anchorInstanceId,
                         @Nullable String eventType,
                         @Nullable Map<String, Object> globalConfig,
                         @Nullable ExpressionPair exitExpressionPair,
                         @Nullable List<Map<String, Object>> expressionArgs,
                         @Nullable JavaScriptCallback callback,
                         @Nullable Context context,
                         @Nullable String instanceId) {

        if (TextUtils.isEmpty(eventType) || expressionArgs == null) {
            LogProxy.e("doBind failed,illegal argument.[" + eventType + "," + expressionArgs + "]");
            return null;
        }

        IEventHandler handler = null;
        Map<String/*eventType*/, IEventHandler> handlerMap = null;
        String token = anchor;
        if (mBindingCouples != null && !TextUtils.isEmpty(anchor) && (handlerMap = mBindingCouples.get(anchor)) != null) {
            handler = handlerMap.get(eventType);
        }

        if (handler == null) {
            LogProxy.d("binding not enabled,try auto enable it.[sourceRef:" + anchor + ",eventType:" + eventType + "]");
            token = doPrepare(context, instanceId, anchor, anchorInstanceId, eventType);
            if (!TextUtils.isEmpty(token) && mBindingCouples != null && (handlerMap = mBindingCouples.get(token)) != null) {
                handler = handlerMap.get(eventType);
            }
        }

        if (handler != null) {
            handler.onBindExpression(eventType, globalConfig, exitExpressionPair, expressionArgs, callback);
            LogProxy.d("createBinding success.[exitExp:" + exitExpressionPair + ",args:" + expressionArgs + "]");
        } else {
            LogProxy.e("internal error.binding failed for ref:" + anchor + ",type:" + eventType);
        }

        return token;
    }

    public void onActivityPause() {
        if (mBindingCouples == null) {
            return;
        }
        try {
            for (Map<String, IEventHandler> map : mBindingCouples.values()) {
                for (IEventHandler h : map.values()) {
                    try {
                        h.onActivityPause();
                    } catch (Exception e) {
                        LogProxy.e("execute activity pause failed.", e);
                    }
                }
            }
        } catch (Exception e) {
            LogProxy.e("activity pause failed", e);
        }
    }

    public void onActivityResume() {
        if (mBindingCouples == null) {
            return;
        }
        try {
            for (Map<String, IEventHandler> map : mBindingCouples.values()) {
                for (IEventHandler h : map.values()) {
                    try {
                        h.onActivityResume();
                    } catch (Exception e) {
                        LogProxy.e("execute activity pause failed.", e);
                    }
                }
            }
        } catch (Exception e) {
            LogProxy.e("activity pause failed", e);
        }
    }

    /**
     * register an eventHandler to handle a specific EventType.
     *
     * @param eventType the event type name like pan/orientation
     * @param creator a factory to create an instance of {@link IEventHandler}
     *
     * */
    public void registerEventHandler(String eventType, ObjectCreator<IEventHandler, Context, PlatformManager> creator) {
        if (TextUtils.isEmpty(eventType) || creator == null) {
            return;
        }
        mInternalEventHandlerCreatorMap.put(eventType, creator);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Nullable
    private IEventHandler createEventHandler(@NonNull Context context,
                                             @Nullable String instanceId,
                                             @NonNull String eventType) {
        if (mInternalEventHandlerCreatorMap.isEmpty() || mPlatformManager == null) {
            return null;
        }
        ObjectCreator<IEventHandler, Context, PlatformManager> creator = mInternalEventHandlerCreatorMap.get(eventType);
        return (creator != null) ? creator.createWith(context,mPlatformManager,instanceId) : null;
    }

    /**
     * Provide instance of {@code Type}.
     */
    public interface ObjectCreator<Type, ParamA, ParamB> {
        Type createWith(@NonNull ParamA p1,@NonNull ParamB p2, Object... extension);
    }

    /**
     * Interface that represent standard javascript callback function
     */
    public interface JavaScriptCallback {
        /**
         * @param params arguments passed to javascript callback method via different platform's
         *               bridge
         */
        void callback(Object params);
    }
}
