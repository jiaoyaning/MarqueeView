package com.jyn.marqueeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jyn.marqueetextview.MarqueeView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {


    private MarqueeView marqueeView;
    private TextView speed_text, spacing_text;

    private SeekBar speed_seekbar, spacing_seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        marqueeView = findViewById(R.id.marquee);
        marqueeView.setText("东风夜放花千树，更吹落星如雨，宝马雕车香满路，凤箫声动，玉壶光转，一夜鱼龙舞。");
        speed_text = findViewById(R.id.speed_text);
        spacing_text = findViewById(R.id.spacing_text);

        speed_seekbar = findViewById(R.id.speed_seekbar);
        spacing_seekbar = findViewById(R.id.spacing_seekbar);

        speed_seekbar.setOnSeekBarChangeListener(this);
        spacing_seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == speed_seekbar) {
            speed_text.setText("速度：" + i);
            marqueeView.setSpeed(i);
        } else if (seekBar == spacing_seekbar) {
            spacing_text.setText("间隔：" + i);
            marqueeView.setSpacing(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
