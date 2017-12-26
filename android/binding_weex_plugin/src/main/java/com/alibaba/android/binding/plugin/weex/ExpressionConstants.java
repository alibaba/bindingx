package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

class ExpressionConstants {
    static final String TAG = "ExpressionBinding";

    static final String STATE_START = "start";
    static final String STATE_END = "end";
    static final String STATE_CANCEL = "cancel";
    static final String STATE_EXIT = "exit";
    static final String STATE_TURNING = "turn";

    static final String KEY_ELEMENT = "element";
    static final String KEY_PROPERTY = "property";
    static final String KEY_EXPRESSION = "expression";
    static final String KEY_CONFIG = "config";
    static final String KEY_OPTIONS = "options";


    static final String KEY_ANCHOR = "anchor";
    static final String KEY_EVENT_TYPE = "eventType";
    static final String KEY_INSTANCE_ID = "instanceId";
    static final String KEY_EXIT_EXPRESSION = "exitExpression";
    static final String KEY_RUNTIME_PROPS = "props";
    static final String KEY_TOKEN = "token";

    static final String KEY_SCENE_TYPE = "sceneType";

    static final String KEY_TRANSFORMED = "transformed";
    static final String KEY_ORIGIN = "origin";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ExpressionConstants.STATE_START, ExpressionConstants.STATE_END,
            ExpressionConstants.STATE_CANCEL, ExpressionConstants.STATE_EXIT, ExpressionConstants.STATE_TURNING})
    @interface State {
    }
}
