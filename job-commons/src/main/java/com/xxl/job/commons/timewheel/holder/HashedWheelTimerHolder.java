package com.xxl.job.commons.timewheel.holder;


import com.xxl.job.commons.timewheel.HashedWheelTimer;
import com.xxl.job.commons.timewheel.Timer;

/**
 * 时间轮单例
 *
 * @author tjq
 * @since 2020/4/5
 */
public class HashedWheelTimerHolder {

    /**
     * 非精确时间轮，每 5S 走一格
     */
    public static final Timer INACCURATE_TIMER = new HashedWheelTimer(5000, 16, 0);

    private HashedWheelTimerHolder() {
    }
}
