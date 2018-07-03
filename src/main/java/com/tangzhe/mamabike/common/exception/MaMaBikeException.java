package com.tangzhe.mamabike.common.exception;

import com.tangzhe.mamabike.common.constants.Constants;

/**
 * Created by tangzhe 2017/9/9.
 */
public class MaMaBikeException extends Exception {

    public MaMaBikeException(String message) {
        super(message);
    }

    public  int getStatusCode() {
        return Constants.RESP_STATUS_INTERNAL_ERROR;
    }

}
