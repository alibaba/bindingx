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

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.Constants;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * Lottie weex组件。
 *
 * Created by rowandjj(chuyi)<br/>
 */
public class WXLottieComponent extends WXComponent<WXLottieView> {

    private static final String TAG = "wx-lottie";

    private static final int CODE_PLAY = 0x01;

    private static final String ATTR_SPEED = "speed";
    private static final String ATTR_LOOP = "loop";
    private static final String ATTR_RESIZE_MODE = "resize";
    private static final String ATTR_AUTO_PLAY = "autoplay";
    private static final String ATTR_SOURCE_JSON = "sourcejson";
    private static final String ATTR_PROGRESS = "progress";

    private boolean mAutoPlay = true;
    private byte[] mJsonBytes = null;

    private boolean isDestroyed;

    private LottieAnimatorListener mAnimatorListener;

    private static final String STATE_START = "start";
    private static final String STATE_END = "complete";
    private static final String STATE_CANCEL = "cancel";
    private static final String STATE_REPEAT = "repeat";

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_PLAY:
                    setAndPlayAnimation();
                    break;
            }
        }
    };

    public WXLottieComponent(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    public WXLottieComponent(WXSDKInstance instance, WXVContainer parent, int type, BasicComponentData basicComponentData) {
        super(instance, parent, type, basicComponentData);
    }

    @Override
    protected WXLottieView initComponentHostView(@NonNull Context context) {
        WXLottieView lottie = new WXLottieView(context);
        mAnimatorListener = new LottieAnimatorListener();
        lottie.addAnimatorListener(mAnimatorListener);
        return lottie;
    }

    class LottieAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            fireEvent(STATE_START, Collections.<String, Object>emptyMap());
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            fireEvent(STATE_END, Collections.<String, Object>emptyMap());
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            fireEvent(STATE_CANCEL, Collections.<String, Object>emptyMap());
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            fireEvent(STATE_REPEAT, Collections.<String, Object>emptyMap());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if(mAnimatorListener != null && getHostView() != null) {
            getHostView().removeAnimatorListener(mAnimatorListener);
        }
        mAnimatorListener = null;
    }

    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case Constants.Name.SRC:
                setSourceURI(WXUtils.getString(param, null));
                break;
            case ATTR_SOURCE_JSON:
                setSourceJSON(WXUtils.getString(param, null));
                break;
            case ATTR_LOOP:
                setLoop(WXUtils.getBoolean(param,false));
                break;
            case ATTR_SPEED:
                setSpeed(WXUtils.getFloat(param,1f));
                break;
            case ATTR_RESIZE_MODE:
                setResizeMode(WXUtils.getString(param, null));
                break;
            case ATTR_AUTO_PLAY:
                setAutoPlay(WXUtils.getBoolean(param, true));
                break;
            case ATTR_PROGRESS:
                setProgressValue(WXUtils.getFloat(param, 0f));
                break;
        }

        return super.setProperty(key, param);
    }

    @WXComponentProp(name = ATTR_SOURCE_JSON)
    public void setSourceJSON(String sourceJson) {
        if(TextUtils.isEmpty(sourceJson)) {
            return;
        }

        try {
            JSONObject object = new JSONObject(sourceJson);
            if (getHostView() != null) {
                getHostView().setAnimation(object);
                if(mAutoPlay) {
                    getHostView().playAnimation();
                }
            }
        }catch (Throwable e) {
            WXLogUtils.e(TAG, "play failed. "+ e.getMessage());
        }
    }

    @WXComponentProp(name = Constants.Name.SRC)
    public void setSourceURI(String uriString) {
        if(TextUtils.isEmpty(uriString)) {
            return;
        }
        uriString = uriString.trim();
        IWXHttpAdapter adapter = WXSDKEngine.getIWXHttpAdapter();
        Uri uri = null;
        try {
            uri = Uri.parse(uriString);
        }catch (Throwable e) {
            WXLogUtils.e(TAG,"uri not valid. \n" + e.getMessage());
        }
        if(uri == null || adapter == null) {
            return;
        }

        if (Constants.Scheme.LOCAL.equals(uri.getScheme()) || Constants.Scheme.FILE.equals(uri.getScheme())) {
            loadSourceFromLocal(uri);
        } else if (Constants.Scheme.HTTP.equals(uri.getScheme()) || Constants.Scheme.HTTPS.equals(uri.getScheme())){
            WXRequest request = new WXRequest();
            request.url = uriString;
            request.method = "GET";
            adapter.sendRequest(request, new WXLottieDownloadHttpListener());
        }
    }

    private void loadSourceFromLocal(@NonNull Uri uri) {
        if (uri.getScheme().equals(Constants.Scheme.LOCAL)) {
            try {
                InputStream is = getInstance().getContext().getAssets().open(uri.getPath().substring(1));
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                this.mJsonBytes = buffer;
                onLottieJsonUpdated();
            } catch (IOException e) {
                WXLogUtils.d(TAG, e.toString());
            }
        } else if (uri.getScheme().equals(Constants.Scheme.FILE)) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                try {
                    FileInputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + uri.getPath());
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    this.mJsonBytes = buffer;
                    inputStream.close();
                    onLottieJsonUpdated();
                } catch (Throwable e) {
                    WXLogUtils.e(TAG, e.getMessage());
                }

            }
        }
    }

    private void onLottieJsonUpdated() {
        if(isDestroyed) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        Message msg = mHandler.obtainMessage();
        msg.what = CODE_PLAY;
        mHandler.sendMessage(msg);
    }

    @WXComponentProp(name = ATTR_LOOP)
    public void setLoop(boolean loop) {
        if (getHostView() != null) {
            getHostView().loop(loop);
        }
    }

    @WXComponentProp(name = ATTR_SPEED)
    public void setSpeed(float speed) {
        if (getHostView() != null) {
            getHostView().setSpeed(speed);
        }
    }

    @WXComponentProp(name = ATTR_RESIZE_MODE)
    public void setResizeMode(String resizeMode) {
        if(getHostView() == null || TextUtils.isEmpty(resizeMode)) {
            return;
        }
        if ("cover".equals(resizeMode)) {
            getHostView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if ("contain".equals(resizeMode)) {
            getHostView().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else if ("center".equals(resizeMode)) {
            getHostView().setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    @WXComponentProp(name = ATTR_AUTO_PLAY)
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if(!mAutoPlay) {
            reset();
        } else {
            play();
        }
    }

    @WXComponentProp(name = ATTR_PROGRESS)
    public void setProgressValue(float progressValue) {
        setProgress(progressValue);
    }

    // -------  JS Methods ---------

    @JSMethod
    public void play() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getHostView() != null && ViewCompat.isAttachedToWindow(getHostView())) {
                    getHostView().cancelAnimation();
                    getHostView().setProgress(0);
                    getHostView().playAnimation();
                } else {
                    WXLogUtils.e(TAG,"can not play! maybe view is not attached to window");
                }
            }
        });
    }

    @JSMethod
    public void pause() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getHostView() != null && ViewCompat.isAttachedToWindow(getHostView())) {
                    getHostView().pauseAnimation();
                } else {
                    WXLogUtils.e(TAG,"can not pause! maybe view is not attached to window");
                }
            }
        });
    }

    @JSMethod
    public void reset() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getHostView() != null && ViewCompat.isAttachedToWindow(getHostView())) {
                    getHostView().cancelAnimation();
                    getHostView().setProgress(0);
                } else {
                    WXLogUtils.e(TAG,"can not reset! maybe view is not attached to window");
                }
            }
        });
    }

    @JSMethod
    public void setProgress(final float progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                float clampProgress = Math.max(0f,Math.min(1f,progress));
                if (getHostView() != null && ViewCompat.isAttachedToWindow(getHostView())) {
                    getHostView().setProgress(clampProgress);
                } else {
                    WXLogUtils.e(TAG,"can not set propress! maybe view is not attached to window");
                }
            }
        });
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        isDestroyed = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setAndPlayAnimation() {
        if (mJsonBytes != null) {
            try {
                JSONObject object = new JSONObject(new String(mJsonBytes));
                if (getHostView() != null) {
                    getHostView().setAnimation(object);
                    if(mAutoPlay) {
                        getHostView().playAnimation();
                    }
                }
                mJsonBytes = null;
            } catch (JSONException e) {
                WXLogUtils.e(TAG, "play failed" + e.toString());
            }
        }
    }


    class WXLottieDownloadHttpListener implements IWXHttpAdapter.OnHttpListener {

        @Override
        public void onHttpStart() {

        }

        @Override
        public void onHeadersReceived(int statusCode, Map<String, List<String>> headers) {

        }

        @Override
        public void onHttpUploadProgress(int uploadProgress) {

        }

        @Override
        public void onHttpResponseProgress(int loadedLength) {

        }

        @Override
        public void onHttpFinish(WXResponse response) {
            if (response != null) {
                if (response.errorCode != null && response.errorCode.equals("-1")) {
                    WXLogUtils.e(TAG, "get json failed" + response.errorMsg);
                    return;
                }

                int errorCode = 200;
                if (response.errorCode != null) {
                    errorCode = Integer.getInteger(response.errorCode);
                }
                if (errorCode >= 200 && errorCode < 300) {
                    mJsonBytes = response.originalData;
                    onLottieJsonUpdated();
                }
            }
        }
    }

}













