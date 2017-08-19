package com.binpo.tasks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.binpo.tasks.model.ExecuteParams;
import com.binpo.tasks.model.ScheduleJob;

public class TaskUtils {
	private static ApplicationContext context;
	private static  Log log = LogFactory.getLog(TaskUtils.class);
	public static void invokMethod(ScheduleJob scheduleJob,ExecuteParams executeParams) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {  
        Object object = null;  
        Class clazz = null;  
        if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {  
            object = context.getBean(scheduleJob.getSpringId());  
        } 
        if (object == null) {  
            log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，请检查是否配置正确！！！");  
            return;  
        }  
        clazz = object.getClass();  
        Method method = null;  
        try {  
            method = clazz.getDeclaredMethod(scheduleJob.getMethodName(),ExecuteParams.class);  
        } catch (NoSuchMethodException e) {  
            log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，方法名设置错误！！！");  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        }  
        if (method != null) {  
        	method.invoke(object,executeParams);  
        }  
          
    } 
	
	public static void setApplicationContext(ApplicationContext applicationContext){
		context=applicationContext;
	}
}
