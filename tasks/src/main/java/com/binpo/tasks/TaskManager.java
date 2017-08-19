package com.binpo.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.binpo.tasks.lock.TaskRunLock;
import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.JobStatus;
import com.binpo.tasks.model.ScheduleJob;

/**
 * 定时任务管理
 * @author zhang
 *
 */
@Component
public class TaskManager {
	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private ApplicationContext context;
	@Autowired
	private TaskRunLock lock;
	private final static SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();  
	
	@Autowired
	private ScheduleJobsOperate scheduleLoadAndLogsave;
	private SerializeConfig mapping = new SerializeConfig();  
    private String dateFormat="yyyy-MM-dd HH:mm:ss";
	public TaskManager(){
		
	}
	
	@Autowired
	public void init() throws SchedulerException{
		TaskUtils.setApplicationContext(context);
		QuartzJobFactory.setLock(lock);
		QuartzJobFactory.setScheduleLoadAndLogsave(scheduleLoadAndLogsave);
		
		List<ScheduleJob> loadJobs = scheduleLoadAndLogsave.loadJobs();
		if(loadJobs==null){
			loadJobs=new ArrayList<ScheduleJob>();
		}
		for(ScheduleJob job:loadJobs){
		    try{
		        addJob(job);
		        logger.debug("加载定时任务："+JSON.toJSONString(job));
		    }catch(Exception e){
		        logger.error("加载定时任务失败：定时任务id"+job.getId(), e);
		    }
		}
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.start();
		logger.info("定时任务初始化完成");
		//不同的策略进行不同的启动后的处理
		//TODO
	}
	
	/**
	 * 添加任务
	 * @param job
	 * @throws SchedulerException
	 */
	public void addJob(ScheduleJob job) throws SchedulerException {  
        if (job == null || !JobStatus.STATUS_RUNNING.equals(job.getJobStatus())) {  
            return;  
        }  
  
        Scheduler scheduler = schedulerFactoryBean.getScheduler();  
        logger.debug(scheduler + ".......................................................................................add");  
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());  
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);  
  
        // 不存在，创建一个  
        if (null == trigger) {  
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class).withIdentity(job.getJobName(), job.getJobGroup()).build();  
            jobDetail.getJobDataMap().put("scheduleJob", job);  
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());  
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();  
            scheduler.scheduleJob(jobDetail, trigger);  
        } else {  
            // Trigger已存在，那么更新相应的定时设置  
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());  
            // 按新的cronExpression表达式重新构建trigger  
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();  
            // 按新的trigger重新设置job执行  
            scheduler.rescheduleJob(triggerKey, trigger);  
        }
        job.setPlanNextRunTime(trigger.getNextFireTime());
	}
	/**
	 * 获取所有计划中的任务列表  
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getAllJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				ScheduleJob job = new ScheduleJob();
				job.setJobName(jobKey.getName());
				job.setJobGroup(jobKey.getGroup());
				job.setDescription("触发器:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				job.setJobStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setCronExpression(cronExpression);
				}
				job.setPlanNextRunTime(trigger.getNextFireTime());
				job.setInitTime(trigger.getStartTime());
				jobList.add(job);
			}
		}
		logger.info("获取所有计划中的任务列表  :"+JSON.toJSONString(jobList));
		return jobList;
	}
	
	/**
	 * 所有正在运行的job 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getRunningJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName(jobKey.getName());
			job.setJobGroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			job.setJobStatus(triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronExpression(cronExpression);
			}
			job.setPlanNextRunTime(trigger.getNextFireTime());
			job.setInitTime(trigger.getStartTime());
			jobList.add(job);
		}
		logger.debug("所有正在运行的job   :"+JSON.toJSONString(jobList));
		return jobList;
	}
	
	
	
	/**
	 * 暂停一个job 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void pauseJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(),scheduleJob.getJobGroup());
		logger.debug(jobKey+"任务被暂停");
		scheduler.pauseJob(jobKey);
	}
	
	
	 /** 
     * 恢复一个job 
     *  
     * @param scheduleJob 
     * @throws SchedulerException 
     */  
	public void resumeJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(),scheduleJob.getJobGroup());
		scheduler.resumeJob(jobKey);
		logger.debug(jobKey+"任务被恢复");
		List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
		for(Trigger trigger:triggersOfJob){
			scheduleJob.setPlanNextRunTime(trigger.getNextFireTime());
		}
		
	}
  
    /** 
     * 删除一个job 
     *  
     * @param scheduleJob 
     * @throws SchedulerException 
     */  
	public void deleteJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(),scheduleJob.getJobGroup());
		scheduler.deleteJob(jobKey);
		logger.debug(jobKey+"任务被删除");
	}
  
    /** 
     * 立即执行job 
     *  
     * @param scheduleJob 
     * @throws SchedulerException 
     */  
	public void runAJobNow(ScheduleJob scheduleJob,ExecuteParams executeParams) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(),scheduleJob.getJobGroup());
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));  
		logger.info("立即执行job :"+jobKey+"   执行参数："+JSON.toJSONString(executeParams, mapping));
		
		
		List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
		for(Trigger trigger:triggersOfJob){
			scheduleJob.setPlanNextRunTime(trigger.getNextFireTime());
		}
		executeParams.setNextRunTime(scheduleJob.getPlanNextRunTime());
		jobDetail.getJobDataMap().put("executeParams", executeParams);
		scheduler.triggerJob(jobKey, jobDetail.getJobDataMap());
	}
  
    /** 
     * 更新job时间表达式 
     *  
     * @param scheduleJob 
     * @throws SchedulerException 
     */  
	public void updateJobCron(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(),scheduleJob.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
		scheduler.rescheduleJob(triggerKey, trigger);
		scheduleJob.setPlanNextRunTime(trigger.getNextFireTime());
	}

	public void setLock(TaskRunLock lock) {
		this.lock = lock;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	
	
    
	
}
