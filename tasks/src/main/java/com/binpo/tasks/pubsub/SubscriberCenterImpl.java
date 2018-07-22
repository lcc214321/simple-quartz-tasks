package com.binpo.tasks.pubsub;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.binpo.tasks.model.ExecuteParams;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 订阅控制中心
 * 
 * @author zhang
 *
 */
public class SubscriberCenterImpl implements SubscriberCenter {
    private List<Subscriber> jedisPubSubs = new ArrayList<>();
    private JedisPool jedisPool;
    private String pubSubHost;
    private Integer pubSubPort;
    private String pubSubPassword;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public SubscriberCenterImpl() {

    }

    public SubscriberCenterImpl(String pubSubHost, Integer pubSubPort) {
        this.pubSubHost = pubSubHost;
        this.pubSubPort = pubSubPort;
        initJedisPool();
    }

    public SubscriberCenterImpl(String pubSubHost, Integer pubSubPort, String pubSubPassword) {
        this.pubSubHost = pubSubHost;
        this.pubSubPort = pubSubPort;
        this.pubSubPassword = pubSubPassword;
        initJedisPool();
    }

    @Override
    public void cancelSubscriber(String channel) {
        if (jedisPool == null)
            initJedisPool();
        for (Subscriber subscriber : this.jedisPubSubs) {
            if (subscriber.getChannelName().equals(channel)) {
                this.jedisPubSubs.remove(subscriber);
            }
        }
    }

    @Override
    public void addSubscriber(Subscriber subscriber, String channel) {
        if (jedisPool == null)
            initJedisPool();
        threadPool.execute(new AddSubscriberThread(subscriber, channel));
    }

    public void setPubSubHost(String pubSubHost) {
        this.pubSubHost = pubSubHost;
    }

    public void setPubSubPort(Integer pubSubPort) {
        this.pubSubPort = pubSubPort;
    }

    private void initJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(20);
        poolConfig.setMaxTotal(200);
        if ("".equals(this.pubSubPassword) || this.pubSubPassword == null) {
            jedisPool = new JedisPool(poolConfig, this.pubSubHost, this.pubSubPort);
        } else {
            jedisPool = new JedisPool(poolConfig, this.pubSubHost, this.pubSubPort, 2000,
                    this.pubSubPassword);
        }
    }

    public List<Subscriber> getJedisPubSubs() {
        return jedisPubSubs;
    }

    public void setJedisPubSubs(List<Subscriber> jedisPubSubs) {
        this.jedisPubSubs = jedisPubSubs;
        initJedisPool();
        this.jedisPubSubs = jedisPubSubs;

        for (Subscriber pubsub : this.jedisPubSubs) {
            threadPool.execute(new AddSubscriberThread(pubsub, pubsub.getChannelName()));
        }
    }

    private class AddSubscriberThread implements Runnable {
        private Subscriber subscriber;
        private String channel;

        public AddSubscriberThread(Subscriber subscriber, String channel) {
            this.channel = channel;
            this.subscriber = subscriber;
        }

        @Override
        public void run() {
            jedisPool.getResource().subscribe(subscriber, channel);
        }

    }

    @Override
    public Long heartbeat(String channel, String tag) {
        if (jedisPool == null)
            initJedisPool();
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "heartbeat");
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.getParams().put("uuid", tag);
        params.put("executeParams", JSON.toJSONString(executeParams));
        Jedis resource = jedisPool.getResource();
        Long publish = resource.publish(channel, JSON.toJSONString(params));

        jedisPool.returnResourceObject(resource);
        return publish;
    }

    public void setPubSubPassword(String pubSubPassword) {
        this.pubSubPassword = pubSubPassword;
    }

}
