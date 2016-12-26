package com.yy.okfiledownloader;

import com.yy.okfiledownloader.bean.FileDownloaderModel;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/26 15:21
 * Time: 15:21
 * Description:
 */
public interface IFileDownloader {

    void start(FileDownloaderModel fileDownloaderModel);

    void pause(FileDownloaderModel fileDownloaderModel);
    
    void finish();

}
