package com.tangzhe.mamabike.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangzhe.mamabike.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * Created by tangzhe 2017/9/11.
 * 消息发送者，同时又是接受者，由于是单机测试，所以这样做
 */
@Component("smsProcessor")
public class SmsProcessor {

    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    @Autowired
    @Qualifier("verCodeService")
    private SmsSender smsSender;

    /**
     * 发送消息队列
     */
    public void sendSmsToQueue(Destination destination, final String message){
        jmsTemplate.convertAndSend(destination, message);
    }

    /**
     * 模拟接收消息队列
     */
    @JmsListener(destination="sms.queue")
    public void doSendSmsMessage(String text){
        JSONObject jsonObject = JSON.parseObject(text);
        smsSender.sendSms(jsonObject.getString("mobile"),jsonObject.getString("tplId"),jsonObject.getString("vercode"));
    }

}
