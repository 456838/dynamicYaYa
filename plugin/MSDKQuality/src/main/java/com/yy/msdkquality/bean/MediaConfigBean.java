package com.yy.msdkquality.bean;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/11/18 10:04
 * Time: 10:04
 * Description:
 */
public class MediaConfigBean implements Serializable {

    private int audioQuality;      //音质
    private int hardwareDecode;    //硬解码
    private int hardwareEncode;    //硬编码
    //    private int scaleMode ;     //缩放模式
    private int fastplay;  //快速接入
    private int lowflow;   //低流模式

    private int currentCodeRate ;

    public int getCurrentCodeRate() {
        return currentCodeRate;
    }

    public void setCurrentCodeRate(int currentCodeRate) {
        this.currentCodeRate = currentCodeRate;
    }

    @Override
    public String toString() {
        return "MediaConfigBean{" +
                "currentCodeRate=" + currentCodeRate +
                ", lowflow=" + lowflow +
                ", fastplay=" + fastplay +
                ", hardwareEncode=" + hardwareEncode +
                ", hardwareDecode=" + hardwareDecode +
                ", audioQuality=" + audioQuality +
                '}';
    }

    public int getAudioQuality() {
        return audioQuality;
    }

    public MediaConfigBean setAudioQuality(int audioQuality) {
        this.audioQuality = audioQuality;
        return this;
    }

    public int getHardwareDecode() {
        return hardwareDecode;
    }

    public MediaConfigBean setHardwareDecode(int hardwareDecode) {
        this.hardwareDecode = hardwareDecode;
        return this;
    }

    public int getHardwareEncode() {
        return hardwareEncode;
    }

    public MediaConfigBean setHardwareEncode(int hardwareEncode) {
        this.hardwareEncode = hardwareEncode;
        return this;
    }

    public int getFastplay() {
        return fastplay;
    }

    public MediaConfigBean setFastplay(int fastplay) {
        this.fastplay = fastplay;
        return this;
    }

    public int getLowflow() {
        return lowflow;
    }

    public MediaConfigBean setLowflow(int lowflow) {
        this.lowflow = lowflow;
        return this;
    }

    public Map<Integer, Integer> build() {
        Map<Integer, Integer> configs = new HashMap<>();
//        configs.put(YCConstant.ConfigKey.AUDIO_ENCODE_QUALITY, getAudioQuality());     //设置音质
//        configs.put(YCConstant.ConfigKey.AUDIO_RECORD_QUALITY, getAudioQuality());     //设置音质
//        configs.put(YCConstant.ConfigKey.AUDIO_RECORD_QUALITY, getAudioQuality());     //设置音质
//        configs.put(MediaVideoMsg.MediaConfigKey.CCK_ENABLE_FASTPLAY_HIGHT_QUALITY_MODE_NEW, getFastplay());//设置快速模式
//
//        configs.put(MediaVideoMsg.MediaConfigKey.CCK_HARDWARE_DECODE, getHardwareDecode());       //硬解
//        configs.put(MediaVideoMsg.MediaConfigKey.CCK_H265_HARDWARE_DECODE, getHardwareDecode());  //h265硬解
//        configs.put(MediaVideoMsg.MediaConfigKey.CCK_HARDWARE_ENCODE, getHardwareEncode());    //硬编
//        configs.put(MediaVideoMsg.MediaConfigKey.CCK_H265_HARDWARE_ENCODE, getHardwareEncode());   //h265硬编
        return configs;
    }

}
