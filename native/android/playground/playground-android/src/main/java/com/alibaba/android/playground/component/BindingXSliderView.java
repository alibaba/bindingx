package com.alibaba.android.playground.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.android.bindingx.core.BindingXEventType;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.core.internal.ExpressionPair;
import com.alibaba.android.bindingx.plugin.android.NativeBindingX;
import com.alibaba.android.bindingx.plugin.android.NativeCallback;
import com.alibaba.android.bindingx.plugin.android.model.BindingXPropSpec;
import com.alibaba.android.bindingx.plugin.android.model.BindingXSpec;
import com.alibaba.android.playground.utils.Utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXSliderView extends AbstractAnimatorView{
    private static final int OFFSET_ID = 10000;// TODO 随机生成
    private NativeBindingX mNativeBinding;

    private String mEasing = "linear";
    List<BindingTransitionSpec> mTransitionSpecArray;

    private int mContainerId = View.NO_ID;

    public BindingXSliderView(Context context) {
        super(context);
        init();
    }

    public BindingXSliderView(Context context, boolean isVertical) {
        super(context, isVertical);
        init();
    }

    public BindingXSliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BindingXSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mNativeBinding = NativeBindingX.create();
        LogProxy.sEnableLog = true;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        this.addView(child,index,params,true);
    }

    private void addView(View child, int index, ViewGroup.LayoutParams params, boolean wrap) {
        if(wrap) {
            super.addView(wrapChild(child),index,params);
        } else {
            super.addView(child, index, params);
        }
    }

    private void addView(View child, boolean wrap) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }

        if(wrap) {
            this.addView(child, -1, params);
        } else {
            this.addView(child, -1, params, false);
        }
    }

    public void setEasingFunction(String easing) {
        this.mEasing = TextUtils.isEmpty(easing) ? this.mEasing : easing;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNativeBinding = null;
    }

    private View wrapChild(@NonNull View child) {
        int count = getChildCount();

        final FrameLayout wrapper = new FrameLayout(this.getContext());
        wrapper.addView(child);
        wrapper.setId(OFFSET_ID + count);

        return wrapper;
    }

    @Override
    protected void switchTo(int index) {
        /* 这是真实数据源中的位置 */
        final int cur = this.mWhichChild;
        final int next = index;

        // 使用BindingX实现滑动效果
        // 当然，这里也可以直接使用android 原生的 Scroller来实现滑动效果
        // BindingX的优势是可以通过不同配置实现不同的交互效果，灵活性高
        mNativeBinding.unbindAll();
        mNativeBinding.bind(this, createBindingXParams(cur, next) , new NativeCallback() {
            @Override
            public void callback(Map<String, Object> params) {
                String state = (String) params.get("state");
                switch (state) {
                    case BindingXConstants.STATE_START:// 动画开始
                        beforeSwitch(cur, next);
                        break;
                    case BindingXConstants.STATE_EXIT:// 动画结束
                        afterSwitch(cur, next);
                        break;
                }
            }
        });
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void beforeSwitch(int cur, int next) {
        // nope
        Log.v(TAG, "slider will slide from " + cur + " to " + next);
    }

    private void afterSwitch(int cur, int next) {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                setScrollOffset(0);
                View viewToMove = getChildAt(0);
                removeView(viewToMove);
                addView(viewToMove,false);

            }
        },0);
    }

    private BindingXSpec createBindingXParams(int from, int to) {
        BindingXSpec spec = new BindingXSpec();

        spec.eventType = BindingXEventType.TYPE_TIMING;
        spec.exitExpression = createExitExpression(mAnimationDuration);
        spec.expressionProps = new LinkedList<>();

        String easing = mEasing;
        int end = computeAnimEndValue();
        int duration = mAnimationDuration;

        if(this.getId() == NO_ID) {
            mContainerId = Utils.generateViewId();
            this.setId(mContainerId);
        }
        int id = getId();
        spec.expressionProps.add(createFlipAnimationProps(id, isVertical, easing, getScrollOffset() , end, duration));
        for(BindingTransitionSpec transitionSpec : mTransitionSpecArray) {
            spec.expressionProps.addAll(resolveBindingTransitionSpec(transitionSpec,from, to));
        }
        return spec;
    }

    private List<BindingXPropSpec> resolveBindingTransitionSpec(@NonNull BindingTransitionSpec transitionSpec, int from, int to) {

//        List<BindingXPropSpec> list = new ArrayList<>(2);
//
//        String elementFrom = String.valueOf(OFFSET_ID + from);
//        String elementTo = String.valueOf(OFFSET_ID + to);

        // TODO


        return Collections.emptyList();
    }

    private int computeAnimEndValue() {
        int pageSize = getPageSize();
        Log.v(TAG,"pageSize is " + pageSize);
        return pageSize;
    }

    private static BindingXPropSpec createFlipAnimationProps(int rootElementId, boolean isVertical, String easing, int begin, int end, int duration) {
        easing = easing == null ? "linear" : easing;
        BindingXPropSpec spec = new BindingXPropSpec();
        spec.element = String.valueOf(rootElementId);
        spec.property = isVertical ? "scroll.contentOffsetY" : "scroll.contentOffsetX";
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
        this.mTransitionSpecArray = config.transitionSpecArray != null ? config.transitionSpecArray : Collections.<BindingTransitionSpec>emptyList();
    }

    public static class Config {
        int duration;
        String easing;
        int flipInterval;
        List<BindingTransitionSpec> transitionSpecArray;

        Config(int duration, String easing, int flipInterval, List<BindingTransitionSpec> transitionSpecArray) {
            this.duration = duration;
            this.easing = easing;
            this.flipInterval = flipInterval;
            this.transitionSpecArray = transitionSpecArray;
        }
    }

    public static class ConfigBuilder {
        private int duration; // 动画时长
        private String easing; // 动画插值器
        private int flipInterval; // 每张卡片展示时间
        private LinkedList<BindingTransitionSpec> mBindingPropsArray; // 扩展动画配置

        public ConfigBuilder() {
            mBindingPropsArray = new LinkedList<>();
        }

        public Config build() {
            return new Config(duration,easing, flipInterval,mBindingPropsArray);
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

        public ConfigBuilder withBindingXTransition(String property, Pair<Float,Float> inputRange, Pair<Object,Object> outputRange) {
            this.mBindingPropsArray.add(new BindingTransitionSpec(property, inputRange, outputRange));
            return this;
        }
    }

    static class BindingTransitionSpec {
        String property;
        Pair<Float,Float> inputRange;
        Pair<Object,Object> outputRange;

        BindingTransitionSpec(String property, Pair<Float,Float> inputRange, Pair<Object,Object> outputRange) {
            this.property = property;
            this.inputRange = inputRange;
            this.outputRange = outputRange;
        }
    }

}
