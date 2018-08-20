package com.alibaba.android.playground.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.internal.ExpressionPair;
import com.alibaba.android.bindingx.plugin.android.NativeBindingX;
import com.alibaba.android.bindingx.plugin.android.NativeCallback;
import com.alibaba.android.bindingx.plugin.android.model.BindingXPropSpec;
import com.alibaba.android.bindingx.plugin.android.model.BindingXSpec;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXSliderView extends AbstractAnimatorView{
    private static final int OFFSET_ID = 10000;
    private static final int CONTAINER_ID = 9999;
    private NativeBindingX mNativeBinding;

    private String mEasing = "linear";

    public BindingXSliderView(Context context) {
        super(context);
        init();
    }

    public BindingXSliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNativeBinding = NativeBindingX.create();
        LogProxy.sEnableLog = true;
        this.setId(CONTAINER_ID);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(wrapChild(index,child), index, params);
    }

    private View wrapChild(int index, @NonNull View child) {
        final FrameLayout wrapper = new FrameLayout(this.getContext());
        wrapper.addView(child);
        wrapper.setId(OFFSET_ID + index);

        if (getChildCount() == 1) {
            wrapper.setVisibility(View.VISIBLE);
        } else {
//            child.setVisibility(View.GONE);
        }
        return wrapper;
    }

    @Override
    protected void switchTo(int index) {
        int cur = this.mWhichChild;
        int next = index;

        Log.v(TAG, "switch from " + cur + " to " + next);

        mNativeBinding.unbindAll();
        mNativeBinding.bind(this, createBindingXParams(cur, next) , new NativeCallback() {
            @Override
            public void callback(Map<String, Object> params) {
                Log.v(TAG, "binding callback:" + params.toString());
            }
        });
    }

    private BindingXSpec createBindingXParams(int cur, int next) {
        BindingXSpec spec = new BindingXSpec();

        spec.eventType = BindingXEventType.TYPE_TIMING;
        spec.exitExpression = createExitExpression(mAnimationDuration);
        spec.expressionProps = new LinkedList<>();

        String easing = mEasing; // TODO 暴露给外面
        int end = computeAnimEndValue(cur, next);
        int duration = mAnimationDuration;
        spec.expressionProps.add(createFlipAnimationProps(easing, getScrollY(), end, duration));
        return spec;
    }

    private int computeAnimEndValue(int cur, int next) {
        // TODo 支持loop
        int pageSize = getPageSize();
        return pageSize * next;
    }

    private static BindingXPropSpec createFlipAnimationProps(String easing, int begin, int end, int duration) {
        easing = easing == null ? "linear" : easing;
        BindingXPropSpec spec = new BindingXPropSpec();
        spec.element = String.valueOf(CONTAINER_ID);
        spec.property = "scroll.contentOffsetY";
        int changed = end - begin;
        String origin = String.format(Locale.getDefault(),"%s(t,%d,%d,%d)", easing, begin, changed, duration);
        String transformed = String.format(Locale.getDefault(),"{\"type\":\"CallExpression\",\"children\":[{\"type\":\"Identifier\",\"value\":\"%s\"},{\"type\":\"Arguments\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":%d},{\"type\":\"NumericLiteral\",\"value\":%d},{\"type\":\"NumericLiteral\",\"value\":%d}]}]}",easing, begin, changed, duration);
        spec.expressionPair = ExpressionPair.create(origin, transformed);
        return spec;
    }

    private static ExpressionPair createExitExpression(int duration) {
        String origin = String.format(Locale.getDefault(), "t>%d", duration);
        String transformed = String.format(Locale.getDefault(), "{\"type\":\">\",\"children\":[{\"type\":\"Identifier\",\"value\":\"t\"},{\"type\":\"NumericLiteral\",\"value\":%d}]}",duration);
        return ExpressionPair.create(origin,transformed);
    }

    /**设置动画相关配置*/
    public void setAnimationConfig(Config config) {
        if(config == null) {
            return;
        }
        this.mAnimationDuration = config.duration > 0 ? config.duration : this.mAnimationDuration;
        this.mEasing = !TextUtils.isEmpty(config.easing) ? config.easing : this.mEasing;
        this.mFlipInterval = config.flipInterval > 0 ? config.flipInterval : this.mFlipInterval;
    }

    public static class Config {
        int duration;
        String easing;
        int flipInterval;

        Config(int duration, String easing, int flipInterval) {
            this.duration = duration;
            this.easing = easing;
            this.flipInterval = flipInterval;
        }
    }

    public static class ConfigBuilder {
        private int duration; // 动画时长
        private String easing; // 动画插值器
        private int flipInterval; // 每张卡片展示时间

        public ConfigBuilder() {
        }

        public Config build() {
            return new Config(duration,easing, flipInterval);
        }

        public ConfigBuilder withEasingFunction(String easing) {
            this.easing = easing;
            return this;
        }

        public ConfigBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public ConfigBuilder withFlipInterval(int flipInterval) {
            this.flipInterval = flipInterval;
            return this;
        }
    }

}
