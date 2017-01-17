package com.yy.saltonframework.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2015/12/12 17:12
 * Time: 17:12
 * Description:
 */
public class FormatUtils {

    /**
     *  将数字按照中国货币显示的转换
     * @param number   未转换的数字
     * @return  返回格式化的数字字符串
     */
    public static String FormatNumForChinese(double number){
        return NumberFormat.getNumberInstance(Locale.CHINA).format(number);
    }
}
