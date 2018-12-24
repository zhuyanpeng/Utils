package com.study.www;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 哨兵机制下的连接
 * @author : THINK.zhuyanpeng
 **/
public class RedisSentinelUtils {

    private static JedisSentinelPool sentinelPool=null;
    private static String redisHosts="192.168.44.11:2639;192.168.44.12:2639;192.168.44.13:2639";
    private static String redisMaster="mymaster";
    private static String password="123456";

    //最大空闲数
    private static final Integer MAX_IDLE=200;
    //最大连接数
    private static final Integer MAX_TOTAL=400;
    // 最小空闲数
    private static final Integer MIN_TOTAL=200;

    static{
        // redis 连接池的相关配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(MAX_IDLE);
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setMinIdle(MIN_TOTAL);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        HashSet<String> host = new HashSet<String>(Arrays.asList(redisHosts.split(";")));
        if (StringUtils.isBlank(password)){
            sentinelPool = new JedisSentinelPool(redisMaster, host, poolConfig);
        }else{
            sentinelPool = new JedisSentinelPool(redisMaster, host, poolConfig,password);
        }
    }

    public String get(String key)throws JedisConnectionException{
        Jedis jedis = sentinelPool.getResource();
        try {
            return jedis.get(key);
        } catch (JedisConnectionException e) {
            throw  e;
        }finally {
            jedis.close();
        }
    }

}
