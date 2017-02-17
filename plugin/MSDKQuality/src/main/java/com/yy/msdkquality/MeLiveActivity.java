package com.yy.msdkquality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yy.msdkquality.aty.ChannelTestLivingRoomActivity;
import com.yy.msdkquality.engine.SDKEngine;
import com.yy.saltonframework.base.ActivityBase;
import com.yy.saltonframework.util.ToastUtils;

import java.util.Random;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/2/16 9:54
 * Time: 9:54
 * Description:
 */
public class MeLiveActivity extends ActivityBase {

    private EditText et_channelId;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_me_live);
        boolean isInited = SDKEngine.getInstance().init(MeLiveActivity.this, YConfig.mAppKey_ME, YConfig.mAppSecret_ME, YConfig.mAppVersion_ME);
        if (isInited) {
//            startActivity(new Intent(MeLiveActivity.this, MainActivity.class));
        } else {
            ToastUtils.show(getApplicationContext(), "初始化SDK失败");
        }
        et_channelId = (EditText) findViewById(R.id.et_channelId);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        if (!isInited) {
            btn_ok.setEnabled(false);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sid = et_channelId.getText().toString().trim();
                Random random = new Random();
                int x = random.nextInt(899999);
                int number = x + 100000;
                Intent intent = new Intent(MeLiveActivity.this, ChannelTestLivingRoomActivity.class);
                intent.putExtra("uid", Integer.toString(number));
                intent.putExtra("sid", Integer.parseInt(sid));
                startActivity(intent);
            }
        });
    }


}
