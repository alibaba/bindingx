package com.alibaba.android.playground.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.plugin.android.NativeBindingX;

import java.util.Map;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class BindingXSliderView extends AbstractAnimatorView{
    private static final int OFFSET_ID = 10000;
    private NativeBindingX mNativeBinding;

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
        int prevId = OFFSET_ID + this.mWhichChild;
        int nextId = OFFSET_ID + index;
        Log.v(TAG, "switch from " + this.mWhichChild + " to " + index);

        switchToInternal(prevId, nextId);
    }

    private void switchToInternal(int prevId, int nextId) {
        mNativeBinding.unbindAll();
//        mNativeBinding.bind(getViewContainer(), createBindingXParams(prevId, nextId) , new NativeCallback() {
//            @Override
//            public void callback(Map<String, Object> params) {
//                // TODO
//            }
//        });
    }

    private Map<String, Object> createBindingXParams(int prevId, int nextId) {
        // TODO
        return null;
    }

}
