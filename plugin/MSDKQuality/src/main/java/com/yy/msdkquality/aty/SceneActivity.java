package com.yy.msdkquality.aty;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.yy.msdkquality.R;

import java.util.Random;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/10 16:51
 * Time: 16:51
 * Description:
 */
public class SceneActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    private Button mBtnLogin;
    private EditText mEditUid;
    private EditText mEditSid;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_scene);

        initView();
    }

    public void initView() {

        mEditUid = (EditText) findViewById(R.id.edittext_uid);
        mEditSid = (EditText) findViewById(R.id.edittext_sid);
        mCheckBox = (CheckBox) this.findViewById(R.id.cb_login_model);

        Random random = new Random();
        int x = random.nextInt(899999);
        int number = x + 100000;
        mEditUid.setText(Integer.toString(number));
        mEditSid.setText("11133");

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = mEditUid.getText().toString().trim();
                String sid = mEditSid.getText().toString().trim();
                boolean checked = mCheckBox.isChecked();

                if (uid.isEmpty() || sid.isEmpty()) {
                    new AlertDialog.Builder(SceneActivity.this)
                            .setMessage(R.string.param_invalid)
                            .show();
                    return;
                }
                Intent intent = new Intent(SceneActivity.this, LiveActivity.class);
                intent.putExtra("uid", Integer.parseInt(uid));
                intent.putExtra("sid", Integer.parseInt(sid));
                intent.putExtra("loginModel", checked);
                startActivity(intent);

            }
        });
    }
}
