package com.alibaba.android.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alibaba.android.bindingx.plugin.android.NativeBindingX;
import com.alibaba.android.bindingx.plugin.android.NativeCallback;
import com.alibaba.fastjson.JSON;

import java.util.Map;

public class Demo1Activity extends AppCompatActivity {

    private NativeBindingX mNativeBindingX = null;
    private View mRootView;

    private String timingConfig = "{\n" +
            "   \"eventType\":\"timing\",\n" +
            "   \"exitExpression\":{\n" +
            "     \"origin\":\"t>2000\",\n" +
            "     \"transformed\":\"{\\\"type\\\":\\\">\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"t\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}\"\n" +
            "   },\n" +
            "   \"props\":[\n" +
            "     {\n" +
            "       \"element\":\"target\",\n" +
            "       \"property\":\"transform.translateX\",\n" +
            "       \"expression\":{\n" +
            "         \"origin\":\"linear(t,0,400,2000)\",\n" +
            "         \"transformed\":\"{\\\"type\\\":\\\"CallExpression\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"linear\\\"},{\\\"type\\\":\\\"Arguments\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"t\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":0},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":400},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}]}\"\n" +
            "       }\n" +
            "     },\n" +
            "     {\n" +
            "       \"element\":\"target\",\n" +
            "       \"property\":\"background-color\",\n" +
            "       \"expression\":{\n" +
            "         \"origin\":\"evaluateColor('#ff0000','#607D8B',min(t,2000)/2000)\",\n" +
            "         \"transformed\":\"{\\\"type\\\":\\\"CallExpression\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"evaluateColor\\\"},{\\\"type\\\":\\\"Arguments\\\",\\\"children\\\":[{\\\"type\\\":\\\"StringLiteral\\\",\\\"value\\\":\\\"'#ff0000'\\\"},{\\\"type\\\":\\\"StringLiteral\\\",\\\"value\\\":\\\"'#607D8B'\\\"},{\\\"type\\\":\\\"/\\\",\\\"children\\\":[{\\\"type\\\":\\\"CallExpression\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"min\\\"},{\\\"type\\\":\\\"Arguments\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"t\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}]},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":2000}]}]}]}\"\n" +
            "       }\n" +
            "     }  \n" +
            "   ]\n" +
            "}";

    private String orientationConfig = "{\n" +
            "   \"eventType\":\"orientation\",\n" +
            "   \"props\":[\n" +
            "     {\n" +
            "       \"element\":\"target\",\n" +
            "       \"property\":\"transform.translateX\",\n" +
            "       \"expression\":{\n" +
            "         \"origin\":\"x+0\",\n" +
            "         \"transformed\":\"{\\\"type\\\":\\\"+\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"x\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":0}]}\"\n" +
            "       }\n" +
            "     },\n" +
            "     {\n" +
            "       \"element\":\"target\",\n" +
            "       \"property\":\"transform.translateY\",\n" +
            "       \"expression\":{\n" +
            "         \"origin\":\"y+0\",\n" +
            "         \"transformed\":\"{\\\"type\\\":\\\"+\\\",\\\"children\\\":[{\\\"type\\\":\\\"Identifier\\\",\\\"value\\\":\\\"y\\\"},{\\\"type\\\":\\\"NumericLiteral\\\",\\\"value\\\":0}]}\"\n" +
            "       }\n" +
            "     }\n" +
            "    ]\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        mRootView = findViewById(R.id.container);

        mNativeBindingX = NativeBindingX.create();
//        LogProxy.sEnableLog = true;
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindTiming();
            }
        });
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindOrientation();
            }
        });
        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNativeBindingX.unbindAll();
            }
        });
    }

    private void bindTiming() {
        mNativeBindingX.bind(mRootView, JSON.parseObject(timingConfig), new NativeCallback() {
            @Override
            public void callback(Map<String, Object> params) {
                Log.v("CHUYI",params.toString());
            }
        });
    }

    private void bindOrientation() {
        mNativeBindingX.bind(mRootView, JSON.parseObject(orientationConfig), new NativeCallback() {
            @Override
            public void callback(Map<String, Object> params) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNativeBindingX.onDestroy();
    }
}
