package com.tangzhe.mamabike.sms;

/**
 * Created by tangzhe 2017/9/11.
 */
public interface SmsSender {

    void sendSms(String phone, String tplId, String params);

}
