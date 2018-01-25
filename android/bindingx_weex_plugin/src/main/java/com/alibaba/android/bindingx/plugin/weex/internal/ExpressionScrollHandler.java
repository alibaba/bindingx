package com.alibaba.android.bindingx.plugin.weex.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.bindingx.plugin.weex.EventType;
import com.alibaba.android.bindingx.plugin.weex.ExpressionBindingCore;
import com.alibaba.android.bindingx.plugin.weex.ExpressionConstants;
import com.alibaba.android.bindingx.plugin.weex.LogProxy;
import com.alibaba.android.bindingx.plugin.weex.PlatformManager;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.bridge.WXBridgeManager;
import com.taobao.weex.common.Constants;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXScroller;
import com.taobao.weex.ui.component.list.WXListComponent;
import com.taobao.weex.ui.view.WXScrollView;
import com.taobao.weex.ui.view.listview.WXRecyclerView;
import com.taobao.weex.ui.view.refresh.wrapper.BounceRecyclerView;
import com.taobao.weex.utils.WXViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 *
 * 监听contentOffset
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class ExpressionScrollHandler extends AbstractEventHandler{

    private boolean isStart = false;

    private int mX,mY;

    private RecyclerView.OnScrollListener mOnScrollListener;
    private WXScrollView.WXScrollViewListener mWxScrollViewListener;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    private static HashMap<String/*list ref*/,ContentOffsetHolder/*offsetX,offsetY*/> sOffsetHolderMap =
            new HashMap<>();

    private String mSourceRef;

    public ExpressionScrollHandler(Context context, PlatformManager manager, Object... extension) {
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
                    mOnScrollListener = new InnerScrollListener(isVertical);
                    recyclerView.addOnScrollListener(mOnScrollListener);
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

    /**
     * @param contentOffsetX 横向绝对偏移(px)
     * @param contentOffsetY 纵向绝对偏移(px)
     * @param dx 相对上一次onScroll事件的横向的偏移
     * @param dy 相对上一次onScroll事件的纵向的偏移
     * @param tdx 距离最近一次"拐点"的横向偏移
     * @param tdy 距离最近一次"拐点"的纵向偏移
     * */
    private void handleScrollEvent(int contentOffsetX, int contentOffsetY, int dx, int dy,
                                   int tdx, int tdy) {
        LogProxy.d(String.format(Locale.CHINA,
                "[ExpressionScrollHandler] scroll changed. (contentOffsetX:%d,contentOffsetY:%d,dx:%d,dy:%d,tdx:%d,tdy:%d)",
                    contentOffsetX,contentOffsetY,dx,dy,tdx,tdy));

        this.mX = contentOffsetX;
        this.mY = contentOffsetY;

        if(!isStart) {
            isStart = true;
            fireEventByState(ExpressionConstants.STATE_START,contentOffsetX,contentOffsetY,dx,dy,tdx,tdy);
        }

        try {
            //消费所有的表达式
            JSMath.applyScrollValuesToScope(mScope, contentOffsetX, contentOffsetY, dx, dy, tdx, tdy);
            if(!evaluateExitExpression(mExitExpressionPair,mScope)) {
                consumeExpression(mExpressionHoldersMap, mScope, EventType.TYPE_SCROLL);
            }
        } catch (Exception e) {
            LogProxy.e("runtime error", e);
        }
    }


    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {
        //nope
    }

    @Override
    public void onBindExpression(@NonNull String eventType, @Nullable Map<String,Object> globalConfig, @Nullable ExpressionPair exitExpressionPair,
                                 @NonNull List<Map<String, Object>> expressionArgs, @Nullable ExpressionBindingCore.JavaScriptCallback callback) {
        super.onBindExpression(eventType,globalConfig, exitExpressionPair, expressionArgs, callback);
    }

    @Override
    public boolean onDisable(@NonNull String sourceRef, @NonNull String eventType) {
        clearExpressions();
        isStart = false;
        fireEventByState(ExpressionConstants.STATE_END, mX, mY,0,0,0,0);
        if(sOffsetHolderMap != null && !TextUtils.isEmpty(mSourceRef)) {
            ContentOffsetHolder h = sOffsetHolderMap.get(mSourceRef);
            if(h != null) {
                h.x = mX;
                h.y = mY;
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
            }
        } else if (sourceComponent instanceof WXListComponent) {
            WXListComponent list = (WXListComponent) sourceComponent;
            BounceRecyclerView hostView = list.getHostView();
            if (hostView != null) {
                WXRecyclerView recyclerView = hostView.getInnerView();
                if (recyclerView != null && mOnScrollListener != null) {
                    recyclerView.removeOnScrollListener(mOnScrollListener);
                    return true;
                }
            }
        }
        return false;
    }

    private void fireEventByState(@ExpressionConstants.State String state, float contentOffsetX, float contentOffsetY,
                                  float dx, float dy, float tdx, float tdy) {
        if (mCallback != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", state);
            double x = contentOffsetX * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            double y = contentOffsetY * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            param.put("x", x);
            param.put("y", y);

            double _dx = dx * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            double _dy = dy * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            param.put("dx", _dx);
            param.put("dy", _dy);

            double _tdx = tdx * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            double _tdy = tdy * WXEnvironment.sDefaultWidth / (double) WXViewUtils.getScreenWidth();
            param.put("tdx", _tdx);
            param.put("tdy", _tdy);

            mCallback.callback(param);
            LogProxy.d(">>>>>>>>>>>fire event:(" + state + "," + x + "," + y + ","+ _dx  +","+ _dy +"," + _tdx +"," + _tdy +")");
        }
    }


    @Override
    public void onDestroy() {
        mOnScrollListener = null;
        mWxScrollViewListener = null;
        mOnOffsetChangedListener = null;
        isStart = false;
        if(sOffsetHolderMap != null) {
            sOffsetHolderMap.clear();
        }
    }

    @Override
    protected void onExit(@NonNull Map<String, Object> scope) {
        float contentOffsetX = (float) scope.get("internal_x");
        float contentOffsetY = (float) scope.get("internal_y");
        fireEventByState(ExpressionConstants.STATE_EXIT, contentOffsetX, contentOffsetY,0,0,0,0);
    }

    private class InnerAppBarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {
        private int mContentOffsetY=0;

        private int mTy=0; // 拐点
        private int mLastDy=0;
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            verticalOffset = -verticalOffset;
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
                fireEventByState(ExpressionConstants.STATE_TURNING,0,mContentOffsetY,
                        0,dy,0,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    handleScrollEvent(0,mContentOffsetY,0,dy,0,tdy);
                }
            },mInstanceId);
        }
    }

    private class InnerScrollViewListener implements WXScrollView.WXScrollViewListener {
        private int mContentOffsetX=0;
        private int mContentOffsetY=0;

        private int mTx=0,mTy=0; // 拐点
        private int mLastDx=0,mLastDy=0;

        /**scroller 监听. 其中x和y是相对起始位置的偏移量*/
        @Override
        public void onScroll(WXScrollView wxScrollView, int x, int y) {

            final int dx = x - mContentOffsetX;
            final int dy = y - mContentOffsetY;

            mContentOffsetX = x;
            mContentOffsetY = y;

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
                fireEventByState(ExpressionConstants.STATE_TURNING,mContentOffsetX,mContentOffsetY,
                        dx,dy,tdx,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    handleScrollEvent(mContentOffsetX,mContentOffsetY,dx,dy,tdx,tdy);
                }
            },mInstanceId);
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
    }

    private class InnerScrollListener extends RecyclerView.OnScrollListener{
        private int mContentOffsetX=0;
        private int mContentOffsetY=0;

        private int mTx=0,mTy=0; // 拐点
        private int mLastDx=0,mLastDy=0;

        private boolean isVertical;

        InnerScrollListener(boolean isVertical){
            this.isVertical = isVertical;
            if(!TextUtils.isEmpty(mSourceRef) && sOffsetHolderMap != null) {
                ContentOffsetHolder holder = sOffsetHolderMap.get(mSourceRef);
                if(holder != null) {
                    mContentOffsetX = holder.x;
                    mContentOffsetY = holder.y;
                }
            }
        }

        /**list 监听. 其中dx和dy是相对上一次的偏移量*/
        @Override
        public void onScrolled(RecyclerView recyclerView, final int dx, final int dy) {
            // RecyclerView#computeVerticalScrollOffset 并不能准确计算contentOffset
            // 因此自行根据dx/dy计算。 但是这种方式如果注册listener时，recyclerView
            // 已经滚动了，则会丢失已滚动的距离。所以建议页面一打开就注册。

            mContentOffsetX += dx;
            mContentOffsetY += dy;

            boolean isTurning = false;
            if(!isSameDirection(dx,mLastDx) && !isVertical) {// 发现拐点 并且是横向list
                //更新拐点
                mTx = mContentOffsetX;
                isTurning = true;
            }

            if(!isSameDirection(dy, mLastDy) && isVertical) {// 发现拐点 并且是纵向的
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
                fireEventByState(ExpressionConstants.STATE_TURNING,mContentOffsetX,mContentOffsetY,
                        dx,dy,tdx,tdy);
            }

            WXBridgeManager.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    handleScrollEvent(mContentOffsetX,mContentOffsetY,dx,dy,tdx,tdy);
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
