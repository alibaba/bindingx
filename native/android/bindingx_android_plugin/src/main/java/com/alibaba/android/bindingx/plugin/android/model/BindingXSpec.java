package com.alibaba.android.bindingx.plugin.android.model;

import com.alibaba.android.bindingx.core.internal.ExpressionPair;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXSpec {
    public String eventType;
    public String anchor;
    public Map<String,Object> options;
    public ExpressionPair exitExpression;
    public List<BindingXPropSpec> expressionProps;
}
