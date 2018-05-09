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

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.AbstractScrollEventHandler;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;

/**
 * Description:
 *
 * A built-in implementation of {@link com.alibaba.android.bindingx.core.IEventHandler} which handle
 * scroll events.
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXScrollHandler extends AbstractScrollEventHandler {

    private InnerScrollViewListener mScrollViewListener = null;

    public BindingXScrollHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        View targetView = mPlatformManager.getViewFinder().findViewBy(sourceRef);
        if(!(targetView instanceof ScrollView)) {
            return false;
        }
        ScrollView scrollView = (ScrollView) targetView;
        ViewTreeObserver observer = scrollView.getViewTreeObserver();
        if(observer != null && observer.isAlive()) {
            mScrollViewListener = new InnerScrollViewListener(scrollView);
            observer.addOnScrollChangedListener(mScrollViewListener);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        // nope
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        super.onDisable(sourceRef,eventType);

        View targetView = mPlatformManager.getViewFinder().findViewBy(sourceRef);
        if(targetView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) targetView;
            ViewTreeObserver observer = scrollView.getViewTreeObserver();
            if(observer != null && observer.isAlive() && mScrollViewListener != null) {
                observer.removeOnScrollChangedListener(mScrollViewListener);
                mScrollViewListener = null;
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScrollViewListener = null;
    }

    private class InnerScrollViewListener implements ViewTreeObserver.OnScrollChangedListener{

        private int mContentOffsetX=0;
        private int mContentOffsetY=0;

        private int mTx=0,mTy=0; // 拐点
        private int mLastDx=0,mLastDy=0;

        private ScrollView mHostView;
        InnerScrollViewListener(ScrollView hostView) {
            this.mHostView = hostView;
        }

        private boolean isSameDirection(int currentValue, int lastValue) {
            return (currentValue > 0 && lastValue > 0) || (currentValue < 0 && lastValue < 0);
        }

        @Override
        public void onScrollChanged() {
            int curContentOffsetX = mHostView.getScrollX();
            int curContentOffsetY = mHostView.getScrollY();

            if(curContentOffsetX == mContentOffsetX && curContentOffsetY == mContentOffsetY) {
                return;
            }

            final int dx = curContentOffsetX - mContentOffsetX;
            final int dy = curContentOffsetY - mContentOffsetY;

            mContentOffsetX = curContentOffsetX;
            mContentOffsetY = curContentOffsetY;

            if(dx == 0 && dy == 0) {
                return;
            }

            boolean isTurning = false;
            if(!isSameDirection(dy, mLastDy)) {// 发现拐点 只可能是纵向的
                mTy = mContentOffsetY;
                isTurning = true;
            }

            // 计算delta拐点
            final int tdx = mContentOffsetX - mTx;
            final int tdy = mContentOffsetY - mTy;

            mLastDx = dx;
            mLastDy = dy;

            if(isTurning) {
                // 通知
                BindingXScrollHandler.super.fireEventByState(BindingXConstants.STATE_TURNING,mContentOffsetX,mContentOffsetY,
                        dx,dy,tdx,tdy);
            }

            BindingXScrollHandler.super.handleScrollEvent(mContentOffsetX,mContentOffsetY,dx,dy,tdx,tdy);
        }
    }
}
