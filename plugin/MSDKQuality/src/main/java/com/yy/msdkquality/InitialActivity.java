package com.yy.msdkquality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yy.msdkquality.engine.SDKEngine;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/24 10:16
 * Time: 10:16
 * Description:
 */
public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_initial);
        findViewById(R.id.btn_test_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInited = SDKEngine.getInstance().init(InitialActivity.this, YConfig.mAppKey, YConfig.mAppSecret, YConfig.mAppVersion);
                if (isInited) {
                    startActivity(new Intent(InitialActivity.this, MainActivity.class));
                }
            }
        });

        findViewById(R.id.btn_51talk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInited = SDKEngine.getInstance().init(InitialActivity.this, YConfig.mAppKey, YConfig.mAppSecret, YConfig.mAppVersion);
                if (isInited) {
                    startActivity(new Intent(InitialActivity.this, MainActivity.class));
                }
            }
        });

        findViewById(R.id.btn_mshow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInited = SDKEngine.getInstance().init(InitialActivity.this, YConfig.mAppKey_Mshow, YConfig.mAppSecret_Mshow, YConfig.mAppVersion_Mshow);
                if (isInited) {
                    startActivity(new Intent(InitialActivity.this, MainActivity.class));
                }
            }
        });
    }
}
