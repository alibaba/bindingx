package com.alibaba.android.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.playground.component.BindingXSliderView;

public class Demo2Activity extends AppCompatActivity {

    private ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        mRootView = findViewById(R.id.container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(60));


        final BindingXSliderView slider = createBindingXSlider();
        slider.setAnimationConfig(new BindingXSliderView.ConfigBuilder()
                .withDuration(500)//ms
                .withEasingFunction("easeOutQuart") //easeOutBack   https://easings.net/zh-tw
                .withFlipInterval(1000)//ms
                .build());


        mRootView.addView(slider, params);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.startFlipping();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.stopFlipping();
            }
        });
    }

    private BindingXSliderView createBindingXSlider() {
        final BindingXSliderView slider = new BindingXSliderView(this);
        int[] bgs = new int[]{0xffE57373,0xffBA68C8,0xff4DB6AC,0xffD4E157};
        for (int i = 0; i < 4; i++) {
            TextView child = new TextView(this);
            child.setText("I AM CHILD " + i);
            child.setBackgroundColor(bgs[i]);
            child.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(60));
            slider.addView(child, params);
        }

        return slider;
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
