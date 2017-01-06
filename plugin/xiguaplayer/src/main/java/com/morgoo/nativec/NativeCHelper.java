package com.morgoo.nativec;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/5 16:44
 * Time: 16:44
 * Description:
 */
public class NativeCHelper {
    private final native static int nativePing();

    static {
        System.loadLibrary("Test");
    }
    public final static int ping() {
        return nativePing();
    }
}
