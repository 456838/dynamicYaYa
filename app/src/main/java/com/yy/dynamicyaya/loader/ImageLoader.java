package com.yy.dynamicyaya.loader;

import android.widget.ImageView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/20 9:59
 * Time: 9:59
 * Description:
 */
public class ImageLoader {
    public static void bind(ImageView imageView, String url){
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCircular(true)
                .setFadeIn(true)
                .setRadius(2)
                .build();

        x.image().bind(imageView,url,imageOptions);
    }
}
