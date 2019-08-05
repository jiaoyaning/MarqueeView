package com.jyn.marqueeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jyn.marqueetextview.MarqueeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        MarqueeView marqueeView = findViewById(R.id.marquee);
        marqueeView.setText("东风夜放花千树，更吹落星如雨，宝马雕车香满路，凤箫声动，玉壶光转，一夜鱼龙舞。");

        MarqueeView marqueeView2 = findViewById(R.id.marquee2);
        marqueeView2.setText("东风夜放花千树，更吹落星如雨，宝马雕车香满路，凤箫声动，玉壶光转，一夜鱼龙舞。");
    }
}
