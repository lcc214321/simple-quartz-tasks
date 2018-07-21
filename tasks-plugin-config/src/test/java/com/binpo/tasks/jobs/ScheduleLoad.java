package com.binpo.tasks.jobs;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

	public ScheduleJob getScheduleJob(Long jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ScheduleJob> loadJobs() {
		List<ScheduleJob> scheduleJobs=new ArrayList<ScheduleJob>();
		ScheduleJob scheduleJob=new ScheduleJob();
		scheduleJob.setCronExpression("0/6 * * * * ? 	");
		scheduleJob.setDescription("");
		scheduleJob.setDistributedRuntime(false);
		scheduleJob.setId(1L);
		scheduleJob.setJobDescription("任务描述");
		scheduleJob.setJobGroup("testGroup");
		scheduleJob.setJobName("test1");
		scheduleJob.setJobStatus("1");
		scheduleJob.setMethodName("test");
		scheduleJob.setSpringId("testTran");
		scheduleJobs.add(scheduleJob);
		return scheduleJobs;
	}

    @Override
    public void updateJobRunTime(Long jobId, Date runTime, Date nextRunTime) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCronExpression(Long jobId, String cronExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveAllJobs(List<ScheduleJob> scheduleJobs) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveAllRuningJobs(List<ScheduleJob> scheduleJobs) {
        // TODO Auto-generated method stub
        
    }

	

}
