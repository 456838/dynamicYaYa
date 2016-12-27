package com.yy.dynamicyaya;

import android.app.Application;
import android.content.Context;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.morgoo.droidplugin.PluginHelper;

import org.xutils.x;


/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/8/22 21:45
 * Time: 21:45
 * Description:
 */
public class CustomApplication extends Application {

    private static CustomApplication mInstance;

    public static CustomApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        x.Ext.init(this);   //初始化Xutils
        x.Ext.setDebug(true);
        TypefaceProvider.registerDefaultIconSets();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }


}
