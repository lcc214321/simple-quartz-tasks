package com.binpo.tasks;

import com.binpo.tasks.model.ExecuteParams;

public abstract class AbstractJob {
	/**
	 * 默认的执行方法，也可以自定义
	 * @param executeParams
	 */
	public abstract void execute(ExecuteParams executeParams);
}
