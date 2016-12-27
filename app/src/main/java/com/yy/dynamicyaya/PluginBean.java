package com.yy.dynamicyaya;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

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

    public void setApkPackageInfo(PackageManager pm,PackageInfo apkPackageInfo,String path) {
        this.apkPackageInfo = apkPackageInfo;
        try {
            apkIcon = pm.getApplicationIcon(apkPackageInfo.applicationInfo);
        } catch (Exception e) {
            apkIcon = pm.getDefaultActivityIcon();
        }
        apkTitle = pm.getApplicationLabel(apkPackageInfo.applicationInfo);
        apkVersionName = apkPackageInfo.versionName;
        apkVersionCode = apkPackageInfo.versionCode;
        apkFile = path;
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
                "iconUrl='" + iconUrl + '\'' +
                ", pluginUrl='" + pluginUrl + '\'' +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}
