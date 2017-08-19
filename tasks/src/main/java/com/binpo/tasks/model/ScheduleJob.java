package com.binpo.tasks.model;

import java.util.Date;

/**
 * 任务计划信息
 * 
 * @author zhang
 *
 */
public class ScheduleJob extends ScheduleBase{
	
	public ScheduleJob(){
		
	}
	
	public ScheduleJob(String jobName,String jobGroup,
			String jobStatus,String cronExpression,String springId,String methodName){
		this.jobName=jobName;
		this.jobGroup=jobGroup;
		this.jobStatus=jobStatus;
		this.cronExpression=cronExpression;
		this.springId=springId;
		this.methodName=methodName;
	}
	private Long id;
	private Date initTime;
	private Date updateTime;
	/**
	 * 计划下次执行时间
	 */
	private Date planNextRunTime;
	/**
	 * 上次执行时间
	 */
	private Date lastRunTime;
	
	/**
	 * 是否支持分布式环境运行
	 * false:不支持，集群环境只有一台机器会执行
	 * true:支持，集群的时候一个任务会在多台任务上执行，所以业务代码需要支持分布式的执行，处理各个一块的任务
	 */
	private boolean distributedRuntime;
	
	private String jobDescription;
	
	/**
	 * 是否支持分布式环境运行<br/>
	 * false:不支持，集群环境只有一台机器会执行<br/>
	 * true:支持，集群的时候一个任务会在多台任务上执行，所以业务代码需要支持分布式的执行，处理各个一块的任务<br/>
	 */
	public boolean isDistributedRuntime() {
		return distributedRuntime;
	}

	public void setDistributedRuntime(boolean distributedRuntime) {
		this.distributedRuntime = distributedRuntime;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getInitTime() {
		return initTime;
	}

	public void setInitTime(Date initTime) {
		this.initTime = initTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getPlanNextRunTime() {
		return planNextRunTime;
	}

	public void setPlanNextRunTime(Date planNextRunTime) {
		this.planNextRunTime = planNextRunTime;
	}

	public Date getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Date lastRunTime) {
		this.lastRunTime = lastRunTime;
	}
	
	
	

	
}
