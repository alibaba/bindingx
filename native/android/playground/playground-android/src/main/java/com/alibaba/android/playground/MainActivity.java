package com.alibaba.android.playground;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.bindingx.core.LogProxy;
import com.alibaba.android.bindingx.plugin.android.NativeBindingX;
import com.alibaba.android.bindingx.plugin.android.NativeCallback;
import com.alibaba.fastjson.JSON;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NativeBindingX mNativeBindingX = null;
    private View mRootView;

    private String timingConfig = "{\n" +
            "   \"eventType\":\"timing\",\n" +
            "   \"exitExpression\":{\n" +
            "      \"origin\":\"t>2000\",\n" +
            "      \"transformed\":\"{\\\"type\\\":\\\">\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"t\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}\"\n" +
            "   },\n" +
            "   \"props\":[\n" +
            "     {\n" +
            "       \"element\":\"target\",\n" +
            "       \"property\":\"transform.translateX\",\n" +
            "       \"expression\":{\n" +
            "         \"origin\":\"linear(t,0,400,2000)\",\n" +
            "         \"transformed\":\"{\\\"type\\\":\\\"CallExpression\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"linear\\\"},{\\\"type\\\":\\\"Arguments\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"t\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":0},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":400},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}]}\"\n" +
            "       }\n" +
            "     }\n" +
            "     ]\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = findViewById(R.id.container);

        mNativeBindingX = NativeBindingX.create();
        LogProxy.sEnableLog = true;
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindTiming();
            }
        });
    }

    private void bindTiming() {
        mNativeBindingX.bind(mRootView, JSON.parseObject(timingConfig), new NativeCallback() {
            @Override
            public void callback(Map<String, Object> params) {

            }
        });
    }
}
