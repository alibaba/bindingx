package com.alibaba.android.bindingx.plugin.weex;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionOrientationHandler;
import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionPair;
import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionScrollHandler;
import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionTimingHandler;
import com.alibaba.android.bindingx.plugin.weex.internal.ExpressionTouchHandler;
import com.alibaba.android.bindingx.plugin.weex.internal.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class ExpressionBindingCore {
    private Map<String/*token*/, Map<String/*event type*/, IEventHandler>> mBindingCouples;
    private final Map<String, ObjectCreator<IEventHandler, Context>> mInternalEventHandlerCreatorMap =
            new HashMap<>(8);
    private final PlatformManager mPlatformManager;

    /**
     * default constructor
     * @param platformManager a class that provide platform-compatible APIs.
     *                        The platform includes Weex and ReactNative.
     * */
    public ExpressionBindingCore(@NonNull PlatformManager platformManager) {
        this.mPlatformManager = platformManager;
        registerEventHandler(EventType.TYPE_PAN, new ObjectCreator<IEventHandler, Context>() {
            @Override
            public IEventHandler createWith(Context context, Object... extension) {
                return new ExpressionTouchHandler(context, extension);
            }
        });
        registerEventHandler(EventType.TYPE_ORIENTATION, new ObjectCreator<IEventHandler, Context>() {
            @Override
            public IEventHandler createWith(Context context, Object... extension) {
                return new ExpressionOrientationHandler(context, extension);
            }
        });
        registerEventHandler(EventType.TYPE_SCROLL, new ObjectCreator<IEventHandler, Context>() {
            @Override
            public IEventHandler createWith(Context context, Object... extension) {
                return new ExpressionScrollHandler(context, extension);
            }
        });
        registerEventHandler(EventType.TYPE_TIMING, new ObjectCreator<IEventHandler, Context>() {
            @Override
            public IEventHandler createWith(Context context, Object... extension) {
                return new ExpressionTimingHandler(context, extension);
            }
        });
    }

    /**
     * @return 如果成功，则返回token，否则返回null
     */
    public String doBind(@Nullable Context context,
                         @Nullable String instanceId,
                         @NonNull Map<String, Object> params,
                         @NonNull JavaScriptCallback callback) {
        String eventType = Utils.getStringValue(params, ExpressionConstants.KEY_EVENT_TYPE);
        String config = Utils.getStringValue(params, ExpressionConstants.KEY_OPTIONS);
        String anchorInstanceId = Utils.getStringValue(params, ExpressionConstants.KEY_INSTANCE_ID);

        //全局配置
        Map<String, Object> configMap = null;
        if (!TextUtils.isEmpty(config)) {
            try {
                configMap = Utils.toMap(new JSONObject(config));
            } catch (Exception e) {
                LogProxy.e("parse external config failed.\n", e);
            }
        }

        ExpressionPair exitExpressionPair = Utils.getExpressionPair(params, ExpressionConstants.KEY_EXIT_EXPRESSION);

        String anchor = Utils.getStringValue(params, ExpressionConstants.KEY_ANCHOR); //可能为空
        List<Map<String, Object>> expressionArgs = Utils.getRuntimeProps(params);

        return doBind(anchor, anchorInstanceId, eventType, configMap, exitExpressionPair, expressionArgs, callback, context, instanceId);
    }

    public void doUnbind(@Nullable Map<String, Object> params) {
        if (params == null) {
            return;
        }
        String eventType = Utils.getStringValue(params, ExpressionConstants.KEY_EVENT_TYPE);
        String token = Utils.getStringValue(params, ExpressionConstants.KEY_TOKEN);

        doUnbind(token, eventType);
    }


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

        // 生成token，如果是pan/scroll类型，那token即view ref.
        final String token = TextUtils.isEmpty(anchor) ? generateToken() : anchor;

        if (mBindingCouples == null) {
            mBindingCouples = new HashMap<>();
        }

        //根据sourceRef寻找事件处理器集合
        Map<String/*eventType*/, IEventHandler> handlerMap = mBindingCouples.get(token);
        //根据eventType寻找目标处理器
        IEventHandler targetHandler;
        if (handlerMap != null && (targetHandler = handlerMap.get(eventType)) != null) {/*处理器存在*/
            //通知handler
            LogProxy.d("you have already enabled binding,[token:" + token + ",type:" + eventType + "]");
            targetHandler.onStart(token, eventType);
            LogProxy.d("enableBinding success.[token:" + token + ",type:" + eventType + "]");
        } else {/*不存在*/
            //集合未创建 则创建之,并插入
            if (handlerMap == null) {
                handlerMap = new HashMap<>(4);
                mBindingCouples.put(token, handlerMap);
            }
            //创建handler
            targetHandler = createEventHandler(context, instanceId, eventType);
            if (targetHandler != null) {//创建成功
                /*可能anchor不在当前instance中*/
                targetHandler.setAnchorInstanceId(anchorInstanceId);
                //初始化
                if (targetHandler.onCreate(token, eventType)) {
                    targetHandler.onStart(token, eventType);
                    //添加到handlerMap
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
     * @param anchor             锚点。是一个view的引用(ref)。可能为空。Notice: ref全局唯一。
     * @param anchorInstanceId   weex实例id。代表anchor所在的weex实例。默认是当前module所在实例。可能为空。
     * @param eventType          事件类型。如pan、scroll等。
     * @param globalConfig       全局配置。
     * @param exitExpressionPair 边界条件表达式。
     * @param expressionArgs     运行时参数。用于控制视图变换。
     * @param callback           事件回调。
     * @param context            上下文
     * @param instanceId         页面id。可选
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

    public void registerEventHandler(String eventType, ObjectCreator<IEventHandler, Context> creator) {
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
        if (mInternalEventHandlerCreatorMap.isEmpty()) {
            return null;
        }
        ObjectCreator<IEventHandler, Context> creator = mInternalEventHandlerCreatorMap.get(eventType);
        return (creator != null) ? creator.createWith(context, instanceId) : null;
    }

    /**
     * Provide instance of {@code Type}.
     */
    public interface ObjectCreator<Type, Param> {
        Type createWith(Param p, Object... extension);
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
