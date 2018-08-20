package com.alibaba.android.playground.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public abstract class AbstractAnimatorView extends LinearLayout{

    protected static final String TAG = "Slider";

    private static final int DEFAULT_INTERVAL = 1500;//ms

    protected boolean isVertical = true;
    protected int mWhichChild = 0;
    protected int mFlipInterval = DEFAULT_INTERVAL;
    protected boolean mAutoStart = false;
    protected boolean mRunning = false;
    protected boolean mStarted = false;
    protected boolean mVisible = false;

    public AbstractAnimatorView(Context context) {
        super(context);
        init();
    }

    public AbstractAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(isVertical ? VERTICAL : HORIZONTAL);
    }

    public void startFlipping() {
        mStarted = true;
        updateRunning();
    }

    public void stopFlipping() {
        mStarted = false;
        updateRunning();
    }

    public void setFlipInterval(int milliseconds) {
        mFlipInterval = milliseconds;
    }

    protected abstract void switchTo(int index);

    public void setDisplayedChild(int whichChild) {
        switchTo(whichChild);
        // 更新位置
        this.mWhichChild = whichChild;
        // 控制下边界
        if (whichChild >= getChildCount()) {
            mWhichChild = 0;
        } else if (whichChild < 0) {
            mWhichChild = getChildCount() - 1;
        }
        boolean hasFocus = getFocusedChild() != null;
        if (hasFocus) {
            requestFocus(FOCUS_FORWARD);
        }
    }

    public void showNext() {
        int count = getChildCount();
        setDisplayedChild((mWhichChild + 1) % count);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        // TODO
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mWhichChild = 0;
    }

    @Override
    public void removeView(View view) {
        final int index = indexOfChild(view);
        if (index >= 0) {
            removeViewAt(index);
        }
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        final int childCount = getChildCount();
        // TODO
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoStart) {
            startFlipping();
        }
        Log.e("CHUYI","child count " + getChildCount());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
//                showOnly(mWhichChild, flipNow);
                postDelayed(mFlipRunnable, mFlipInterval);
            } else {
                removeCallbacks(mFlipRunnable);
            }
            mRunning = running;
        }
    }

    private final Runnable mFlipRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRunning) {
                showNext();
                postDelayed(mFlipRunnable, mFlipInterval);
            }
        }
    };
}
