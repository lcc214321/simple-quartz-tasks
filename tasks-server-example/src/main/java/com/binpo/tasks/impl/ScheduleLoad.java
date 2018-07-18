package com.binpo.tasks.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.binpo.tasks.ScheduleJobsOperate;
import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.ScheduleJob;
/**
 * 
 * 实现数据操作接口，加载任务信息等到任务框架
 *
 * @author zhang 2017年8月19日 下午1:33:46
 */
@Component
public class ScheduleLoad implements ScheduleJobsOperate{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	public ScheduleJob getScheduleJob(Long jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ScheduleJob> loadJobs() {
		List<ScheduleJob> scheduleJobs=new ArrayList<ScheduleJob>();
		
		List<Map<String, Object>> list = 
		        jdbc.queryForList("select * from jobs_schedule", new HashMap<String, Object>());
		for(Map<String, Object> map : list){
		    ScheduleJob scheduleJob=new ScheduleJob();
		    scheduleJob.setCronExpression(map.get("cronExpression").toString());
		    scheduleJob.setDescription(map.get("description").toString());
		    scheduleJob.setDistributedRuntime(false);
		    scheduleJob.setId((Long)map.get("id"));
		    scheduleJob.setJobDescription(map.get("description").toString());
		    scheduleJob.setJobGroup(map.get("jobGroup").toString());
		    scheduleJob.setJobName(map.get("jobName").toString());
		    scheduleJob.setJobStatus(map.get("jobStatus").toString());
		    scheduleJob.setMethodName(map.get("methodName").toString());
		    scheduleJob.setSpringId(map.get("springId").toString());
		    scheduleJobs.add(scheduleJob);
		}
		logger.debug("数据库加载任务："+JSON.toJSONString(scheduleJobs));
		
		return scheduleJobs;
	}

	public void saveSuccessLog(Long jobId, ExecuteParams runParams, String log) {
		// TODO Auto-generated method stub
		
	}

	public void saveErrorLog(Long jobId, ExecuteParams runParams, String log) {
		// TODO Auto-generated method stub
		
	}

	public void sendSuccessEmail(Long jobId, ExecuteParams runParams, String log) {
		// TODO Auto-generated method stub
		
	}

	public void sendErrorEmail(Long jobId, ExecuteParams runParams, String log) {
		// TODO Auto-generated method stub
		
	}

	public void updateJobRunTime(Long jobId, Date runTime, Date nextRunTime) {
		// TODO Auto-generated method stub
		
	}

	public void updateCronExpression(Long jobId, String cronExpression) {
		// TODO Auto-generated method stub
		
	}

	public void saveAllJobs(List<ScheduleJob> scheduleJobs) {
		// TODO Auto-generated method stub
		
	}

	public void saveAllRuningJobs(List<ScheduleJob> scheduleJobs) {
		// TODO Auto-generated method stub
		
	}

	

	
	

}
