package com.binpo.tasks.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis 对Key的锁
 * 
 * @author zhang
 *
 */
public class ReidsTaskRunLock implements TaskRunLock {
    private JedisPool jedisPool;
    private String host;
    private Integer port;
    private String password;

    public ReidsTaskRunLock(String host, String port) {
        this.host = host;
        this.port = Integer.valueOf(port);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(20);
        poolConfig.setMaxTotal(200);
        jedisPool = new JedisPool(poolConfig, this.host, this.port);
    }

    public ReidsTaskRunLock(String host, String port, String password) {
        this.host = host;
        this.port = Integer.valueOf(port);
        this.password = password;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(20);
        poolConfig.setMaxTotal(200);
        if ("".equals(this.password) || this.password == null) {
            jedisPool = new JedisPool(poolConfig, this.host, this.port);
        } else {
            jedisPool = new JedisPool(poolConfig, this.host, this.port, 2000, this.password);
        }
    }

    @Override
    public boolean isLock(String key) {
        Jedis resource = jedisPool.getResource();
        Long setnx = resource.setnx(key, "1");
        jedisPool.returnResourceObject(resource);
        if (setnx.equals(1L)) {
            resource = jedisPool.getResource();
            resource.expire(key, 300);
            jedisPool.returnResourceObject(resource);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void releaseLock(String key) {
        Jedis resource = jedisPool.getResource();
        resource.del(key);
        jedisPool.returnResourceObject(resource);
    }

}
