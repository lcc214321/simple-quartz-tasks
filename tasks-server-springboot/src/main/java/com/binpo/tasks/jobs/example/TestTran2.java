package com.binpo.tasks.jobs.example;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.binpo.tasks.AbstractJob;
import com.binpo.tasks.model.ExecuteParams;
/**
 * 
 * 业务中的定时任务B
 *
 * @author zhang 2017年8月19日 下午1:35:41
 */
@Component
public class TestTran2 extends AbstractJob{
	private Logger logger = LoggerFactory.getLogger(getClass());
	public void test(ExecuteParams executeParams){
		logger.info("....demo2 的自定义方法名....");
	}
    @Override
    public void execute(ExecuteParams executeParams) {
        // TODO Auto-generated method stub
        logger.info("....demo2 默认方法....");
    }
}
