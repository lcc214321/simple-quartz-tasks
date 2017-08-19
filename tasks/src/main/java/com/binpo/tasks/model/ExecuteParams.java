package com.binpo.tasks.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 执行参数
 * @author zhang
 *
 */
public class ExecuteParams {
	/**
	 * 其他人为传递的参数
	 */
	private Map params=new HashMap();
	
	/**
	 * 人为控制传的参数
	 */
	private Date executeTime;
	
	private Date runTime;
	
	private Date nextRunTime;

	/**
	 * 数据库中的任务id
	 */
	private Long jobId;
	
	

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public Date getRunTime() {
		return runTime;
	}

	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

	public Date getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}
	
	
	
}
