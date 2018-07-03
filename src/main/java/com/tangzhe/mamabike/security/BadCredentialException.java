package com.tangzhe.mamabike.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by tangzhe 2017/09/11.
 * 自定义异常类，用于用户授权
 */
public class BadCredentialException extends AuthenticationException {

    public BadCredentialException(String msg) {
        super(msg);
    }

}
