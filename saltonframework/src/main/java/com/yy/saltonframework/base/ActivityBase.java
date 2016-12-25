package com.yy.saltonframework.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.orhanobut.logger.Logger;


/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2015-08-06
 * Time: 12:23
 */
public abstract class ActivityBase extends AppCompatActivity {
    public void ShowToast(String p_Msg) {
        Toast.makeText(this, p_Msg, Toast.LENGTH_SHORT).show();
        Logger.d(p_Msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        //注册一个Activity管理，用于完全退出本软件。
//        CustomActivityManager.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);

    }

    public Context GetContext() {
        return getApplicationContext();
    }

    /**
     * 根据类打开Activity
     *
     * @param pClass
     */
    public void OpenActivity(Class<?> pClass) {
        OpenActivity(pClass, null);
    }

    /**
     * 带有参数的根据类打开Activity
     *
     * @param pClass
     * @param pBundle 封装的参数
     */
    public void OpenActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void OpenActivityForResult(Class<?> pClass, int pRequestCode) {
        OpenActivityForResult(pClass, pRequestCode, null);
    }

    /**
     * 带有参数的根据类打开Activity
     *
     * @param pClass
     * @param pBundle 封装的参数
     */
    public void OpenActivityForResult(Class<?> pClass, int pRequestCode, Bundle pBundle) {
        Intent intent = new Intent(GetContext(), pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivityForResult(intent, pRequestCode);
    }

    /**
     * @return 布局解析器
     */
    protected LayoutInflater GetLayouInflater() {
        LayoutInflater _LayoutInflater = LayoutInflater.from(this);
        return _LayoutInflater;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
