package com.binpo.tasks.model;

import java.io.Serializable;

public class ScheduleBase implements Serializable{
	/**
	 * 任务名称
	 */
	public String jobName;
	/**
	 * 任务分组
	 */
	public String jobGroup;
	/**
	 * 任务状态 是否启动任务
	 */
	public String jobStatus;
	/**
	 * cron表达式
	 */
	public String cronExpression;
	/**
	 * 描述
	 */
	public String description;
	
	/**
	 * spring bean
	 */
	public String springId;
	/**
	 * 任务调用的方法名
	 */
	public String methodName;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSpringId() {
		return springId;
	}
	public void setSpringId(String springId) {
		this.springId = springId;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
}
