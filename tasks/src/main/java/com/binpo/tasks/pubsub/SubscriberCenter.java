package com.binpo.tasks.pubsub;

public interface SubscriberCenter {
    /**
     * 取消订阅
     * 
     * @param channel 订阅的名称
     * @author zhang
     * @date 2016年4月29日下午5:01:02
     * @version 0.01
     * @return void
     */
    void cancelSubscriber(String channel);

    /**
     * 添加订阅
     * 
     * @param jedisPubSub 订阅的实现
     * @param channel
     * @author zhang
     * @date 2016年4月29日下午5:02:12
     * @version 0.01
     * @return void
     */
    void addSubscriber(Subscriber subscriber, String channel);

    /**
     * 
     * 检测心跳
     * 
     * @param channel 订阅地址
     * @param tag 请求标识
     * @return
     */
    Long heartbeat(String channel,String tag);

}
