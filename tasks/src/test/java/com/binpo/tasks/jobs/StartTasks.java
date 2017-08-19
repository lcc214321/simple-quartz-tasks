package com.binpo.tasks.jobs;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.binpo.tasks.TaskManager;
import com.binpo.tasks.model.ScheduleJob;
/**
 * spring中任务框架配置例子
 * @author zhang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jobs.xml")  
public class StartTasks {
	public StartTasks(){
		System.out.println("create new ");
	}
	@Autowired
	private TaskManager taskManager;
	@Test
	public void run() throws IOException, SchedulerException{
		List<ScheduleJob> runningJob = taskManager.getRunningJob();
		System.out.println(JSON.toJSONString(runningJob));
		System.in.read();
	}
}
