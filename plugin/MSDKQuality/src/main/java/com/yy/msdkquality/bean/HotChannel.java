package com.yy.msdkquality.bean;

import java.io.Serializable;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/11/8 15:30
 * Time: 15:30
 * Description:
 */
public class HotChannel implements Serializable {


    @Override
    public String toString() {
        return "HotChannel{" +
                "onlinePeople=" + onlinePeople +
                ", thumbnail='" + thumbnail + '\'' +
                ", titleInfo='" + titleInfo + '\'' +
                ", subsid=" + subsid +
                ", sid=" + sid +
                '}';
    }

    private long sid;
    private long subsid;
    private String titleInfo ;
    private String thumbnail ;
    private int onlinePeople ;


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

    public int getOnlinePeople() {
        return onlinePeople;
    }

    public void setOnlinePeople(int onlinePeople) {
        this.onlinePeople = onlinePeople;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getSubsid() {
        return subsid;
    }

    public void setSubsid(long subsid) {
        this.subsid = subsid;
    }
}
