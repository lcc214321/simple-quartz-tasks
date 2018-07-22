package com.binpo.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.binpo.tasks.lock.TaskRunLock;
import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.ScheduleJob;

/**
 * 若一个方法一次执行不完下次轮转时则等待改方法执行完后才执行下一次操作
 * 
 * @author zhang
 *
 */
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {
    private SerializeConfig mapping = new SerializeConfig();
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private Log logger = LogFactory.getLog(getClass());

    private final String TASK_FACTORY = "taskFactory:";

    private static TaskRunLock lock;

    public static void setLock(TaskRunLock lock) {
        QuartzJobFactory.lock = lock;
    }

    private static ScheduleJobsOperate scheduleLoadAndLogsave;

    public static void setScheduleLoadAndLogsave(ScheduleJobsOperate scheduleLoadAndLogsave) {
        QuartzJobFactory.scheduleLoadAndLogsave = scheduleLoadAndLogsave;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String uuid = UUID.randomUUID().toString();
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        String jobDescription = scheduleJob.getJobDescription() + ":" + uuid + ":";
        logger.info(TASK_FACTORY + "开始执行任务：" + jobDescription);
        String triggerKey = "runKey_" + context.getTrigger().getKey().toString();
        logger.debug("触发器执行key：" + triggerKey);
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        Map<String, Object> wrappedMap = mergedJobDataMap.getWrappedMap();
        Date nextFireTime = context.getTrigger().getNextFireTime();
        ExecuteParams executeParams = new ExecuteParams();
        Object object = wrappedMap.get("executeParams");
        if (object != null) {
            executeParams = (ExecuteParams) object;
        }
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.MILLISECOND, 0);
        executeParams.setRunTime(instance.getTime());
        if (nextFireTime != null) {
            executeParams.setNextRunTime(nextFireTime);
        }
        executeParams.setJobId(scheduleJob.getId());
        boolean distributedRuntime = scheduleJob.isDistributedRuntime();
        try {
            logger.debug(jobDescription + "是否支持分布式环境运行：" + distributedRuntime);

            if (distributedRuntime == false && lock.isLock(triggerKey)) {
                logger.info(TASK_FACTORY + jobDescription + "任务已经在其他服务器执行");
                return;
            }
            mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
            logger.info(TASK_FACTORY + jobDescription + "执行参数：" + JSON.toJSONString(executeParams, mapping));

            TaskUtils.invokMethod(scheduleJob, executeParams);
            logger.info(TASK_FACTORY + jobDescription + "任务执行完成");
            this.saveSuccessLog(executeParams, scheduleJob);
            this.sendSuccessEmail(executeParams, scheduleJob);
            logger.debug(scheduleJob.getJobDescription() + "释放任务锁：" + triggerKey);
            lock.releaseLock(triggerKey);
        } catch (Exception e) {
            this.saveErrorLog(executeParams, scheduleJob);
            this.sendErrorEmail(executeParams, scheduleJob);
            logger.debug("释放任务锁：" + triggerKey);
            lock.releaseLock(triggerKey);
            logger.error(scheduleJob.getId() + "--:--" + jobDescription + "任务执行失败", e);
            try {
                throw e;
            } catch (Exception e1) {
            }
        }
    }

    private void saveSuccessLog(ExecuteParams executeParams, ScheduleJob scheduleJob) {
        scheduleLoadAndLogsave.saveSuccessLog(Long.valueOf(scheduleJob.getId()), executeParams, null);
    }

    private void saveErrorLog(ExecuteParams executeParams, ScheduleJob scheduleJob) {
    }

    private void sendSuccessEmail(ExecuteParams executeParams, ScheduleJob scheduleJob) {
        scheduleLoadAndLogsave.sendSuccessEmail(Long.valueOf(scheduleJob.getId()), executeParams, null);
    }

    private void sendErrorEmail(ExecuteParams executeParams, ScheduleJob scheduleJob) {
        scheduleLoadAndLogsave.sendErrorEmail(Long.valueOf(scheduleJob.getId()), executeParams, null);
    }

}
