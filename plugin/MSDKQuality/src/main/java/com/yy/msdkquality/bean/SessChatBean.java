package com.yy.msdkquality.bean;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/7/14 14:23
 * Time: 14:23
 * Description:
 */
public class SessChatBean {

    private long uid;
    private String nickname;
    private String text;


    @Override
    public String toString() {
        return "SessChatBean{" +
                "uid=" + uid +
                ", nickname='" + nickname + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
