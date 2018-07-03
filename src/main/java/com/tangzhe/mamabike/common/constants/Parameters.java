package com.tangzhe.mamabike.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tangzhe 2017/09/10.
 */
@Component
public class Parameters {

    /*****redis config start*******/
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;
    @Value("${redis.max-idle}")
    private int redisMaxTotal;
    @Value("${redis.max-total}")
    private int redisMaxIdle;
    @Value("${redis.max-wait-millis}")
    private int redisMaxWaitMillis;
    /*****redis config end*******/

    @Value("#{'${security.noneSecurityPath}'.split(',')}")
    private List<String> noneSecurityPath;

    public List<String> getNoneSecurityPath() {
        return noneSecurityPath;
    }

    public void setNoneSecurityPath(List<String> noneSecurityPath) {
        this.noneSecurityPath = noneSecurityPath;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public int getRedisMaxTotal() {
        return redisMaxTotal;
    }

    public void setRedisMaxTotal(int redisMaxTotal) {
        this.redisMaxTotal = redisMaxTotal;
    }

    public int getRedisMaxIdle() {
        return redisMaxIdle;
    }

    public void setRedisMaxIdle(int redisMaxIdle) {
        this.redisMaxIdle = redisMaxIdle;
    }

    public int getRedisMaxWaitMillis() {
        return redisMaxWaitMillis;
    }

    public void setRedisMaxWaitMillis(int redisMaxWaitMillis) {
        this.redisMaxWaitMillis = redisMaxWaitMillis;
    }

}
