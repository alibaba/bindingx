package com.alibaba.android.bindingx.plugin.weex;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class ExpressionConstants {
    public static final String TAG = "ExpressionBinding";

    public static final String STATE_START = "start";
    public static final String STATE_END = "end";
    public static final String STATE_CANCEL = "cancel";
    public static final String STATE_EXIT = "exit";
    public static final String STATE_TURNING = "turn";

    public static final String KEY_ELEMENT = "element";
    public static final String KEY_PROPERTY = "property";
    public static final String KEY_EXPRESSION = "expression";
    public static final String KEY_CONFIG = "config";
    public static final String KEY_OPTIONS = "options";


    public static final String KEY_ANCHOR = "anchor";
    public static final String KEY_EVENT_TYPE = "eventType";
    public static final String KEY_INSTANCE_ID = "instanceId";
    public static final String KEY_EXIT_EXPRESSION = "exitExpression";
    public static final String KEY_RUNTIME_PROPS = "props";
    public static final String KEY_TOKEN = "token";

    public static final String KEY_SCENE_TYPE = "sceneType";

    public static final String KEY_TRANSFORMED = "transformed";
    public static final String KEY_ORIGIN = "origin";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ExpressionConstants.STATE_START, ExpressionConstants.STATE_END,
            ExpressionConstants.STATE_CANCEL, ExpressionConstants.STATE_EXIT, ExpressionConstants.STATE_TURNING})
    public @interface State {
    }
}
