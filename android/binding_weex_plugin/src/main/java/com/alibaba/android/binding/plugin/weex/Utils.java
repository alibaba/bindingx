package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

final class Utils {

    private Utils(){}

    @Nullable
    static String getStringValue(@NonNull Map<String,Object> params, @NonNull String key) {
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
    static List<Map<String, Object>> getRuntimeProps(@NonNull Map<String,Object> params) {
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
    static ExpressionPair getExpressionPair(@NonNull Map<String,Object> params, @NonNull String key) {
        String raw = getStringValue(params,key);
        if(TextUtils.isEmpty(raw)) {
            return null;
        }

        try {
            Map<String,Object> map = JSON.parseObject(raw);
            String origin = getStringValue(map,ExpressionConstants.KEY_ORIGIN);
            String transformed = getStringValue(map,ExpressionConstants.KEY_TRANSFORMED);
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
    static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    @SafeVarargs
    static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<E>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
}
