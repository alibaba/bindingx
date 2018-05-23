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
package com.alibaba.android.bindingx.plugin.weex;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.core.BindingXCore;
import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.AbstractScrollEventHandler;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.alibaba.android.bindingx.core.internal.ExpressionPair;
import com.taobao.weex.bridge.WXBridgeManager;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXScroller;
import com.taobao.weex.ui.component.list.WXListComponent;
import com.taobao.weex.ui.view.WXHorizontalScrollView;
import com.taobao.weex.ui.view.WXScrollView;
import com.taobao.weex.ui.view.listview.WXRecyclerView;
import com.taobao.weex.ui.view.refresh.wrapper.BounceRecyclerView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * scroll event handler implementation for weex.
 *
 * support components:
 *
 * 1. List: http://weex-project.io/cn/references/components/list.html
 * 2. Scroller: http://weex-project.io/cn/references/components/scroller.html
 * 3. AppBarLayout(android): NA
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXScrollHandler extends AbstractScrollEventHandler {

    private RecyclerView.OnScrollListener mListOnScrollListener;
    private WXScrollView.WXScrollViewListener mWxScrollViewListener;
    private WXHorizontalScrollView.ScrollViewListener mHorizontalViewScrollListener;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    private static HashMap<String/*list ref*/,ContentOffsetHolder/*offsetX,offsetY*/> sOffsetHolderMap =
            new HashMap<>();

    private String mSourceRef;

    public BindingXScrollHandler(Context context, PlatformManager manager, Object... extension) {
        super(context,manager,extension);
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
        WXComponent sourceComponent = WXModuleUtils.findComponentByRef(instanceId, sourceRef);
        if (sourceComponent == null) {
            LogProxy.e("[ExpressionScrollHandler]source component not found.");
            return false;
        }
        this.mSourceRef = sourceRef;
        if (sourceComponent instanceof WXScroller) {
            WXScroller scroller = (WXScroller) sourceComponent;
            View innerView = scroller.getInnerView();
            if (innerView != null && innerView instanceof WXScrollView) {
                mWxScrollViewListener = new InnerScrollViewListener();
                ((WXScrollView) innerView).addScrollViewListener(mWxScrollViewListener);
                return true;
            } else if(innerView != null && innerView instanceof WXHorizontalScrollView) {
                mHorizontalViewScrollListener = new InnerScrollViewListener();
                ((WXHorizontalScrollView) innerView).addScrollViewListener(mHorizontalViewScrollListener);
                return true;
            }

        } else if (sourceComponent instanceof WXListComponent) {
            WXListComponent list = (WXListComponent) sourceComponent;
            BounceRecyclerView hostView = list.getHostView();
            if (hostView != null) {
                WXRecyclerView recyclerView = hostView.getInnerView();
                boolean isVertical = list.getOrientation() == Constants.Orientation.VERTICAL;
                if (recyclerView != null) {
                    if(sOffsetHolderMap != null) {
                        ContentOffsetHolder holder = sOffsetHolderMap.get(sourceRef);
                        if(holder == null) {
                            sOffsetHolderMap.put(sourceRef,new ContentOffsetHolder(0,0));
                        }
                    }
                    mListOnScrollListener = new InnerListScrollListener(isVertical, new WeakReference<WXListComponent>(list));
                    recyclerView.addOnScrollListener(mListOnScrollListener);
                    return true;
                }
            }
        } else if(sourceComponent.getHostView()!= null && sourceComponent.getHostView() instanceof AppBarLayout) {
            AppBarLayout appBarLayout = (AppBarLayout) sourceComponent.getHostView();
            mOnOffsetChangedListener = new InnerAppBarOffsetChangedListener();
            appBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
            return true;
        }
        return false;
    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        //nope
    }

    @Override
    public void onBindExpression(@NonNull String eventType,
                                 @Nullable Map<String,Object> globalConfig,
                                 @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs,
                                 @Nullable BindingXCore.JavaScriptCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        super.onDisable(sourceRef,eventType);
        if(sOffsetHolderMap != null && !TextUtils.isEmpty(mSourceRef)) {
            ContentOffsetHolder h = sOffsetHolderMap.get(mSourceRef);
            if(h != null) {
                h.x = mContentOffsetX;
                h.y = mContentOffsetY;
            }
        }

        String instanceId = TextUtils.isEmpty(mAnchorInstanceId) ? mInstanceId : mAnchorInstanceId;
        WXComponent sourceComponent = WXModuleUtils.findComponentByRef(instanceId, sourceRef);
        if (sourceComponent == null) {
            LogProxy.e("[ExpressionScrollHandler]source component not found.");
            return false;
        }
        if (sourceComponent instanceof WXScroller) {
            WXScroller scroller = (WXScroller) sourceComponent;
            View innerView = scroller.getInnerView();
            if (innerView != null && innerView instanceof WXScrollView && mWxScrollViewListener != null) {
                ((WXScrollView) innerView).removeScrollViewListener(mWxScrollViewListener);
                return true;
            } else if(innerView != null && innerView instanceof WXHorizontalScrollView && mHorizontalViewScrollListener != null) {
                ((WXHorizontalScrollView) innerView).removeScrollViewListener(mHorizontalViewScrollListener);
                return true;
            }
        } else if (sourceComponent instanceof WXListComponent) {
            WXListComponent list = (WXListComponent) sourceComponent;
            BounceRecyclerView hostView = list.getHostView();
            if (hostView != null) {
                WXRecyclerView recyclerView = hostView.getInnerView();
                if (recyclerView != null && mListOnScrollListener != null) {
                    recyclerView.removeOnScrollListener(mListOnScrollListener);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mListOnScrollListener = null;
        mWxScrollViewListener = null;
        mOnOffsetChangedListener = null;
        if(sOffsetHolderMap != null) {
            sOffsetHolderMap.clear();
        }
    }


    private class InnerAppBarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {
        private int mContentOffsetY=0;

        private int mTy=0;
        private int mLastDy=0;
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            verticalOffset = -verticalOffset;// normalize.
            final int dy = verticalOffset - mContentOffsetY;
            mContentOffsetY = verticalOffset;
            if(dy == 0) {
                return;
            }

            boolean isTurning = false;
            if(!isSameDirection(dy, mLastDy)) {
                mTy = mContentOffsetY;
                isTurning = true;
            }

            final int tdy = mContentOffsetY - mTy;
            mLastDy = dy;
            if(isTurning) {
                BindingXScrollHandler.super.fireEventByState(BindingXConstants.STATE_TURNING,0,mContentOffsetY,
                        0,dy,0,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    BindingXScrollHandler.super.handleScrollEvent(0,mContentOffsetY,0,dy,0,tdy);
                }
            },mInstanceId);
        }
    }

    private class InnerScrollViewListener implements WXScrollView.WXScrollViewListener,WXHorizontalScrollView.ScrollViewListener {
        private int mContentOffsetX=0;
        private int mContentOffsetY=0;

        private int mTx=0,mTy=0;
        private int mLastDx=0,mLastDy=0;

        @Override
        public void onScroll(WXScrollView wxScrollView, int x, int y) {
            onScrollInternal(x, y);
        }

        @Override
        public void onScrollChanged(WXHorizontalScrollView wxHorizontalScrollView, int x, int y, int oldX, int oldY) {
            onScrollInternal(x, y);
        }

        @Override
        public void onScrollChanged(WXScrollView wxScrollView, int i, int i1, int i2, int i3) {

        }

        @Override
        public void onScrollToBottom(WXScrollView wxScrollView, int i, int i1) {

        }

        @Override
        public void onScrollStopped(WXScrollView wxScrollView, int i, int i1) {

        }

        private void onScrollInternal(int x, int y) {
            final int dx = x - mContentOffsetX;
            final int dy = y - mContentOffsetY;

            mContentOffsetX = x;
            mContentOffsetY = y;

            if(dx == 0 && dy == 0) {
                return;
            }

            boolean isTurning = false;
            if(!isSameDirection(dy, mLastDy)) {
                mTy = mContentOffsetY;
                isTurning = true;
            }

            final int tdx = mContentOffsetX - mTx;
            final int tdy = mContentOffsetY - mTy;

            mLastDx = dx;
            mLastDy = dy;

            if(isTurning) {
                BindingXScrollHandler.super.fireEventByState(BindingXConstants.STATE_TURNING,mContentOffsetX,mContentOffsetY,
                        dx,dy,tdx,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    BindingXScrollHandler.super.handleScrollEvent(mContentOffsetX,mContentOffsetY,dx,dy,tdx,tdy);
                }
            },mInstanceId);
        }

    }

    private class InnerListScrollListener extends RecyclerView.OnScrollListener{
        private int mContentOffsetX=0;
        private int mContentOffsetY=0;

        private int mTx=0,mTy=0;
        private int mLastDx=0,mLastDy=0;

        private boolean isVertical;
        private WeakReference<WXListComponent> mComponentRef;

        InnerListScrollListener(boolean isVertical, WeakReference<WXListComponent> componentRef){
            this.isVertical = isVertical;
            this.mComponentRef = componentRef;
            if(!TextUtils.isEmpty(mSourceRef) && sOffsetHolderMap != null) {
                ContentOffsetHolder holder = sOffsetHolderMap.get(mSourceRef);
                if(holder != null) {
                    mContentOffsetX = holder.x;
                    mContentOffsetY = holder.y;
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, final int dx, final int dy) {
            // RecyclerView#computeVerticalScrollOffset not calculate contentOffset accurately
            // so we calculate that ourselves. But if recyclerView already scrolls before we register
            // the listener, then we'll missing that scrolled distance.
            // so you should bind it as early as possible

            if(ViewCompat.isInLayout(recyclerView) && mComponentRef != null && mComponentRef.get() != null) {
                mContentOffsetY = Math.abs(mComponentRef.get().calcContentOffset(recyclerView));
            } else {
                mContentOffsetY += dy;
            }
            mContentOffsetX += dx;

            boolean isTurning = false;
            if(!isSameDirection(dx,mLastDx) && !isVertical) {
                mTx = mContentOffsetX;
                isTurning = true;
            }

            if(!isSameDirection(dy, mLastDy) && isVertical) {
                mTy = mContentOffsetY;
                isTurning = true;
            }

            final int tdx = mContentOffsetX - mTx;
            final int tdy = mContentOffsetY - mTy;

            mLastDx = dx;
            mLastDy = dy;

            if(isTurning) {
                fireEventByState(BindingXConstants.STATE_TURNING,mContentOffsetX,mContentOffsetY,
                        dx,dy,tdx,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    BindingXScrollHandler.super.handleScrollEvent(mContentOffsetX,mContentOffsetY,dx,dy,tdx,tdy);
                }
            },mInstanceId);
        }
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }


    private boolean isSameDirection(int currentValue, int lastValue) {
        return (currentValue > 0 && lastValue > 0) || (currentValue < 0 && lastValue < 0);
    }


    private static class ContentOffsetHolder {
        int x,y;

        ContentOffsetHolder(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
