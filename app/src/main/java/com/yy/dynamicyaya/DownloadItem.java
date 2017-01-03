package com.yy.dynamicyaya;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/3 9:56
 * Time: 9:56
 * Description:
 */
public class DownloadItem {
    private int id;
    private int apkVersionCode;
    private String pluginDownloadUrl;
    private String iconDownloadUrl;
    private String apkVersionName;
    private String name ;
    private String category;
    private int size ;
    private String description ;
    private boolean isFileExist ;

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isFileExist() {
        return isFileExist;
    }

    public void setFileExist(boolean fileExist) {
        isFileExist = fileExist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public DownloadItem() {
    }


    public DownloadItem(int id, int apkVersionCode, String packageName,String pluginDownloadUrl, String iconDownloadUrl, String apkVersionName, String name, String category, int size) {
        this.id = id;
        this.packageName = packageName ;
        this.apkVersionCode = apkVersionCode;
        this.pluginDownloadUrl = pluginDownloadUrl;
        this.iconDownloadUrl = iconDownloadUrl;
        this.apkVersionName = apkVersionName;
        this.name = name;
        this.category = category;
        this.size = size;
    }


    public DownloadItem(int id, int apkVersionCode, String pluginDownloadUrl, String iconDownloadUrl, String apkVersionName) {
        this.id = id;
        this.apkVersionCode = apkVersionCode;
        this.pluginDownloadUrl = pluginDownloadUrl;
        this.iconDownloadUrl = iconDownloadUrl;
        this.apkVersionName = apkVersionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApkVersionCode() {
        return apkVersionCode;
    }

    public void setApkVersionCode(int apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
    }

    public String getPluginDownloadUrl() {
        return pluginDownloadUrl;
    }

    public void setPluginDownloadUrl(String pluginDownloadUrl) {
        this.pluginDownloadUrl = pluginDownloadUrl;
    }

    public String getIconDownloadUrl() {
        return iconDownloadUrl;
    }

    public void setIconDownloadUrl(String iconDownloadUrl) {
        this.iconDownloadUrl = iconDownloadUrl;
    }

    public String getApkVersionName() {
        return apkVersionName;
    }

    public void setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DownloadItem{");
        sb.append("id=").append(id);
        sb.append(", apkVersionCode=").append(apkVersionCode);
        sb.append(", pluginDownloadUrl='").append(pluginDownloadUrl).append('\'');
        sb.append(", iconDownloadUrl='").append(iconDownloadUrl).append('\'');
        sb.append(", apkVersionName='").append(apkVersionName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
