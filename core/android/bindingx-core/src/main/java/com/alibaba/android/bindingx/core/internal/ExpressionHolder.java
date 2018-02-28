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

import java.util.Collections;
import java.util.Map;

final class ExpressionHolder {

    String targetRef;
    String targetInstanceId;
    ExpressionPair expressionPair;
    String prop;
    String eventType;
    Map<String,Object> config;

    ExpressionHolder(String target, String targetInstanceId, ExpressionPair expressionPair, String prop, String eventType, Map<String,Object> config) {
        this.targetRef = target;
        this.targetInstanceId = targetInstanceId;
        this.expressionPair = expressionPair;
        this.prop = prop;
        this.eventType = eventType;
        if(config == null) {
            this.config = Collections.emptyMap();
        } else {
            this.config = Collections.unmodifiableMap(config);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionHolder that = (ExpressionHolder) o;

        if (targetRef != null ? !targetRef.equals(that.targetRef) : that.targetRef != null)
            return false;
        if (expressionPair != null ? !expressionPair.equals(that.expressionPair) : that.expressionPair != null)
            return false;
        if (prop != null ? !prop.equals(that.prop) : that.prop != null) return false;
        if (eventType != null ? !eventType.equals(that.eventType) : that.eventType != null)
            return false;
        return config != null ? config.equals(that.config) : that.config == null;

    }

    @Override
    public int hashCode() {
        int result = targetRef != null ? targetRef.hashCode() : 0;
        result = 31 * result + (expressionPair != null ? expressionPair.hashCode() : 0);
        result = 31 * result + (prop != null ? prop.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        return result;
    }
}