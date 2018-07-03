package com.tangzhe.mamabike.common.rest;

import com.tangzhe.mamabike.cache.CommonCacheUtil;
import com.tangzhe.mamabike.common.constants.Constants;
import com.tangzhe.mamabike.user.entity.UserElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by tangzhe 2017/09/11.
 */
public class BaseController {

    private Logger log = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private CommonCacheUtil cacheUtil;

    protected UserElement getCurrentUser(){

        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);
        if(!StringUtils.isBlank(token)){
            try {
                UserElement ue = cacheUtil.getUserByToken(token);
                return ue;
            }catch (Exception e){
                log.error("fail to get user by token", e);
                throw e;
            }
        }
       return  null;
    }

    protected String getIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }

}
