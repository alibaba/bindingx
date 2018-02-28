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

import com.google.zxing.Result;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.qrcode.extension.ZXingScannerView;
import com.alibaba.android.qrcode.extension.core.HistoryManager;
import com.taobao.weex.utils.WXLogUtils;

public class QRCodeCaptureActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private HistoryManager mHistoryManager;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode_capture);
        mScannerView = (ZXingScannerView) findViewById(R.id.zxingview);

        findViewById(R.id.history_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),HistoryActivity.class));
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHistoryManager = new HistoryManager(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void handleResult(Result result) {
        if(result == null || TextUtils.isEmpty(result.getText())) {
            return;
        }
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        vibrate();
        handleScanResult(result.getText());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mScannerView != null) {
                    mScannerView.resumeCameraPreview(QRCodeCaptureActivity.this);
                }
            }
        }, 800);
    }


    private void handleScanResult(@NonNull String code) {
        try {
            if(mHistoryManager != null) {
                mHistoryManager.setItem(code);
            }
            Intent intent = new Intent(QRCodeCaptureActivity.this, WXActivity.class);
            intent.putExtra(WXActivity.WEEX_URL, code);
            intent.setData(Uri.parse(code));
            startActivity(intent);
        }catch (Throwable e) {
            WXLogUtils.e("QRCodeCaptureActivity", "unexpected error: " + e.getMessage());
        }

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if(vibrator != null) {
            vibrator.vibrate(200);
        }
    }
}
