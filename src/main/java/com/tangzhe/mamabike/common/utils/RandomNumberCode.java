package com.tangzhe.mamabike.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * Created by tangzhe 2017/9/11.
 */
public class RandomNumberCode {

    /**
     * 4位随机验证码
     */
    public static String verCoder() {
        Random random = new Random();
        return StringUtils.substring(String.valueOf(random.nextInt()),2,6);
    }

    /**
     * 随机单车订单号
     */
    public static String randomNo() {
        Random random = new Random();
        return String.valueOf(Math.abs(random.nextInt()*-10));
    }

}
