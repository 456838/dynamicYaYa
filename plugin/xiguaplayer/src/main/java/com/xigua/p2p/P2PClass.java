package com.xigua.p2p;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.yy.xiguaplayer.YConfig;

import java.io.File;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/4 15:34
 * Time: 15:34
 * Description:
 */
public class P2PClass {
    private static P2PClass instance;

    /**
     * 单例模式
     *
     * @return
     */
    public static P2PClass getInstance() {
        if (instance == null) {
            instance = new P2PClass();
        }
        return instance;
    }

    private String cachePath = YConfig.CACHE_PATH;

    /**
     * 初始化P2P
     */
    public void init() {

        File cacheFile = new File(cachePath);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
            Logger.i("缓存目录创建成功!");
        }
        if (P2Pdoxstarthttpd(cachePath.getBytes()) == 0) {
            Logger.i("p2p服务已经初始化!");
        }
    }
//
//    public void reinit() {
//        String cmd = "rm -r " + cachePath;
//        try {
//            Runtime.getRuntime().exec(cmd).waitFor();
//            return;
//        } catch (Exception localException) {
//            localException.printStackTrace();
//        }
//    }

    static {
        System.loadLibrary("p2p");
    }

    private final native int dosetupload(int paramInt);

    private final native int doxadd(byte[] paramArrayOfByte);

    private final native int doxcheck(byte[] paramArrayOfByte);

    private final native int doxdel(byte[] paramArrayOfByte);

    private final native int doxdownload(byte[] paramArrayOfByte);

    private final native int doxendhttpd();

    private final native int doxpause(byte[] paramArrayOfByte);

    private final native int doxsave();

    private final native int doxsetduration(int paramInt);

    private final native int doxstart(byte[] paramArrayOfByte);

    private final native int doxstarthttpd(byte[] paramArrayOfByte);

    private final native int doxterminate();


    private final native long getdownsize(int paramInt);

    private final native long getfilesize(int paramInt);

    private final native long getlocalfilesize(byte[] paramArrayOfByte);

    private final native int getpercent();

    private final native long getspeed(int paramInt);
//
//    public long P2PGetFree() {
//        String str = YConfig.CACHE_PATH;
//        StatFs localStatFs = new StatFs(str);
//        return localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
//    }

    public long P2Pdosetduration(int paramInt) {
        return doxsetduration(paramInt);
    }

    public int P2Pdosetupload(int paramInt) {
        return dosetupload(paramInt);
    }

    public int P2Pdoxadd(byte[] paramArrayOfByte) {
        return doxadd(paramArrayOfByte);
    }

    public int P2Pdoxcheck(byte[] paramArrayOfByte) {
        return doxcheck(paramArrayOfByte);
    }

    public int P2Pdoxdel(byte[] paramArrayOfByte) {
        return doxdel(paramArrayOfByte);
    }

    public int P2Pdoxdownload(byte[] paramArrayOfByte) {
        return doxdownload(paramArrayOfByte);
    }

    public int P2Pdoxpause(byte[] paramArrayOfByte) {
        return doxpause(paramArrayOfByte);
    }

    public int P2Pdoxstart(byte[] paramArrayOfByte) {
        String str = new String(paramArrayOfByte);
        int ret =  doxstart(paramArrayOfByte);
        Log.e("aa","ret:"+ret+"path:"+str);
        return ret;
    }

    public int P2Pdoxstarthttpd(byte[] paramArrayOfByte) {
        return doxstarthttpd(paramArrayOfByte);
    }

    public int P2Pdoxterminate() {
        return doxterminate();
    }

    public long P2Pgetdownsize(int paramInt) {
        return getdownsize(paramInt);
    }

    public long P2Pgetfilesize(int paramInt) {
        return getfilesize(paramInt);
    }

    public long P2Pgetlocalfilesize(byte[] paramArrayOfByte) {
        return getlocalfilesize(paramArrayOfByte);
    }

    public int P2Pgetpercent() {
        return getpercent();
    }

    public long P2Pgetspeed(int paramInt) {
        return getspeed(paramInt);
    }

    public int getVersion() {
        return 18;
    }

}
