package com.binpo.tasks;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.binpo.tasks.lock.TaskRunLock;
import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.ScheduleJob;
import com.binpo.tasks.pubsub.Subscriber;

/**
 * 通过订阅来管理机任务
 * 
 * @author zhang
 *
 */
public class JobManagePubSub extends Subscriber {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private ScheduleJobsOperate scheduleJobsOperate;
    @Autowired
    private TaskRunLock lock;

    private final String TASK_PUBSUB = "taskPubSub:";

    /**
     * 上次心跳检测的uuid
     */
    private String lastRunUuid = "";
    public String getLastRunUuid() {
        return lastRunUuid;
    }

    /**
     * 订阅频道
     */
    private String channelName;

    @Override
    public String getChannelName() {
        return this.channelName;
    }

    @Override
    public void onMessage(String channel, String message) {
        try {
            Map<String, String> params = JSON.parseObject(message, Map.class);
            String method = params.get("method");
            if (!checkMethodName(method)) {
                return;
            }
            Method declaredMethod = this.getClass().getDeclaredMethod(method, Map.class);
            declaredMethod.invoke(this, params);
        } catch (Exception e) {
            logger.error(TASK_PUBSUB + "方法调用失败;调用参数:" + message, e);
            e.printStackTrace();
        }
    }

    /**
     * 添加任务
     * 
     * @param params
     * @throws SchedulerException
     */
    private void addJob(Map<String, String> params) throws SchedulerException {
        // 找到老的任务删除
        String oldJobGroup = params.get("oldJobGroup");
        String oldJobName = params.get("oldJobName");
        if (logger.isDebugEnabled()) {
            logger.debug("删除老任务：oldJobGroup：" + oldJobGroup + "    oldJobName:" + oldJobName);
        }
        ScheduleJob oldJob = new ScheduleJob();
        oldJob.setJobGroup(oldJobGroup);
        oldJob.setJobName(oldJobName);
        taskManager.deleteJob(oldJob);
        ScheduleJob job = scheduleJobsOperate.getScheduleJob(getId(params));
        taskManager.addJob(job);
        if (logger.isDebugEnabled()) {
            logger.debug("添加新的任务：" + job.getJobGroup() + ":" + job.getJobName());
        }
        updateJobRunTime(job);
    }

    private void runAJobNow(Map<String, String> params) throws SchedulerException {
        ScheduleJob job = scheduleJobsOperate.getScheduleJob(getId(params));
        String key = "runAJobNow_id_" + job.getId();
        try {
            boolean distributedRuntime = job.isDistributedRuntime();
            boolean lock2 = lock.isLock(key);
            if (distributedRuntime == false && lock2) {
                logger.info(TASK_PUBSUB + job.getJobDescription() + "已经在其他服务器上执行");
                return;
            }
            ExecuteParams executeParams = JSON.parseObject(params.get("executeParams"), ExecuteParams.class);
            taskManager.runAJobNow(job, executeParams);
            updateJobRunTime(job);
            lock.releaseLock(key);
        } catch (Exception e) {
            lock.releaseLock(key);
            logger.error(TASK_PUBSUB + "手动执行任务id为：" + getId(params) + "的任务失败", e);
            e.printStackTrace();
        }
    }

    /**
     * 暂停一个任务
     * 
     * @param params
     * @throws SchedulerException
     */
    private void pauseJob(Map<String, String> params) throws SchedulerException {
        ScheduleJob job = scheduleJobsOperate.getScheduleJob(getId(params));
        taskManager.pauseJob(job);
    }

    /**
     * 恢复一个任务
     * 
     * @param params
     * @throws SchedulerException
     */
    private void resumeJob(Map<String, String> params) throws SchedulerException {
        ScheduleJob job = scheduleJobsOperate.getScheduleJob(getId(params));
        taskManager.resumeJob(job);
        updateJobRunTime(job);
    }

    /**
     * 删除一个任务
     * 
     * @param params
     * @throws SchedulerException
     */
    private void deleteJob(Map<String, String> params) throws SchedulerException {
        ScheduleJob job = scheduleJobsOperate.getScheduleJob(getId(params));
        taskManager.deleteJob(job);
    }

    /**
     * 更新任务表达式
     * 
     * @param params
     */
    private void updateJobCron(Map<String, String> params) {
        ScheduleJob scheduleJob = scheduleJobsOperate.getScheduleJob(getId(params));
        updateCronExpression(scheduleJob, params);
    }

    private void getAllJob(Map<String, String> params) throws SchedulerException {
        List<ScheduleJob> allJob = taskManager.getAllJob();
        scheduleJobsOperate.saveAllJobs(allJob);
    }

    private void getRunningJob(Map<String, String> params) throws SchedulerException {
        List<ScheduleJob> allJob = taskManager.getRunningJob();
        scheduleJobsOperate.saveAllRuningJobs(allJob);
    }

    private Long getId(Map<String, String> params) {
        return Long.valueOf(params.get("id"));
    }

    private void updateJobRunTime(ScheduleJob job) {
        String key = "updateJobRunTime_" + job.getId();
        boolean lock2 = lock.isLock(key);
        if (!lock2) {
            scheduleJobsOperate.updateJobRunTime(job.getId(), job.getLastRunTime(), job.getPlanNextRunTime());
            lock.releaseLock(key);
        }
    }

    private void updateCronExpression(ScheduleJob job, Map<String, String> params) {
        String key = "updateCronExpression_" + job.getId();
        boolean lock2 = lock.isLock(key);
        if (!lock2) {
            String cronExpression = params.get("cronExpression");
            scheduleJobsOperate.updateCronExpression(job.getId(), cronExpression);
            lock.releaseLock(key);
        }
    }

    /**
     * 检查是否存在此方法
     * 
     * @param checkName
     * @author zhang
     * @date 2015年10月28日
     * @version 0.01
     * @return boolean
     */
    private boolean checkMethodName(String methodName) {
        Method[] declaredMethods = this.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    private void heartbeat(Map<String, String> params) throws SchedulerException {
        ExecuteParams executeParams = JSON.parseObject(params.get("executeParams"), ExecuteParams.class);
        lastRunUuid = executeParams.getParams().get("uuid").toString();
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.info("onPMessage:pattern:" + pattern + "   channel:" + channel + "     message:" + message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("onSubscribe:   channel:" + channel + "     subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info("onUnsubscribe:   channel:" + channel + "     subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.info("onPUnsubscribe:pattern:" + pattern + "       subscribedChannels:" + subscribedChannels);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.info("onPSubscribe:pattern:" + pattern + "        subscribedChannels:" + subscribedChannels);
    }

}
