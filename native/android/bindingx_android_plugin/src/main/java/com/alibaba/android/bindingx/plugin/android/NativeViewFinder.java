package com.alibaba.android.bindingx.plugin.android;

import android.view.View;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public interface NativeViewFinder {
    View findViewBy(View rootView, String ref);
}
