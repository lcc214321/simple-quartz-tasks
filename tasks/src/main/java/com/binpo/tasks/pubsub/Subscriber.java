package com.binpo.tasks.pubsub;

import redis.clients.jedis.JedisPubSub;

public abstract class Subscriber extends JedisPubSub {
    public abstract String getChannelName();

    public abstract String getLastRunUuid();

    public abstract void onMessage(String channel, String message);

    public abstract void onPMessage(String pattern, String channel, String message);

    public abstract void onSubscribe(String channel, int subscribedChannels);

    public abstract void onUnsubscribe(String channel, int subscribedChannels);

    public abstract void onPUnsubscribe(String pattern, int subscribedChannels);

    public abstract void onPSubscribe(String pattern, int subscribedChannels);
}
