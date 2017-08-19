package com.binpo.tasks.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSaveTime implements Serializable{
	private Date saveTime=new Date();
	
	private String serverId;
	
	private List<ScheduleJob> scheduleJobs=new ArrayList<ScheduleJob>();

	
	
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public Date getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}

	public List<ScheduleJob> getScheduleJobs() {
		return scheduleJobs;
	}

	public void setScheduleJobs(List<ScheduleJob> scheduleJobs) {
		this.scheduleJobs = scheduleJobs;
	}
	
	
}
