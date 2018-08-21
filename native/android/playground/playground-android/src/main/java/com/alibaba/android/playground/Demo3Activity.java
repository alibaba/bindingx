package com.alibaba.android.playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.playground.component.BindingXSliderView;
import com.alibaba.android.playground.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class Demo3Activity extends AppCompatActivity {

    private ViewGroup mRootView;
    private BindingXSliderView mTouTiaoSlider;
    private BindingXSliderView mWuliuSlider;

    private static final List<TouTiao> FAKE_TOUTIAO_DATA = Arrays.asList(
            TouTiao.create("手机","手机","快来围观 联想K5 Note首发评测","手机别着急买，9月iphoneX新品上市", R.drawable.bg_1),
            TouTiao.create("最新","手机","三星发布全新旗舰Note100","因崔斯汀，锤子新品上市", R.drawable.bg_2),
            TouTiao.create("旅行","超赞","中国唯一一座城市，三千年来从未改变","weex富交互终极解决方案BindingX重磅开源", R.drawable.bg_3),
            TouTiao.create("手机","母婴","魅族16如何，已经有人上手了","孕妈顺产时开宫口慢，跟四种情况有关", R.drawable.bg_1)
            );

    private static final List<Wuliu> FAKE_WULIU_DATA = Arrays.asList(
            Wuliu.create("已签收", "签收人是小邮局，期待再次为您服务", "8-20", R.drawable.wuliu_1),
            Wuliu.create("运输中", "快件已到达杭州市余杭区文一西路100号菜鸟驿站", "8-23", R.drawable.wuliu_2),
            Wuliu.create("已签收", "签收人是淘小宝", "8-19", R.drawable.wuliu_3),
            Wuliu.create("运输中", "快件已到达海南省三亚市中转站", "8-18", R.drawable.wuliu_4)
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo3);

        mRootView = findViewById(R.id.container);

        // create slider view from xml
        mTouTiaoSlider = findViewById(R.id.slider_toutiao);
        bindTouTiaoData(mTouTiaoSlider);


        mWuliuSlider = findViewById(R.id.slider_wuliu);
        bindWuliuData(mWuliuSlider);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mTouTiaoSlider == null) {
            return;
        }

        mTouTiaoSlider.setAnimationConfig(new BindingXSliderView.ConfigBuilder()
                .withEasingFunction("easeOutQuart")
                .withDuration(500)
                .withFlipInterval(2000)
                .build());
        mTouTiaoSlider.startFlipping();


        mWuliuSlider.setAnimationConfig(new BindingXSliderView.ConfigBuilder()
                .withEasingFunction("easeOutQuart")
                .withDuration(700)
                .withFlipInterval(2100)
                .withBindingXTransition("opacity", Pair.create(0f, 1f), Pair.<Object,Object>create(0.4f, 1f))
                .build());
        mWuliuSlider.startFlipping();

    }

    private void bindTouTiaoData(BindingXSliderView sliderView) {
        for (int i = 0; i < FAKE_TOUTIAO_DATA.size(); i++) {
            View view = View.inflate(this, R.layout.item_taobao_toutiao, null);
            bindTouTiao(view, FAKE_TOUTIAO_DATA.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 60));
            sliderView.addView(view, params);
        }
    }

    private void bindWuliuData(BindingXSliderView sliderView) {
        for (int i = 0; i < FAKE_WULIU_DATA.size(); i++) {
            View view = View.inflate(this, R.layout.item_taobao_wuliu, null);
            bindWuliu(view, FAKE_WULIU_DATA.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 60));
            sliderView.addView(view, params);
        }
    }


    static class TouTiao {
        String label1,label2;
        String title1,title2;
        int imageId;


        TouTiao() {}

        static TouTiao create(String label1, String label2, String title1, String title2, int imageId) {
            TouTiao touTiao = new TouTiao();
            touTiao.label1 = label1;
            touTiao.label2 = label2;

            touTiao.title1 = title1;
            touTiao.title2 = title2;
            touTiao.imageId = imageId;
            return touTiao;
        }
    }

    static class Wuliu {
        String state,desc,date;
        int imageId;

        Wuliu(){}

        static Wuliu create(String state, String desc, String date, int imageId) {
            Wuliu wuliu = new Wuliu();
            wuliu.date = date;
            wuliu.desc = desc;
            wuliu.state = state;
            wuliu.imageId = imageId;
            return wuliu;
        }
    }

    private static void bindTouTiao(View view, TouTiao touTiao) {
        TextView label1View = view.findViewById(R.id.label_1);
        TextView label2View = view.findViewById(R.id.label_2);
        TextView title1View = view.findViewById(R.id.title_1);
        TextView title2View = view.findViewById(R.id.title_2);
        ImageView imageView = view.findViewById(R.id.image);

        label1View.setText(touTiao.label1);
        label2View.setText(touTiao.label2);
        title1View.setText(touTiao.title1);
        title2View.setText(touTiao.title2);
        imageView.setImageResource(touTiao.imageId);
    }

    private void bindWuliu(View view, Wuliu wuliu) {
        TextView dateView = view.findViewById(R.id.date);
        TextView stateView = view.findViewById(R.id.state);
        TextView descView = view.findViewById(R.id.desc);

        ImageView imageView = view.findViewById(R.id.image);

        dateView.setText(wuliu.date);
        descView.setText(wuliu.desc);
        stateView.setText(wuliu.state);

        imageView.setImageResource(wuliu.imageId);
    }

}
