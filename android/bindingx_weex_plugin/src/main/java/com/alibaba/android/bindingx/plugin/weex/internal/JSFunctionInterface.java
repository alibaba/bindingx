package com.alibaba.android.bindingx.plugin.weex.internal;

import org.json.JSONException;

import java.util.ArrayList;

interface JSFunctionInterface extends JSObjectInterface {
    Object execute(ArrayList<Object> arguments) throws NumberFormatException,JSONException;
}
