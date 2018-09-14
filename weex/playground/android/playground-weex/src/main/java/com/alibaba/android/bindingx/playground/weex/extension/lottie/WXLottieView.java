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
package com.alibaba.android.bindingx.playground.weex.extension.lottie;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.airbnb.lottie.LottieAnimationView;
import com.taobao.weex.ui.view.gesture.WXGesture;
import com.taobao.weex.ui.view.gesture.WXGestureObservable;

/**
 * Description:
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class WXLottieView extends LottieAnimationView implements WXGestureObservable {

    private WXGesture wxGesture;

    public WXLottieView(Context context) {
        super(context);
    }

    public WXLottieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WXLottieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void registerGestureListener(@Nullable WXGesture wxGesture) {
        this.wxGesture = wxGesture;
    }

    @Override
    public WXGesture getGestureListener() {
        return this.wxGesture;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (this.wxGesture != null) {
            result |= this.wxGesture.onTouch(this, event);
        }

        return result;
    }
}
