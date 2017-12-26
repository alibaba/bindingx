package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.utils.WXLogUtils;

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

class ExpressionBindingCore{

    private static final String TAG = ExpressionConstants.TAG;

    private Map<String/*token*/, Map<String/*event type*/,IEventHandler>> mBindingCouples;

    /**
     * @return 如果成功，则返回token，否则返回null
     * */
    String doBind(@NonNull Map<String, Object> params, @NonNull JSCallback callback, @NonNull WXSDKInstance instance) {
        String eventType = Utils.getStringValue(params,ExpressionConstants.KEY_EVENT_TYPE);
        String config = Utils.getStringValue(params,ExpressionConstants.KEY_OPTIONS);
        String anchorInstanceId = Utils.getStringValue(params,ExpressionConstants.KEY_INSTANCE_ID);

        //全局配置
        Map<String,Object> configMap = null;
        if(!TextUtils.isEmpty(config)) {
            try {
                configMap = Utils.toMap(new JSONObject(config));
            }catch (Exception e) {
                WXLogUtils.e(TAG,"parse external config failed.\n"+e.getMessage());
            }
        }

        ExpressionPair exitExpressionPair = Utils.getExpressionPair(params,ExpressionConstants.KEY_EXIT_EXPRESSION);

        String anchor = Utils.getStringValue(params,ExpressionConstants.KEY_ANCHOR); //可能为空
        List<Map<String, Object>> expressionArgs = Utils.getRuntimeProps(params);

        return doBind(anchor,anchorInstanceId,eventType,configMap,exitExpressionPair,expressionArgs,callback, instance);
    }

    void doUnbind(@Nullable Map<String, Object> params) {
        if(params == null) {
            return;
        }
        String eventType = Utils.getStringValue(params, ExpressionConstants.KEY_EVENT_TYPE);
        String token = Utils.getStringValue(params, ExpressionConstants.KEY_TOKEN);

        doUnbind(token,eventType);
    }


    void doUnbind(@Nullable String token, @Nullable String eventType) {
        WXLogUtils.d(TAG, "disable binding [" + token + "," + eventType + "]");
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(eventType)) {
            WXLogUtils.d(TAG, "disable binding failed(0x1) [" + token + "," + eventType + "]");
            return;
        }
        if (mBindingCouples == null || mBindingCouples.isEmpty()) {
            WXLogUtils.d(TAG, "disable binding failed(0x2) [" + token + "," + eventType + "]");
            return;
        }

        Map<String/*eventType*/,IEventHandler> handlerMap = mBindingCouples.get(token);
        if(handlerMap == null || handlerMap.isEmpty()) {
            WXLogUtils.d(TAG, "disable binding failed(0x3) [" + token + "," + eventType + "]");
            return;
        }
        IEventHandler handler = handlerMap.get(eventType);
        if (handler == null) {
            WXLogUtils.d(TAG, "disable binding failed(0x4) [" + token + "," + eventType + "]");
            return;
        }

        if(handler.onDisable(token,eventType)) {
            mBindingCouples.remove(token);
            WXLogUtils.d(TAG, "disable binding success[" + token + "," + eventType + "]");
        } else {
            WXLogUtils.d(TAG, "disabled failed(0x4) [" + token + "," + eventType + "]");
        }
    }

    void doRelease() {
        if (mBindingCouples != null) {
            try {
                for(Map<String/*eventType*/,IEventHandler> handlerMap : mBindingCouples.values()) {
                    if(handlerMap != null && !handlerMap.isEmpty()) {
                        for(IEventHandler h : handlerMap.values()) {
                            if (h != null) {
                                h.onDestroy();
                            }
                        }
                    }
                }
                mBindingCouples.clear();
                mBindingCouples = null;
            }catch (Exception e) {
                WXLogUtils.e(TAG, e.getMessage());
            }
        }
    }

    String doPrepare(@Nullable String anchor, @Nullable String anchorInstanceId, @Nullable String eventType, @NonNull WXSDKInstance instance){
        if(TextUtils.isEmpty(eventType)) {
            WXLogUtils.e(TAG,"[doPrepare] failed. can not found eventType");
            return null;
        }

        if (instance.getContext() == null) {
            WXLogUtils.e(TAG, "[doPrepare] failed. context or wxInstance is null");
            return null;
        }

        // 生成token，如果是pan/scroll类型，那token即view ref.
        final String token = TextUtils.isEmpty(anchor) ? InnerTokenGenerator.generate() : anchor;

        if (mBindingCouples == null) {
            mBindingCouples = new HashMap<>();
        }

        //根据sourceRef寻找事件处理器集合
        Map<String/*eventType*/,IEventHandler> handlerMap = mBindingCouples.get(token);
        //根据eventType寻找目标处理器
        IEventHandler targetHandler;
        if(handlerMap != null && (targetHandler = handlerMap.get(eventType)) != null) {/*处理器存在*/
            //通知handler
            if(WXEnvironment.isApkDebugable()) {
                WXLogUtils.d(TAG, "you have already enabled binding,[token:" + token + ",type:" + eventType+"]");
            }
            targetHandler.onStart(token,eventType);
            if(WXEnvironment.isApkDebugable()) {
                WXLogUtils.d(TAG, "enableBinding success.[token:" + token + ",type:" + eventType + "]");
            }
        } else {/*不存在*/
            //集合未创建 则创建之,并插入
            if(handlerMap == null) {
                handlerMap = new HashMap<>(4);
                mBindingCouples.put(token,handlerMap);
            }
            //创建handler
            targetHandler = createEventHandler(eventType,instance);
            if(targetHandler != null) {//创建成功
                /*可能anchor不在当前instance中*/
                targetHandler.setAnchorInstanceId(anchorInstanceId);
                //初始化
                if(targetHandler.onCreate(token,eventType)) {
                    targetHandler.onStart(token,eventType);
                    //添加到handlerMap
                    handlerMap.put(eventType, targetHandler);

                    if(WXEnvironment.isApkDebugable()) {
                        WXLogUtils.d(TAG, "enableBinding success.[token:" + token + ",type:" + eventType + "]");
                    }
                } else {
                    WXLogUtils.e(TAG,"expression enabled failed. [token:"+token+",type:"+eventType+"]");
                    return null;
                }
            } else {
                WXLogUtils.e(TAG,"unknown eventType: " + eventType);
                return null;
            }

        }

        return token;
    }


    /**
     *
     * @param anchor 锚点。是一个view的引用(ref)。可能为空。Notice: ref全局唯一。
     * @param anchorInstanceId weex实例id。代表anchor所在的weex实例。默认是当前module所在实例。可能为空。
     * @param eventType 事件类型。如pan、scroll等。
     * @param globalConfig 全局配置。
     * @param exitExpressionPair 边界条件表达式。
     * @param expressionArgs 运行时参数。用于控制视图变换。
     * @param callback 事件回调。
     * @param instance 当前weex实例。
     * */
    String doBind(@Nullable String anchor, @Nullable String anchorInstanceId, @Nullable String eventType, @Nullable Map<String,Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                  @Nullable List<Map<String, Object>> expressionArgs, @Nullable JSCallback callback, @NonNull WXSDKInstance instance) {

        if (TextUtils.isEmpty(eventType) || expressionArgs == null) {
            WXLogUtils.e(TAG, "doBind failed,illegal argument.[" + eventType + "," + expressionArgs + "]");
            return null;
        }

        IEventHandler handler = null;
        Map<String/*eventType*/,IEventHandler> handlerMap = null;
        String token = anchor;
        if(mBindingCouples != null && !TextUtils.isEmpty(anchor) && (handlerMap=mBindingCouples.get(anchor)) != null) {
            handler = handlerMap.get(eventType);
        }

        if(handler == null) {
            if(WXEnvironment.isApkDebugable()) {
                WXLogUtils.d(TAG, "binding not enabled,try auto enable it.[sourceRef:"+anchor+",eventType:"+eventType+"]");
            }
            token = doPrepare(anchor,anchorInstanceId, eventType,instance);
            if(!TextUtils.isEmpty(token) && mBindingCouples != null && (handlerMap=mBindingCouples.get(token)) != null) {
                handler = handlerMap.get(eventType);
            }
        }

        if(handler != null) {
            handler.onBindExpression(eventType, globalConfig, exitExpressionPair, expressionArgs, callback);
            WXLogUtils.d(TAG, "createBinding success.[exitExp:" + exitExpressionPair + ",args:" + expressionArgs + "]");
        } else {
            WXLogUtils.e(TAG, "internal error.binding failed for ref:" + anchor + ",type:" + eventType);
        }

        return token;
    }

    @Nullable
    private IEventHandler createEventHandler(@NonNull String eventType, @NonNull WXSDKInstance instance) {
        /*手势相关*/
        if (EventType.TYPE_PAN.equals(eventType) || EventType.TYPE_FLICK.equals(eventType)) {
            return new ExpressionTouchHandler(instance);
        /*设备方向*/
        }else if (EventType.TYPE_ORIENTATION.equals(eventType)) {
            return new ExpressionOrientationHandler(instance);
        /*content offset*/
        } else if (EventType.TYPE_SCROLL.equals(eventType)) {
            return new ExpressionScrollHandler(instance);
        /*timing*/
        } else if (EventType.TYPE_TIMING.equals(eventType)) {
            return new ExpressionTimingHandler(instance);
        }

        return null;
    }


    private static class InnerTokenGenerator {

        static String generate() {
            return UUID.randomUUID().toString();
        }
    }

    void onActivityPause() {
        if(mBindingCouples == null) {
            return;
        }
        try {
            for(Map<String,IEventHandler> map : mBindingCouples.values()) {
                for(IEventHandler h : map.values()) {
                    try {
                        h.onActivityPause();
                    }catch (Exception e) {
                        WXLogUtils.e(TAG,e.getMessage());
                    }
                }
            }
        }catch (Exception e) {
            WXLogUtils.e(TAG, e.getMessage());
        }
    }

    void onActivityResume() {
        if(mBindingCouples == null) {
            return;
        }
        try {
            for(Map<String,IEventHandler> map : mBindingCouples.values()) {
                for(IEventHandler h : map.values()) {
                    try {
                        h.onActivityResume();
                    }catch (Exception e) {
                        WXLogUtils.e(TAG,e.getMessage());
                    }
                }
            }
        }catch (Exception e) {
            WXLogUtils.e(TAG, e.getMessage());
        }
    }
}
