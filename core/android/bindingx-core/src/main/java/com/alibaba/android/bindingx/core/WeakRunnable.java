package com.alibaba.android.bindingx.core;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public class WeakRunnable implements Runnable {
    private final WeakReference<Runnable> mDelegateRunnable;

    public WeakRunnable(@NonNull Runnable runnable) {
        mDelegateRunnable = new WeakReference<>(runnable);
    }

    @Override
    public void run() {
        Runnable runnable = mDelegateRunnable.get();
        if (runnable != null) {
            runnable.run();
        }
    }
}