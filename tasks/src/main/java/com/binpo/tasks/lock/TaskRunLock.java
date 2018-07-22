package com.binpo.tasks.lock;

public interface TaskRunLock {
    /**
     * 是否被锁
     * 
     * @param key
     * @return
     */
    public boolean isLock(String key);

    /**
     * 释放锁
     * 
     * @param key
     */
    public void releaseLock(String key);
}
