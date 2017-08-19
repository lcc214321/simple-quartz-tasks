package com.binpo.tasks.jobs.example;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binpo.tasks.AbstractJob;
import com.binpo.tasks.model.ExecuteParams;
/**
 * 
 * 业务中的定时任务a
 *
 * @author zhang 2017年8月19日 下午1:35:23
 */
public class TestTran extends AbstractJob{
	private Logger logger = LoggerFactory.getLogger(getClass());
	public void test(ExecuteParams executeParams){
		
		logger.info("....sleep....");
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("更新用户名为：FDSFSFSD");
		
		logger.info("....demo1....");
	}
    @Override
    public void execute(ExecuteParams executeParams) {
        // TODO Auto-generated method stub
        
    }
}
