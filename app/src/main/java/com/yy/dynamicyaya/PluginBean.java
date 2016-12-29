package com.yy.dynamicyaya;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/18 15:16
 * Time: 15:16
 * Description:
 */
public class PluginBean {

    private int order;
    private String name;
    private String pluginUrl;
    private String iconUrl;
    private String version;

    /**
     * apk文件下载以后再处理这些
     */
    Drawable apkIcon;
    CharSequence apkTitle;
    String apkVersionName;
    int apkVersionCode;
    String apkFile;
    PackageInfo apkPackageInfo;
    boolean apkInstalling = false;

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apkIcon) {
        this.apkIcon = apkIcon;
    }

    public CharSequence getApkTitle() {
        return apkTitle;
    }

    public void setApkTitle(CharSequence apkTitle) {
        this.apkTitle = apkTitle;
    }

    public String getApkVersionName() {
        return apkVersionName;
    }

    public void setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
    }

    public int getApkVersionCode() {
        return apkVersionCode;
    }

    public void setApkVersionCode(int apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
    }

    public String getApkFile() {
        return apkFile;
    }

    public void setApkFile(String apkFile) {
        this.apkFile = apkFile;
    }

    public PackageInfo getApkPackageInfo() {
        return apkPackageInfo;
    }


    public static Resources getResources(Context context, String apkPath) throws Exception {
        String PATH_AssetManager = "android.content.res.AssetManager";
        Class assetMagCls = Class.forName(PATH_AssetManager);
        Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
        Object assetMag = assetMagCt.newInstance((Object[]) null);
        Class[] typeArgs = new Class[1];
        typeArgs[0] = String.class;
        Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                typeArgs);
        Object[] valueArgs = new Object[1];
        valueArgs[0] = apkPath;
        assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
        Resources res = context.getResources();
        typeArgs = new Class[3];
        typeArgs[0] = assetMag.getClass();
        typeArgs[1] = res.getDisplayMetrics().getClass();
        typeArgs[2] = res.getConfiguration().getClass();
        Constructor resCt = Resources.class.getConstructor(typeArgs);
        valueArgs = new Object[3];
        valueArgs[0] = assetMag;
        valueArgs[1] = res.getDisplayMetrics();
        valueArgs[2] = res.getConfiguration();
        res = (Resources) resCt.newInstance(valueArgs);
        return res;
    }

    public void setApkPackageInfo(PackageManager pm, PackageInfo info, String path) {
        if (pm ==null){
            Logger.i("PackageManager == null");
        }
        if(info ==null){
            Logger.i("PackageInfo == null");
        }
        try {
            apkIcon = pm.getApplicationIcon(info.applicationInfo);
        } catch (Exception e) {
            apkIcon = pm.getDefaultActivityIcon();
        }
        apkTitle = pm.getApplicationLabel(info.applicationInfo);
        apkVersionName = info.versionName;
        apkVersionCode = info.versionCode;
        apkFile = path;
        apkPackageInfo = info;
    }


    public boolean isApkInstalling() {
        return apkInstalling;
    }

    public void setApkInstalling(boolean apkInstalling) {
        this.apkInstalling = apkInstalling;
    }

    public PluginBean(int order, String name, String pluginUrl, String iconUrl) {
        this.order = order;
        this.name = name;
        this.pluginUrl = pluginUrl;
        this.iconUrl = iconUrl;
    }

    public PluginBean(int order, String name, String pluginUrl, String iconUrl, String version) {
        this.order = order;
        this.name = name;
        this.pluginUrl = pluginUrl;
        this.iconUrl = iconUrl;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginUrl() {
        return pluginUrl;
    }

    public void setPluginUrl(String pluginUrl) {
        this.pluginUrl = pluginUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        return "PluginBean{" +
                "order=" + order +
                ", name='" + name + '\'' +
                ", pluginUrl='" + pluginUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", version='" + version + '\'' +
                ", apkIcon=" + apkIcon +
                ", apkTitle=" + apkTitle +
                ", apkVersionName='" + apkVersionName + '\'' +
                ", apkVersionCode=" + apkVersionCode +
                ", apkFile='" + apkFile + '\'' +
                ", apkPackageInfo=" + apkPackageInfo +
                ", apkInstalling=" + apkInstalling +
                '}';
    }
}
