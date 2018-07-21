package com.binpo.tasks;

import java.util.Date;
import java.util.List;

import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.ScheduleJob;

/**
 * 任务数据载入和执行日志保存邮件发送
 * 
 * @author zhang
 *
 */
public interface ScheduleJobsOperate {
    /**
     * 通过id获取定任务信息
     * 
     * @param jobId
     * @return
     */
    ScheduleJob getScheduleJob(Long jobId);

    /**
     * 从数据库/配置载入定时任务配置
     * 
     * @return
     */
    List<ScheduleJob> loadJobs();

    /**
     * 保存成功日志信息
     * 
     * @param jobId 任务id
     * @param runParams 执行时候的参数
     * @param log 日志内容
     */
    default void saveSuccessLog(Long jobId, ExecuteParams runParams, String log) {
        
    }
    

    /**
     * 保存错误日志信息
     * 
     * @param jobId 任务id
     * @param runParams 执行时候的参数
     * @param log 日志内容
     */
    default void saveErrorLog(Long jobId, ExecuteParams runParams, String log) {
        
    }

    /**
     * 发送成功邮件
     * 
     * @param jobId 任务id
     * @param runParams 执行时候的参数
     * @param log 日志内容
     */
    default void sendSuccessEmail(Long jobId, ExecuteParams runParams, String log) {
        
    }

    /**
     * 发送失败邮件
     * 
     * @param jobId 任务id
     * @param runParams 执行时候的参数
     * @param log 日志内容
     */
    default void sendErrorEmail(Long jobId, ExecuteParams runParams, String log) {
        
    }

    /**
     * 更新任务的运行时间到数据库
     * 
     * @param jobId
     * @param runTime
     * @param nextRunTime
     */
    void updateJobRunTime(Long jobId, Date runTime, Date nextRunTime);

    /**
     * 更新任务的调度表达式到数据库
     * 
     * @param jobId
     * @param cronExpression 调度表达式
     */
    void updateCronExpression(Long jobId, String cronExpression);

    /**
     * 保存任务框架中任务信息到数据库
     * 
     * @param scheduleJobs
     */
    void saveAllJobs(List<ScheduleJob> scheduleJobs);

    /**
     * 读取任务框架中任务信息保存到指定位置(数据库，缓存服务器)
     * 
     * @param scheduleJobs
     */
    void saveAllRuningJobs(List<ScheduleJob> scheduleJobs);

}
