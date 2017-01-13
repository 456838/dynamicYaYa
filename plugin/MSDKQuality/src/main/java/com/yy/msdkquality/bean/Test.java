package com.yy.msdkquality.bean;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/11/8 19:07
 * Time: 19:07
 * Description:
 */
public class Test {


    /**
     * sid : 991
     * subsid : 0
     * onlinePeople : 10000
     * titleInfo : 听音乐就来991
     * thumbnail : https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png
     */

    private int sid;
    private int subsid;
    private int onlinePeople;
    private String titleInfo;
    private String thumbnail;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getSubsid() {
        return subsid;
    }

    public void setSubsid(int subsid) {
        this.subsid = subsid;
    }

    public int getOnlinePeople() {
        return onlinePeople;
    }

    public void setOnlinePeople(int onlinePeople) {
        this.onlinePeople = onlinePeople;
    }

    public String getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
