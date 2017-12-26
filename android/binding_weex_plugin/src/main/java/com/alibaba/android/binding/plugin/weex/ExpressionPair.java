package com.alibaba.android.binding.plugin.weex;

import android.support.annotation.Nullable;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

class ExpressionPair {

    //原生表达式
    final String origin;

    //转化后表达式
    final String transformed;

    ExpressionPair(String origin,String transformed) {
        this.origin = origin;
        this.transformed = transformed;
    }

    static ExpressionPair create(@Nullable String origin, @Nullable String transformed) {
        return new ExpressionPair(origin,transformed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionPair that = (ExpressionPair) o;

        if (origin != null ? !origin.equals(that.origin) : that.origin != null) return false;
        return transformed != null ? transformed.equals(that.transformed) : that.transformed == null;

    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (transformed != null ? transformed.hashCode() : 0);
        return result;
    }
}
