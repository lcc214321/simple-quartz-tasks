package com.github.zhangyinhao1234.plugin.tasks_server_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import com.binpo.tasks.jobs.example.JobTaskConfig;

@SpringBootApplication
@ComponentScan("com.binpo")
public class TaskMain {

    public static void main(String[] args) {
        SpringApplication.run(TaskMain.class, args);
    }
}
