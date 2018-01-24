package com.alibaba.android.binding.playground;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.taobao.weex.utils.WXSoInstallMgrSdk;

public class MainActivity extends AbstractWeexActivity {

    private String mUrl;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContainer((ViewGroup) findViewById(R.id.container));

        if(!WXSoInstallMgrSdk.isCPUSupport()) {
            Toast.makeText(this,"device not support weex",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(getIntent() != null) {
            mUrl = getIntent().getStringExtra("url");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(mUrl)) {
                    //http://dotwe.org/vue/e7d22905f0b1e5db6bc7fdbd9b3c7a16
                    renderPageByURL("http://dotwe.org/raw/dist/e7d22905f0b1e5db6bc7fdbd9b3c7a16.bundle.wx");
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
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
