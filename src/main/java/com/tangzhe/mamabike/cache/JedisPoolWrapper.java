package com.tangzhe.mamabike.cache;

import com.tangzhe.mamabike.common.constants.Parameters;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/11.
 */
@Component
public class JedisPoolWrapper {

    private JedisPool jedisPool = null;

    private Logger log = LoggerFactory.getLogger(JedisPoolWrapper.class);

    @Autowired
    private Parameters parameters;

    @PostConstruct
    public void init() throws MaMaBikeException {
        try {
            JedisPoolConfig config  = new JedisPoolConfig();
            config.setMaxWaitMillis(parameters.getRedisMaxWaitMillis());
            config.setMaxIdle(parameters.getRedisMaxIdle());
            config.setMaxTotal(parameters.getRedisMaxTotal());
            jedisPool = new JedisPool(config,parameters.getRedisHost(),parameters.getRedisPort(),2000);
        } catch (Exception e) {
           log.error("Fail to initialize redis pool", e);
           throw new MaMaBikeException("初始化redis失败");
        }

    }

    public JedisPool getJedisPool(){
        return jedisPool;
    }

}
