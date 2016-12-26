package com.yy.okfiledownloader;

import android.text.TextUtils;

import com.yy.okfiledownloader.util.FileDownloadUtils;

import java.io.File;

import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FileUtils;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/26 15:04
 * Time: 15:04
 * Description:
 */
public class SimpleFileDownloader {

    /**
     * 下载文件
     *
     * @param url
     * @param fileDownloaderCallback
     */
    public static void downloadFile(String url, FileDownloaderCallback fileDownloaderCallback) {

    }

    /**
     * @param url
     * @param savePath
     * @param fileDownloaderCallback
     */
    public static void downloadFile(String url, String savePath, FileDownloaderCallback fileDownloaderCallback) {
        if (StringUtils.isEmpty(url)) {
            savePath = createPath(url);
        } else {
            File file = new File(savePath);
            if (!file.exists()) {
                FileUtils.mkdirs(file.getParentFile());
            }
        }

    }

    /**
     * 创建下载保存地址
     * @param url
     * @return
     */
    private static String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }


}
