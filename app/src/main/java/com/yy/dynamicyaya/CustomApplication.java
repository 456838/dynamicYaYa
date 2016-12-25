package com.yy.dynamicyaya;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.finalteam.filedownloaderfinal.DownloaderManager;
import cn.finalteam.filedownloaderfinal.DownloaderManagerConfiguration;
import cn.finalteam.toolsfinal.StorageUtils;


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
    }

    private void initDownloaderManager() {
        //下载文件所保存的目录
        File storeFile = StorageUtils.getCacheDirectory(this, false, "FileDownloader");
        if (!storeFile.exists()) {
            storeFile.mkdirs();
        }

        Map<String, String> dbExFeildMap = new HashMap<>();
        dbExFeildMap.put("title", "VARCHAR");
        dbExFeildMap.put("icon", "VARCHAR");
        final DownloaderManagerConfiguration.Builder dmBulder = new DownloaderManagerConfiguration.Builder(this)
                .setMaxDownloadingCount(3) //配置最大并行下载任务数，配置范围[1-100]
                .setDbExtField(dbExFeildMap) //配置数据库扩展字段
        .setDbVersion(1)//配置数据库版本
        .setDbUpgradeListener(null) //配置数据库更新回调
        .setDownloadStorePath(storeFile.getAbsolutePath()); //配置下载文件存储目录
        //初始化下载管理最好放到线程中去执行防止卡顿情况
        new Thread() {
            @Override
            public void run() {
                super.run();
                DownloaderManager.getInstance().init(dmBulder.build());//必要语句
            }
        }.start();
    }

}
