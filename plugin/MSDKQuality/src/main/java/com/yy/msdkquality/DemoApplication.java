package com.yy.msdkquality;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Process;

import com.hjc.SDKParam.SDKParam.AppInfo;
import com.orhanobut.logger.Logger;
import com.ycloud.live.YCMedia;
import com.ycsignal.outlet.IProtoMgr;
import com.yy.saltonframework.util.LogUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DemoApplication extends Application {
    private long mTerminalType = 0x20001; //android terminal type 0x20001

    //app信息在欢聚云应用管理页面上获取
    private int mAppKey = YConfig.mAppKey;
    private int mAppVersion = YConfig.mAppVersion;
    private static AtomicReference<DemoApplication> mInstance =
            new AtomicReference<DemoApplication>();

    public static DemoApplication getInstance() {
        return mInstance.get();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance.set(this);
        if (isPkgMainProc()) {
            File esdf = Environment.getExternalStorageDirectory();
            String logPath = esdf.getAbsolutePath();
            if (null != logPath) {
                logPath += "/ycmedia/msdkquality";
            }
            AppInfo app = new AppInfo();
            app.appKey = mAppKey;
            app.terminalType = mTerminalType;
            app.logPath = logPath.getBytes();
            app.appVer = String.valueOf(mAppVersion).getBytes();
            IProtoMgr.instance().init(getApplicationContext(), app, null);
            boolean mediaInit = YCMedia.getInstance().init(this, IProtoMgr.instance(), logPath);
            LogUtils.i("mediaInit:" + mediaInit);
        }else{
            LogUtils.i("mediaInit failed:" );
        }
    }

    @Override
    public void onTerminate() {
        YCMedia.getInstance().unInit();
        IProtoMgr.instance().deInit();
        LogUtils.i("sdk unInit");
    }

    public boolean isPkgMainProc() {
        ActivityManager am = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = this.getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
