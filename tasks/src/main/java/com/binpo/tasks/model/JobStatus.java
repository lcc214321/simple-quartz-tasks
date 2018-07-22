package com.binpo.tasks.model;

public interface JobStatus {
    /**
     * 运行中
     */
    public static final String STATUS_RUNNING = "1";
    /**
     * 不在运行中
     */
    public static final String STATUS_NOT_RUNNING = "0";
    public static final String CONCURRENT_IS = "1";
    public static final String CONCURRENT_NOT = "0";
}
