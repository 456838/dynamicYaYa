package com.yy.okfiledownloader;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/26 15:05
 * Time: 15:05
 * Description:
 */
public abstract class FileDownloaderCallback {
    /**
     * onPending 等待，已经进入下载队列	数据库中的soFarBytes与totalBytes
     * connected 已经连接上	ETag, 是否断点续传, soFarBytes, totalBytes
     *
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param preProgress
     */
    public abstract void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress);

    /**
     * 下载进度回调	soFarBytes
     *
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param speed
     * @param progress
     */
    public abstract void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress);

    /**
     * 等待
     *
     * @param downloadId
     */
    public abstract void onWait(int downloadId);

    /**
     * paused	暂停下载	soFarBytes
     * error	下载出现错误	抛出的Throwable
     *
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param progress
     */
    public abstract void onStop(int downloadId, long soFarBytes, long totalBytes, int progress);

    /**
     * 完成整个下载过程
     *
     * @param downloadId
     * @param path
     */
    public abstract void onFinish(int downloadId, String path);
}
