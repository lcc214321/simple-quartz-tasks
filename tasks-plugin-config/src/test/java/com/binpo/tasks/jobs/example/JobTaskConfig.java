package com.binpo.tasks.jobs.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * 初始化需要加载到spring中的任务
 *
 * @author zhang 2018年7月21日 下午9:21:05
 */
@PropertySource(value = "classpath:application.properties")
@ComponentScan(basePackages = "com.binpo.tasks")
public class JobTaskConfig {

    /**
     * 
     * 加载任务
     * 
     * @return
     */
    @Bean
    public TestTran testTran() {
        return new TestTran();
    }

    /**
     * 
     * 加载任务2
     * 
     * @return
     */
    @Bean
    public TestTran2 testTran2() {
        return new TestTran2();
    }
}
