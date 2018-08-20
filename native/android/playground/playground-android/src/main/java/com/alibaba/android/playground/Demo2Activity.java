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

        // create vertical slider
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(60));
        final BindingXSliderView slider1 = createFakeBindingXSlider1();
        slider1.setAnimationConfig(new BindingXSliderView.ConfigBuilder()
                .withDuration(500)//ms
                .withEasingFunction("easeOutQuart") //easeOutBack   https://easings.net/zh-tw
                .withFlipInterval(1000)//ms
                .build());
        mRootView.addView(slider1, params);

        // create horizontal slider
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(160));
        params2.topMargin = dp2px(50);
        final BindingXSliderView slider2 = createFakeBindingXSlider2();
        slider2.setAnimationConfig(new BindingXSliderView.ConfigBuilder()
                .withDuration(700)//ms
                .withEasingFunction("easeOutBack") //easeOutBack   https://easings.net/zh-tw
                .withFlipInterval(2000)//ms
                .build());
        mRootView.addView(slider2, params2);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider1.startFlipping();
                slider2.startFlipping();

            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider1.stopFlipping();
                slider2.stopFlipping();
            }
        });
    }

    private BindingXSliderView createFakeBindingXSlider1() {
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

    private BindingXSliderView createFakeBindingXSlider2() {
        final BindingXSliderView slider = new BindingXSliderView(this, false);
        int[] bgs = new int[]{0xffE57373,0xffBA68C8,0xff4DB6AC,0xffD4E157};
        for (int i = 0; i < 4; i++) {
            TextView child = new TextView(this);
            child.setText("I AM CHILD " + i);
            child.setBackgroundColor(bgs[i]);
            child.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(160));
            slider.addView(child, params);
        }

        return slider;
    }



    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
