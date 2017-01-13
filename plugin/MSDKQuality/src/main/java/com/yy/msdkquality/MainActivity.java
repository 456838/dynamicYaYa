package com.yy.msdkquality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.yy.msdkquality.aty.ChannelTestLivingRoomActivity;
import com.yy.msdkquality.aty.LiveActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private BootstrapButton btn_anchor,btn_audience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_anchor = (BootstrapButton) findViewById(R.id.btn_anchor);
        btn_anchor.setOnClickListener(this);
        btn_audience = (BootstrapButton) findViewById(R.id.btn_audience);
        btn_audience.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_anchor:
                startActivity(new Intent(MainActivity.this, LiveActivity.class));
                break;
            case R.id.btn_audience:
                startActivity(new Intent(MainActivity.this, ChannelTestLivingRoomActivity.class));
                break;
        }
    }
}
