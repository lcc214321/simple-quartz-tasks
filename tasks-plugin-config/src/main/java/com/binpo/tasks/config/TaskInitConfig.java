package com.binpo.tasks.config;

import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.binpo.tasks.JobManagePubSub;
import com.binpo.tasks.ScheduleJobsOperate;
import com.binpo.tasks.TaskManager;
import com.binpo.tasks.lock.ReidsTaskRunLock;
import com.binpo.tasks.lock.TaskRunLock;
import com.binpo.tasks.pubsub.SubscriberCenterImpl;

/**
 * 
 * 任务框架初始化，spring 4
 *
 * @author zhang 2018年7月18日 下午11:40:22
 */
@Component
public class TaskInitConfig {

    /**
     * 数据库地址
     */
    @Value("${task.datasource.url:}")
    private String url;
    /**
     * 数据库账号
     */
    @Value("${task.datasource.username:}")
    private String username;
    /**
     * 数据库密码
     */
    @Value("${task.datasource.password:}")
    private String password;
    /**
     * 使用的数据库驱动
     */
    @Value("${task.datasource.driverClassName:}")
    private String driverClassName;
    /**
     * 最大活动数量
     */
    @Value("${task.datasource.maxActive:10}")
    private String maxActive;

    /**
     * 初始值
     */
    @Value("${task.datasource.initialSize:1}")
    private String initialSize;

    /**
     * 消息订阅：redis的地址
     */
    @Value("${task.pubsub.redis.host:127.0.0.1}")
    private String pubSubHost;

    /**
     * 消息订阅，redis的端口
     */
    @Value("${task.pubsub.redis.port:6379}")
    private String pubSubPort;

    /**
     * 消息订阅，redis的密码
     */
    @Value("${task.pubsub.redis.pw:}")
    private String pubSubPassword;

    @Bean
    public DruidDataSource dataSource() throws SQLException {
        if (StringUtils.isBlank(url))
            return null;
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setMaxActive(Integer.valueOf(maxActive));
        dataSource.setInitialSize(Integer.valueOf(initialSize));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DruidDataSource dataSource) {
        if (dataSource == null)
            return null;
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DruidDataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 
     * 默认的任务管理器，订阅了默认的频道 jobManageAction
     * 
     * @return
     */
    @Bean
    public JobManagePubSub jobManagePubSub(TaskManager taskManager, ScheduleJobsOperate scheduleJobsOperate,
            TaskRunLock lock) {
        JobManagePubSub jobManagePubSub = new JobManagePubSub();
        jobManagePubSub.setLock(lock);
        jobManagePubSub.setTaskManager(taskManager);
        jobManagePubSub.setScheduleJobsOperate(scheduleJobsOperate);
        jobManagePubSub.setChannelName("jobManageAction");
        return jobManagePubSub;
    }

    /**
     * 
     * 初始化订阅中心
     * 
     * @return
     */
    @Bean
    public SubscriberCenterImpl subscriberCenter(JobManagePubSub jobManagePubSub) {
        SubscriberCenterImpl subscriberCenter = new SubscriberCenterImpl();
        subscriberCenter.setPubSubHost(pubSubHost);
        subscriberCenter.setPubSubPort(Integer.valueOf(pubSubPort));
        if (StringUtils.isNotBlank(pubSubPassword)) {
            subscriberCenter.setPubSubPassword(pubSubPassword);
        }
        subscriberCenter.setJedisPubSubs(Stream.of(jobManagePubSub).collect(Collectors.toList()));
        return subscriberCenter;
    }

    @Bean
    public TaskRunLock lock() {
        return new ReidsTaskRunLock(pubSubHost, pubSubPort, pubSubPassword);
    }
}
