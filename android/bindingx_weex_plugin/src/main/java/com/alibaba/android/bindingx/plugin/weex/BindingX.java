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

public class BindingX {
    private BindingX(){}

    /**
     * register binding module
     * */
    public static void register() throws WXException{
        WXSDKEngine.registerModule("expressionBinding", WXExpressionBindingModule.class);
        WXSDKEngine.registerModule("binding", WXBindingXModule.class);
        WXSDKEngine.registerModule("bindingX", WXBindingXModule.class);
    }
}
