package com.alibaba.android.binding.plugin.weex.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.binding.plugin.weex.ExpressionConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public final class Utils {

    private Utils(){}

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        if(object == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        if(array == null) {
            return Collections.emptyList();
        }
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    @Nullable
    public static String getStringValue(@NonNull Map<String,Object> params, @NonNull String key) {
        Object value = params.get(key);
        if(value == null) {
            return null;
        }

        if(value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getRuntimeProps(@NonNull Map<String,Object> params) {
        Object result = params.get(ExpressionConstants.KEY_RUNTIME_PROPS);
        if(result == null) {
            return null;
        }
        try {
            return (List<Map<String,Object>>)result;
        }catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static ExpressionPair getExpressionPair(@NonNull Map<String,Object> params, @NonNull String key) {
        String raw = getStringValue(params,key);
        if(TextUtils.isEmpty(raw)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(raw);
            String origin = jsonObject.optString(ExpressionConstants.KEY_ORIGIN,null);
            String transformed = jsonObject.optString(ExpressionConstants.KEY_TRANSFORMED,null);
            if(TextUtils.isEmpty(origin) && TextUtils.isEmpty(transformed)) {
                //说明是老的协议
                return ExpressionPair.create(null,raw);
            }
            //新协议
            return ExpressionPair.create(origin,transformed);
        }catch (Exception e) {
            //转换失败
            return ExpressionPair.create(null,raw);
        }

    }

    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<E>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
}
