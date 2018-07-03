package com.tangzhe.mamabike.common.resp;

import com.tangzhe.mamabike.common.constants.Constants;

/**
 * 返回给安卓端的数据类型
 */
public class ApiResult <T> {

    private int code = Constants.RESP_STATUS_OK;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
