package com.yy.xiguaplayer;

import android.os.Environment;

import java.io.File;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/4 15:59
 * Time: 15:59
 * Description:
 */
public class YConfig {
    public static final String DEFALULT_SAVE_LOCATION = Environment.getExternalStorageDirectory()+ File.separator+"NewSalton"+File.separator;
//    public static final String DEFALULT_SAVE_LOCATION = Environment.getExternalStorageDirectory()+ File.separator;

    public static final String CACHE_PATH=DEFALULT_SAVE_LOCATION+"cache";
}
