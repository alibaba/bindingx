package com.alibaba.android.binding.plugin.weex;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Choreographer;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

abstract class AnimationFrame {

    static AnimationFrame newInstance() {
        AnimationFrame frame = null;
        if(Build.VERSION.SDK_INT >= 16) {
            frame = new ChoreographerAnimationFrameImpl();
        } else {
            frame = new HandlerAnimationFrameImpl();
        }
        return frame;
    }

    abstract void clear();

    abstract void terminate();

    abstract void requestAnimationFrame(@NonNull Callback callback);

    interface Callback {
        void doFrame();
    }

    @TargetApi(16)
    private static class ChoreographerAnimationFrameImpl extends AnimationFrame implements Choreographer.FrameCallback{

        private Choreographer choreographer;
        private Callback callback;
        private boolean isRunning;

        @TargetApi(16)
        ChoreographerAnimationFrameImpl() {
            choreographer = Choreographer.getInstance();
        }

        @Override
        void clear() {
            if(choreographer != null) {
                choreographer.removeFrameCallback(this);
            }
            this.isRunning = false;
        }

        @Override
        void terminate() {
            clear();
            choreographer = null;
        }

        @Override
        void requestAnimationFrame(@NonNull Callback callback) {
            this.callback = callback;
            this.isRunning = true;
            if(choreographer != null) {
                choreographer.postFrameCallback(this);
            }
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            if(callback != null) {
                callback.doFrame();
            }
            if(choreographer != null && isRunning) {
                choreographer.postFrameCallback(this);
            }
        }
    }

    private static class HandlerAnimationFrameImpl extends AnimationFrame implements Handler.Callback{

        private HandlerThread mInnerHandlerThread;
        private Handler mInnerHandler;

        private Callback callback;
        private boolean isRunning;

        private static final int MSG_FRAME_CALLBACK = 100;
        private static final long DEFAULT_DELAY_MILLIS = 16;

        HandlerAnimationFrameImpl() {
            if(mInnerHandlerThread != null) {
                terminate();
            }
            mInnerHandlerThread = new HandlerThread("expression-timing-thread");
            mInnerHandlerThread.start();
            mInnerHandler = new Handler(mInnerHandlerThread.getLooper(), this);
        }

        @Override
        void clear() {
            if(mInnerHandler != null) {
                mInnerHandler.removeCallbacksAndMessages(null);
            }
            isRunning = false;
        }

        @Override
        void terminate() {
            clear();
            if(Build.VERSION.SDK_INT >= 18) {
                mInnerHandlerThread.quitSafely();
            } else {
                mInnerHandlerThread.quit();
            }
            mInnerHandler = null;
            mInnerHandlerThread = null;
        }

        @Override
        void requestAnimationFrame(@NonNull Callback callback) {
            this.callback = callback;
            this.isRunning = true;
            if(mInnerHandler != null) {
                mInnerHandler.sendEmptyMessage(MSG_FRAME_CALLBACK);
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            if(msg != null && msg.what == MSG_FRAME_CALLBACK && mInnerHandler != null) {
                if(callback != null) {
                    callback.doFrame();
                }
                if(isRunning) {
                    mInnerHandler.sendEmptyMessageDelayed(MSG_FRAME_CALLBACK, DEFAULT_DELAY_MILLIS);
                }
                return true;
            }
            return false;
        }
    }

}
