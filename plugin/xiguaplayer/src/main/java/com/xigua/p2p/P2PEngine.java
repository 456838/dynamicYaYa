package com.xigua.p2p;

import com.orhanobut.logger.Logger;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/6 9:49
 * Time: 9:49
 * Description:
 */
public class P2PEngine {

    public static void addDownloadTask(String downloadUrl) {
        try {
            byte[] downloadUrlBytes = downloadUrl.getBytes("GBK");
            int ret = P2PClass.getInstance().P2Pdoxadd(downloadUrlBytes);
            long downloadSize = P2PClass.getInstance().P2Pgetdownsize(ret);
            long fileSize = P2PClass.getInstance().P2Pgetfilesize(ret);
            long localFileSize = P2PClass.getInstance().P2Pgetlocalfilesize(downloadUrlBytes);
            long speed = P2PClass.getInstance().P2Pgetspeed(-1);
            int percent = P2PClass.getInstance().P2Pgetpercent();
            int startcode = P2PClass.getInstance().P2Pdoxstart(downloadUrlBytes);

            Logger.i("添加下载任务,id:%d,downloadSize:%d,fileSize:%d,localFileSize:%d,speed:%d,percent:%d,startcode:%d", ret, downloadSize, fileSize, localFileSize, speed, percent, startcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
