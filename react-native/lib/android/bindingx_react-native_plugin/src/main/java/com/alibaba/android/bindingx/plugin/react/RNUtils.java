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
package com.alibaba.android.bindingx.plugin.react;

public class RNUtils {

    public static int getInt(Object value, int defaultValue) {
        if(value == null || !(value instanceof String)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt((String) value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(Object value, String defaultValue) {
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }
}
