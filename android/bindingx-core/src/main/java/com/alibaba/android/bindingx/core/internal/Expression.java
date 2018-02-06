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


import com.alibaba.android.bindingx.core.LogProxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Execute transformed expression use recursive way
 * */
class Expression {


    JSONObject root;

    Expression(String json) {
        try {
            this.root = (JSONObject) new JSONTokener(json).nextValue();
        } catch (Throwable e) {
            LogProxy.e("[Expression] expression is illegal. \n ", e);
        }
    }

    Expression(JSONObject root) {
        this.root = root;
    }

    Object execute(Map<String, Object> scope) throws IllegalArgumentException, JSONException {
        return execute(this.root, scope);
    }

    private double toNumber(Object value) {
        if (value instanceof String)
            return Double.parseDouble((String) value);
        if (value instanceof Boolean)
            return (boolean) value ? 1.0 : 0.0;
        return (double) value;
    }

    private boolean toBoolean(Object value) {
        if (value instanceof String)
            return (String) value == "";
        if (value instanceof Double)
            return (double) value != 0;
        return ((Boolean) value).booleanValue();
    }

    private Object toPrimitive(Object value) {
        if (value instanceof JSObjectInterface)
            return "[object Object]";
        return value;
    }


    private String toString(Object value) {
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue() ? "true" : "false";
        if (value instanceof Double)
            return Double.toString((Double) value);
        return (String) value;
    }

    private boolean equal(Object v1, Object v2) {
        if (v1 instanceof JSObjectInterface
                && v2 instanceof JSObjectInterface)
            return v1 == v2;
        if (v1 instanceof String
                && v2 instanceof String)
            return v1.equals(v2);
        if (v1 instanceof Boolean
                && v2 instanceof Boolean)
            return toBoolean(v1) == toBoolean(v2);
        return toNumber(v1) == toNumber(v2);
    }

    private boolean strictlyEqual(Object v1, Object v2) {
        if (v1 instanceof JSObjectInterface
                && !(v2 instanceof JSObjectInterface))
            return false;

        if (v1 instanceof Boolean
                && !(v2 instanceof Boolean))
            return false;

        if (v1 instanceof Double
                && !(v2 instanceof Double))
            return false;

        if (v1 instanceof String
                && !(v2 instanceof String))
            return false;
        return v1 == v2;
    }

    private Object execute(JSONObject node, Map<String, Object> scope) throws IllegalArgumentException, JSONException {

        String type = node.getString("type");
        JSONArray children = node.optJSONArray("children");
        switch (type) {
            case "StringLiteral":
                return node.getString("value");
            case "NumericLiteral":
                return node.getDouble("value");
            case "BooleanLiteral":
                return node.getBoolean("value");
            case "Identifier":
                return scope.get(node.getString("value"));
            case "CallExpression":
                JSFunctionInterface function = (JSFunctionInterface) execute(children.getJSONObject(0), scope);
                ArrayList<Object> arguments = new ArrayList<Object>();
                JSONArray jsonArguments = children.getJSONObject(1).getJSONArray("children");
                for (int i = 0; i < jsonArguments.length(); i++)
                    arguments.add(execute(jsonArguments.getJSONObject(i), scope));
                return function.execute(arguments);

            case "?":
                if ((Boolean) execute(children.getJSONObject(0), scope))
                    return execute(children.getJSONObject(1), scope);
                else
                    return execute(children.getJSONObject(2), scope);

            case "+":
                return toNumber(execute(children.getJSONObject(0), scope)) + toNumber(execute(children.getJSONObject(1), scope));
            case "-":
                return toNumber(execute(children.getJSONObject(0), scope)) - toNumber(execute(children.getJSONObject(1), scope));
            case "*":
                return toNumber(execute(children.getJSONObject(0), scope)) * toNumber(execute(children.getJSONObject(1), scope));
            case "/":
                return toNumber(execute(children.getJSONObject(0), scope)) / toNumber(execute(children.getJSONObject(1), scope));
            case "%":
                return toNumber(execute(children.getJSONObject(0), scope)) % toNumber(execute(children.getJSONObject(1), scope));
            case "**":
                return Math.pow(toNumber(execute(children.getJSONObject(0), scope)), toNumber(execute(children.getJSONObject(1), scope)));

            case ">":
                return toNumber(execute(children.getJSONObject(0), scope)) > toNumber(execute(children.getJSONObject(1), scope));
            case "<":
                return toNumber(execute(children.getJSONObject(0), scope)) < toNumber(execute(children.getJSONObject(1), scope));
            case ">=":
                return toNumber(execute(children.getJSONObject(0), scope)) >= toNumber(execute(children.getJSONObject(1), scope));
            case "<=":
                return toNumber(execute(children.getJSONObject(0), scope)) <= toNumber(execute(children.getJSONObject(1), scope));

            case "==":
                return equal(execute(children.getJSONObject(0), scope), execute(children.getJSONObject(1), scope));
            case "===":
                return strictlyEqual(execute(children.getJSONObject(0), scope), execute(children.getJSONObject(1), scope));
            case "!=":
                return !equal(execute(children.getJSONObject(0), scope), execute(children.getJSONObject(1), scope));
            case "!==":
                return !strictlyEqual(execute(children.getJSONObject(0), scope), execute(children.getJSONObject(1), scope));

            case "&&":
                Object result;
                result = execute(children.getJSONObject(0), scope);
                if (!toBoolean(result))
                    return result;
                return execute(children.getJSONObject(1), scope);
            case "||":
                result = execute(children.getJSONObject(0), scope);
                if (toBoolean(result))
                    return result;
                return execute(children.getJSONObject(1), scope);
            case "!":
                return !toBoolean(execute(children.getJSONObject(0), scope));

        }
        return null;
    }

}
