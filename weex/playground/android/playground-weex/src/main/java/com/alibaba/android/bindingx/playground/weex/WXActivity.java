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
package com.alibaba.android.bindingx.playground.weex;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.taobao.weex.utils.WXSoInstallMgrSdk;

public class WXActivity extends AbstractWeexActivity{

    private String mUrl;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public static String WEEX_URL = "weexUrl";

    private static final String NAV_HIDDEN = "wx_navbar_hidden";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        setActionBarTitle("bindingx weex playground");

        setContainer((ViewGroup) findViewById(R.id.container));

        if(!WXSoInstallMgrSdk.isCPUSupport()) {
            Toast.makeText(this,"device not support weex",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(getIntent() != null) {
            mUrl = getIntent().getStringExtra(WEEX_URL);
        }
        hideAppBarIfNeeded(mUrl);
    }

    private void hideAppBarIfNeeded(@Nullable String url) {
        if(TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            String result = uri.getQueryParameter(NAV_HIDDEN);
            ActionBar actionbar = getSupportActionBar();
            if (actionbar != null && TextUtils.equals(result, Boolean.toString(true))) {
                actionbar.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(mUrl)) {
                    //https://jsplayground.taobao.com/raxplayground/57f81e4e-8242-4124-bb90-996a57331343
                    renderPageByURL("https://jsplayground.taobao.com/bundle/57f81e4e-8242-4124-bb90-996a57331343/raxbundle.js?wh_weex=true&wh_ttid=native&_wx_tpl=https://jsplayground.taobao.com/bundle/57f81e4e-8242-4124-bb90-996a57331343/raxbundle.js");
                } else {
                    renderPageByURL(mUrl);
                }
            }
        },500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    public static void start(Context context, String url) {
        if(context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context,WXActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WEEX_URL, url);
        context.startActivity(intent);
    }
}
