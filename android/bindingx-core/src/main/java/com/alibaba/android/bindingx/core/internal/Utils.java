package com.alibaba.android.bindingx.core.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

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
        Object result = params.get(BindingXConstants.KEY_RUNTIME_PROPS);
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
            String origin = jsonObject.optString(BindingXConstants.KEY_ORIGIN,null);
            String transformed = jsonObject.optString(BindingXConstants.KEY_TRANSFORMED,null);
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

    /**
     * parse rotation to (-180, 180]
     * */
    public static float normalizeRotation(float rotation) {
        rotation = rotation % 360;
        float normalizedRotation;
        if(rotation >= 0) {
            if(rotation >= 0 && rotation <= 180) {
                normalizedRotation = rotation;
            } else {
                normalizedRotation = (rotation % 180) - 180;
            }
        } else {
            if(rotation > -180 && rotation < 0) {
                normalizedRotation = rotation;
            } else {
                normalizedRotation = 180 + (rotation % 180);
            }
        }
        return normalizedRotation;
    }

    public static int normalizedPerspectiveValue(@NonNull Context context, int raw) {
        //refer: react-native # BaseViewManager
        // The following converts the matrix's perspective to a camera distance
        // such that the camera perspective looks the same on Android and iOS
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (scale * raw * 5)/*CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER*/;
    }

    @Nullable
    public static Pair<Float,Float> parseTransformOrigin(@Nullable String value, @NonNull View view) {
        if(TextUtils.isEmpty(value)) {
            return null;
        }
        int firstSpace = value.indexOf(' ');
        if (firstSpace != -1) {
            int i = firstSpace;
            for (; i < value.length(); i++) {
                if (value.charAt(i) != ' ') {
                    break;
                }
            }

            if (i < value.length() && value.charAt(i) != ' ') {
                String x = value.substring(0, firstSpace).trim();
                String y = value.substring(i, value.length()).trim();

                float pivotX,pivotY;
                if("left".equals(x)) {
                    pivotX = 0f;
                } else if("right".equals(x)) {
                    pivotX = view.getWidth();
                } else if("center".equals(x)) {
                    pivotX = view.getWidth()/2;
                } else {
                    pivotX = view.getWidth()/2;
                }

                if("top".equals(y)) {
                    pivotY = 0;
                } else if("bottom".equals(y)) {
                    pivotY = view.getHeight();
                } else if("center".equals(y)) {
                    pivotY = view.getHeight()/2;
                } else {
                    pivotY = view.getHeight()/2;
                }

                return new Pair<>(pivotX,pivotY);
            }
        }
        return null;
    }
}
