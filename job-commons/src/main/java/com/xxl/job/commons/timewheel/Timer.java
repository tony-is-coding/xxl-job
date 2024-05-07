package com.xxl.job.commons.timewheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 *
 * @author tjq
 * @since 2020/4/2
 */
public interface Timer {

    /**
     * 调度定时任务
     */
    TimerFuture schedule(TimerTask task, long delay, TimeUnit unit, long taskId);

    /**
     * 停止所有调度任务
     */
    Set<TimerTask> stop();
}
