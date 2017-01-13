package com.yy.msdkquality;

import android.text.TextUtils;
import android.util.Log;

import com.duowan.yy.sysop.yctoken.YCToken;
import com.duowan.yy.sysop.yctoken.YCTokenAppSecretProvider;
import com.duowan.yy.sysop.yctoken.YCTokenBuilder;
import com.duowan.yy.sysop.yctoken.YCTokenPropertyProvider;
import com.duowan.yy.sysop.yctoken.model.ExtendProperty;

import org.apache.commons.codec.binary.Base64New;
import org.joou.UInteger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/5/12 10:32
 * Time: 10:32
 * Description:Builder
 */
public class SaltonTokenBuilder {

    private YCTokenBuilder mTokenBuilder;
    private int mAppKey;     //appKey
    private String mAppSecret;      //AppSecret
    private long mExpirationTime;        //过期时间
    private YCTokenPropertyProvider mYCTokenPropertyProvider;
    private short mAppVersion;

    /**
     * @param p_AppKey         由欢聚云后台创建的应用提供的AppKey
     * @param p_AppSecret      由欢聚云后台创建的应用提供的AppSecret
     * @param p_ExpirationTime 生成的Token有效期，可以使用UpdateToken来更新
     */
    public SaltonTokenBuilder(int p_AppKey, final String p_AppSecret, short p_AppVersion, long p_ExpirationTime) {
        this.mAppKey = p_AppKey;
        this.mAppSecret = p_AppSecret;
        this.mAppVersion = p_AppVersion;
        this.mExpirationTime = p_ExpirationTime;
    }

    public SaltonTokenBuilder setAppKey(int p_AppKey) {
        mAppKey = p_AppKey;
        return this;
    }

    public int getAppKey() {
        return mAppKey;
    }

    public SaltonTokenBuilder setAppSecret(String p_AppSecret) {
        mAppSecret = p_AppSecret;
        return this;
    }

    public String getAppSecret() {
        return mAppSecret;
    }

    public long getExpirationTime() {
        return mExpirationTime;
    }

    public SaltonTokenBuilder setExpirationTime(long p_ExpirationTime) {
        this.mExpirationTime = p_ExpirationTime;
        return this;
    }

    public SaltonTokenBuilder addTokenExtendProperty(String key, String value) {
        if (this.mYCTokenPropertyProvider == null) {
            mYCTokenPropertyProvider = new YCTokenPropertyProvider(mAppKey, mExpirationTime);
        }
        this.mYCTokenPropertyProvider.addTokenExtendProperty(key, value);
        return this;
    }

    public SaltonTokenBuilder addTokenExtendProperty(String key, UInteger propValue) {
        if (this.mYCTokenPropertyProvider == null) {
            mYCTokenPropertyProvider = new YCTokenPropertyProvider(mAppKey, mExpirationTime);
        }
        this.mYCTokenPropertyProvider.addTokenExtendProperty(key, propValue);
        return this;
    }


    /**
     * 注意要把
     *
     * @param p_optype
     * @return
     */
    public SaltonTokenBuilder setOpType(String p_optype) {
        this.mYCTokenPropertyProvider.addTokenExtendProperty("OPTYPE", p_optype);
        return this;
    }

    /**
     * 返回所有的Token额外配置参数
     *
     * @return
     */
    public List<ExtendProperty> getAllExtendProperties() {
        List<ExtendProperty> properties = this.mYCTokenPropertyProvider.populateExtendProperties();
        return properties;
    }

    /**
     * 构造秘钥
     *
     * @return
     */
    public byte[] build() {
        if (mAppKey < 0 || TextUtils.isEmpty(mAppSecret) | mExpirationTime < 0) {//需要保证参数不为空
            throw new RuntimeException("App Secret is null, token initial interrupt");
        } else {
            mTokenBuilder = new YCTokenBuilder(new YCTokenAppSecretProvider() {
                public Map<Short, String> getAppsecret(int appKey) {
                    Map<Short, String> m = new HashMap<Short, String>();
                    // 传入密钥版本和密码
                    m.put(mAppVersion, mAppSecret);
                    return m;
                }
            });
            //设置AppKey和token有效期，有效期为UTC nonce
//            mYCTokenPropertyProvider = new YCTokenPropertyProvider(mAppKey, mExpirationTime);
        }
        try {
            Base64New coder = new Base64New(76, new byte[]{}, true);
            //取得二进制Token比特数组
            byte[] buildBinanyToken = mTokenBuilder.buildBinanyToken(mYCTokenPropertyProvider);
            //加密后的二进制Token比特数组
            byte[] encode = coder.encode(buildBinanyToken);
            //加密后的二进制Token字符串
            String tokenStr = coder.encodeAsString(buildBinanyToken); // 打印token
//            System.out.println("tokenStr:" + tokenStr);
            //解密后的二进制Token
            byte[] decodeBytes = coder.decode(tokenStr);
            //校验Token是否有效,如果校验不通过则会报异常，不会返回结果
            YCToken mYCToken = mTokenBuilder.validateTokenBytes(decodeBytes);
            Log.e("TokenGenerator", "校验通过");
            return decodeBytes;
        } catch (Exception e) {
            Log.e("build token error", e.getMessage());
            return null;
        }
    }

}
