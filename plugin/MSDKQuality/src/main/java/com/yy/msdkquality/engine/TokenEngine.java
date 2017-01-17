package com.yy.msdkquality.engine;

import android.util.Base64;
import android.util.Log;

import com.duowan.yy.sysop.yctoken.YCToken;
import com.duowan.yy.sysop.yctoken.YCTokenAppSecretProvider;
import com.duowan.yy.sysop.yctoken.YCTokenBuilder;
import com.duowan.yy.sysop.yctoken.YCTokenPropertyProvider;
import com.orhanobut.logger.Logger;
import com.yy.msdkquality.SaltonTokenBuilder;
import com.yy.msdkquality.YConfig;

import org.apache.commons.codec.binary.Base64New;
import org.joou.Unsigned;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/10 11:28
 * Time: 11:28
 * Description:
 */
public class TokenEngine {


    //    private static final int mAppKey = YConfig.mAppKey;
//        private static final String mAppSecret = YConfig.mAppSecret;
    public static final long TOKEN_EXPIRED_TIME = 60 * 60 * 24; //seconds

//    //这个是可以使用的
//    public static byte[] genToken(int uid, int sid, int auth) {
//        long currentTime = (System.currentTimeMillis() / 1000);
//        long expireTime = currentTime + TOKEN_EXPIRED_TIME;
//            SaltonTokenBuilder _TokenBuilder = new SaltonTokenBuilder(YConfig.mAppKey, YConfig.mAppSecret, expireTime);
//            _TokenBuilder
//                    .addTokenExtendProperty("UID", Unsigned.uint(uid))
//                    .addTokenExtendProperty("SID", Unsigned.uint(sid))
//                    .addTokenExtendProperty("AUDIO_RECV",
//                            Unsigned.uint(expireTime))
//                    .addTokenExtendProperty("AUDIO_SEND",
//                            Unsigned.uint(expireTime))
//                    .addTokenExtendProperty("VIDEO_RECV",
//                            Unsigned.uint(expireTime))
//                    .addTokenExtendProperty("VIDEO_SEND",
//                            Unsigned.uint(expireTime))
//                    .addTokenExtendProperty("TEXT_RECV",
//                            Unsigned.uint(expireTime))
//                    .addTokenExtendProperty("AUTH", Unsigned.uint(auth))
//                    .addTokenExtendProperty("OPTYPE", "1")
//                    .addTokenExtendProperty("TEXT_SEND",
//                            Unsigned.uint(expireTime));
//            return _TokenBuilder.build();
//    }
//
//    public static byte[] genToken(int uid, int sid, int au) {
//        long currentTime = (System.currentTimeMillis() / 1000);
//        long tokenExpire = currentTime + TOKEN_EXPIRED_TIME;
//        SaltonTokenBuilder _TokenBuilder = new SaltonTokenBuilder(mAppKey, mAppSecret, tokenExpire);
//        _TokenBuilder.
//                addTokenExtendProperty("UID", Unsigned.uint(uid)).
////					addTokenExtendProperty("CONTEXT", context).
////					addTokenExtendProperty("AUTH", Unsigned.uint(auth)).
//        addTokenExtendProperty("SID", Unsigned.uint(sid)).
//
////					addTokenExtendProperty("tokenType", tokentype).
//
//        addTokenExtendProperty("AUDIO_RECV", Unsigned.uint(tokenExpire)).
//                addTokenExtendProperty("AUDIO_SEND", Unsigned.uint(tokenExpire)).
//                addTokenExtendProperty("VIDEO_RECV", Unsigned.uint(tokenExpire)).
//                addTokenExtendProperty("VIDEO_SEND", Unsigned.uint(tokenExpire)).
//                addTokenExtendProperty("TEXT_RECV", Unsigned.uint(tokenExpire)).
//        addTokenExtendProperty("AUTH", Unsigned.uint(3)).
//                addTokenExtendProperty("OPTYPE", "1").
//                addTokenExtendProperty("TEXT_SEND", Unsigned.uint(tokenExpire));
//
//        return _TokenBuilder.build();
//    }

    /**
     * 获取Token
     *
     * @param uid  用户标识  u32int
     * @param sid  频道标识   u32int
     * @param auth 权限标识    u32int 值可以是 读:1 写:2 读写:3
     * @return
     */
    public static byte[] getLoginToken(int uid, int sid, int auth) {
        long expireTime = (new Date().getTime() + 12000) / 1000;
        Logger.e("time:"+new Date().getTime()+"\n"+expireTime);
        SaltonTokenBuilder saltonTokenBuilder = new SaltonTokenBuilder(YConfig.mAppKey, YConfig.mAppSecret,YConfig.mAppVersion, expireTime);
        byte[] httpToken = saltonTokenBuilder
                .addTokenExtendProperty("UID", Unsigned.uint(uid))
                .addTokenExtendProperty("SID", Unsigned.uint(sid))
                .addTokenExtendProperty("AUDIO_RECV",
                        Unsigned.uint(expireTime))
                .addTokenExtendProperty("AUDIO_SEND",
                        Unsigned.uint(expireTime))
                .addTokenExtendProperty("VIDEO_RECV",
                        Unsigned.uint(expireTime))
                .addTokenExtendProperty("VIDEO_SEND",
                        Unsigned.uint(expireTime))
                .addTokenExtendProperty("TEXT_RECV",
                        Unsigned.uint(expireTime))
                .addTokenExtendProperty("AUTH", Unsigned.uint(auth))
                .addTokenExtendProperty("OPTYPE", "1")
                .addTokenExtendProperty("TEXT_SEND",
                        Unsigned.uint(expireTime))
                .build();
        String tokenHex = Base64.encodeToString(httpToken, Base64.URL_SAFE | Base64.NO_WRAP);
//        System.out.println(tokenHex);
        Logger.e(tokenHex);
        return httpToken;
    }

    public static byte[] genToken(int uid, int sid, int au) {
        long tokenExpire = (new Date().getTime() + 1200000) / 1000;
//        String context = String.format("{\"bucket\":\"%s\",\"filename\":\"%s\"}", "hamburgbucket01", "hello.mp3");
        SaltonTokenBuilder builder = new SaltonTokenBuilder(YConfig.mAppKey, YConfig.mAppSecret,YConfig.mAppVersion, tokenExpire);
        builder.addTokenExtendProperty("UID", Unsigned.uint(uid)).
//                addTokenExtendProperty("CONTEXT", context).
                addTokenExtendProperty("AUTH", Unsigned.uint(au)).
                addTokenExtendProperty("SID", Unsigned.uint(sid)).
                addTokenExtendProperty("AUDIO_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("AUDIO_SEND", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("VIDEO_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("VIDEO_SEND", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("TEXT_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("TEXT_SEND", Unsigned.uint(tokenExpire));
        return builder.build();
    }

    //可用
    public static byte[] genToken2(int uid, int sid, int au) {
        long currentTime = (System.currentTimeMillis() / 1000);
        long tokenExpire = currentTime + TOKEN_EXPIRED_TIME;
        // 构见builder
        YCTokenBuilder tokenBuilder = new YCTokenBuilder(new YCTokenAppSecretProvider() {

            public Map<Short, String> getAppsecret(int appKey) {

                Map<Short, String> m = new HashMap<Short, String>();
                // 传入密钥版本和密码
                m.put(YConfig.mAppVersion, YConfig.mAppSecret);
                return m;
            }
        });

        // 传入appkey和token有效期
        YCTokenPropertyProvider propProvider = new YCTokenPropertyProvider(YConfig.mAppKey, tokenExpire);

        // 传入业务需要的具体参数，具体参数以业务为准
        propProvider.addTokenExtendProperty("UID", Unsigned.uint(uid)).
//					addTokenExtendProperty("CONTEXT", context).
//					addTokenExtendProperty("AUTH", Unsigned.uint(auth)).
        addTokenExtendProperty("SID", Unsigned.uint(sid)).

//					addTokenExtendProperty("tokenType", tokentype).

        addTokenExtendProperty("AUDIO_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("AUDIO_SEND", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("VIDEO_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("VIDEO_SEND", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("TEXT_RECV", Unsigned.uint(tokenExpire)).
                addTokenExtendProperty("TEXT_SEND", Unsigned.uint(tokenExpire));

//        Base64New coder = new Base64New(76, new byte[]{}, true);
//        byte[] buildBinanyToken = tokenBuilder.buildBinanyToken(propProvider);
//        String token = coder.encodeAsString(buildBinanyToken); // 打印token
//        return buildBinanyToken;
        try {
            Base64New coder = new Base64New(76, new byte[]{}, true);
            //取得二进制Token比特数组
            byte[] buildBinanyToken = tokenBuilder.buildBinanyToken(propProvider);
            //加密后的二进制Token比特数组
            byte[] encode = coder.encode(buildBinanyToken);
            //加密后的二进制Token字符串
            String tokenStr = coder.encodeAsString(buildBinanyToken); // 打印token
//            System.out.println("tokenStr:" + tokenStr);
            //解密后的二进制Token
            byte[] decodeBytes = coder.decode(tokenStr);
            //校验Token是否有效,如果校验不通过则会报异常，不会返回结果
            YCToken mYCToken = tokenBuilder.validateTokenBytes(decodeBytes);
            Log.e("TokenGenerator", "校验通过");
            return decodeBytes;
        } catch (Exception e) {
            Log.e("build token error", e.getMessage());
            return null;
        }
    }

}
