package com.yy.dynamicyaya;

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


    public PluginBean() {

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
