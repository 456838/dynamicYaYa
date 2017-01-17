package com.yy.saltonframework.util;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2015/12/1 10:51
 * Time: 10:51
 * Description:校验银行卡是否正确的工具类
 */
public class LuhnUtils {

    public static boolean isBankNumber(String bankNumber) {
        char[] cc = bankNumber.toCharArray();
        int[] n = new int[cc.length + 1];
        int j = 1;
        for (int i = cc.length - 1; i >= 0; i--) {
            n[j++] = cc[i] - '0';
        }
        int even = 0;
        int odd = 0;
        for (int i = 1; i < n.length; i++) {
            if (i % 2 == 0) {
                int temp = n[i] * 2;
                if (temp < 10) {
                    even += temp;
                } else {
                    temp = temp - 9;
                    even += temp;
                }
            } else {
                odd += n[i];
            }
        }

        int total = even + odd;
        return total % 10 == 0;
    }
}
