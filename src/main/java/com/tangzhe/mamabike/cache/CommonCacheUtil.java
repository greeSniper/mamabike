package com.tangzhe.mamabike.cache;

import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.user.entity.UserElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * Created by tangzhe 2017/09/10.
 */
@Component
public class CommonCacheUtil {

    private Logger log = LoggerFactory.getLogger(CommonCacheUtil.class);

    @Autowired
    private JedisPoolWrapper jedisPoolWrapper;

    private static final String TOKEN_PREFIX = "token.";

    private static final String USER_PREFIX = "user.";

    /**
     * 缓存 可以value 永久
     * @param key
     * @param value
     */
    public void cache(String key, String value) {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);//选择redis第0片区
                    Jedis.set(key, value);
                }
            }
        } catch (Exception e) {
            log.error("Fail to cache value", e);
        }
    }


    /**
     * 获取缓存key
     * @param key
     * @return
     */
    public String getCacheValue(String key) {
        String value = null;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);
                    value = Jedis.get(key);
                }
            }
        } catch (Exception e) {
            log.error("Fail to get cached value", e);
        }
        return value;
    }

    /**
     * 设置key value 以及过期时间
     * @param key
     * @param value
     * @param expiry
     * @return
     */
    public long cacheNxExpire(String key, String value, int expiry) {
        long result = 0;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    result = jedis.setnx(key, value);
                    jedis.expire(key, expiry);
                }
            }
        } catch (Exception e) {
            log.error("Fail to cacheNx value", e);
        }

        return result;
    }

    /**
     * 删除缓存key
     * @param key
     */
    public void delKey(String key) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    jedis.del(key);
                } catch (Exception e) {
                    log.error("Fail to remove key from redis", e);
                }
            }catch (Exception e) {
                log.error("Fail to remove key from redis", e);
            }
        }
    }


    /**
     * 登录时设置token
     * @param ue
     */
    public void putTokenWhenLogin(UserElement ue) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                Transaction trans = jedis.multi();
                try {
                    trans.del(TOKEN_PREFIX + ue.getToken());
                    trans.hmset(TOKEN_PREFIX + ue.getToken(), ue.toMap());
                    trans.expire(TOKEN_PREFIX + ue.getToken(), 2592000);
                    trans.sadd(USER_PREFIX + ue.getUserId(), ue.getToken());
                    trans.exec();
                } catch (Exception e) {
                    trans.discard();
                    log.error("Fail to cache token to redis", e);
                }
            }
        }
    }


    public UserElement getUserByToken(String token) {
        UserElement ue = null;
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {
            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    Map<String,String> map = jedis.hgetAll(TOKEN_PREFIX+token);
                    if(!CollectionUtils.isEmpty(map)){
                         ue=UserElement.fromMap(map);
                    }else {
                        log.warn("fail to find cached element for token");
                    }
                } catch (Exception e) {
                    log.error("Fail to get user by  token in redis", e);
                    throw e;
                }
            }
        }
        return ue;
    }

    /**
     * 缓存手机验证码专用 限制了发送次数
     * @return  1 当前验证码未过期   2 手机号超过当日验证码次数上限  3 ip超过当日验证码次数上线
     */
    public int cacheForVerificationCode(String key, String verCode, String type, int second, String ip) throws MaMaBikeException{
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {
            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    String ipKey = "ip."+ip;
                    if(ip==null){
                        return 3;
                    }else {
                        String ipSendCount = jedis.get(ipKey);
                        try {
                            if (ipSendCount != null && Integer.parseInt(ipSendCount) >= 10) {
                                return 3;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Fail to process ip send count", e);
                            return 3;
                        }
                        long succ = jedis.setnx(key, verCode);
                        if (succ == 0) {
                            return 1;
                        }
                        String sendCount = jedis.get(key + "." + type);
                        try {
                            if (sendCount != null && Integer.parseInt(sendCount) >= 10) {
                                jedis.del(key);
                                return 2;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Fail to process send count", e);
                            jedis.del(key);
                            return 2;
                        }
                        try {
                            jedis.expire(key, second);
                            long val = jedis.incr(key + "." + type);
                            if (val == 1) {
                                jedis.expire(key + "." + type, 86400);
                            }
                            jedis.incr(ipKey);
                            if (val == 1) {
                                jedis.expire(ipKey, 86400);
                            }
                        } catch (Exception e) {
                            log.error("Fail to cache data into redis", e);
                        }
                    }
                } catch (Exception e) {
                    log.error("Fail to set vercode to redis", e);
                    throw e;
                }
            }catch (Exception e) {
                log.error("Fail to cache for expiry", e);
                throw new MaMaBikeException("Fail to cache for expiry");
            }
        }
        return 0;

    }

}
