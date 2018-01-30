package com.alibaba.android.bindingx.plugin.react;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.android.bindingx.core.PlatformManager;
import com.alibaba.android.bindingx.core.internal.AbstractScrollEventHandler;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */

public class BindingXScrollHandler extends AbstractScrollEventHandler {

    public BindingXScrollHandler(Context context, PlatformManager manager, Object... extension) {
        super(context, manager, extension);
    }

    @Override
    public boolean onCreate(@NonNull String sourceRef, @NonNull String eventType) {
        return false;
    }

    @Override
    public void onStart(@NonNull String sourceRef, @NonNull String eventType) {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}
