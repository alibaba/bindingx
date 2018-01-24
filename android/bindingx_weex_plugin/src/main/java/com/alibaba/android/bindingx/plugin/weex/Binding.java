package com.alibaba.android.bindingx.plugin.weex;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;

/**
 * Description:
 *
 * register binding module
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class Binding {
    private Binding(){}

    /**
     * register binding module
     * */
    public static void register() throws WXException{
        WXSDKEngine.registerModule("expressionBinding", WXExpressionBindingModule.class);
        WXSDKEngine.registerModule("binding", WXExpressionBindingV2Module.class);
    }
}
