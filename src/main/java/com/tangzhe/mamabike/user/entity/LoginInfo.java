package com.tangzhe.mamabike.user.entity;

/**
 * Created by tangzhe 2017/9/9.
 */
public class LoginInfo {

    //登录信息密文
    private String data;

    //RSA加密的AES的密钥
    private String key;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
