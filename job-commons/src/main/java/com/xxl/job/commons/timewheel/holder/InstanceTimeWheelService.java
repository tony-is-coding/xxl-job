package com.xxl.job.commons.timewheel.holder;

import com.google.common.collect.Maps;
import com.xxl.job.commons.timewheel.HashedWheelTimer;
import com.xxl.job.commons.timewheel.Timer;
import com.xxl.job.commons.timewheel.TimerFuture;
import com.xxl.job.commons.timewheel.TimerTask;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 定时调度任务实例
 *
 * @author tjq
 * @since 2020/7/25
 */
public class InstanceTimeWheelService {

    private static final Map<Long, TimerFuture> CARGO = Maps.newConcurrentMap();

    /**
     * 精确调度时间轮，每 1MS 走一格
     */
    private static final Timer TIMER = new HashedWheelTimer(1, 4096, Runtime.getRuntime().availableProcessors() * 4);
    /**
     * 非精确调度时间轮，用于处理高延迟任务，每 10S 走一格
     */
    private static final Timer SLOW_TIMER = new HashedWheelTimer(10000, 12, 0);

    /**
     * 支持取消的时间间隔，低于该阈值则不会放进 CARGO
     */
    private static final long MIN_INTERVAL_MS = 1000;
    /**
     * 长延迟阈值
     */
    private static final long LONG_DELAY_THRESHOLD_MS = 60000;

    /**
     * 定时调度
     * @param uniqueId 唯一 ID，必须是 分布式 唯一 ID
     * @param delayMS 延迟毫秒数
     * @param timerTask 需要执行的目标方法
     */
    public static void schedule(Long uniqueId, Long delayMS, TimerTask timerTask) {
        realSchedule(uniqueId, delayMS, timerTask);
    }

    /**
     * 获取 TimerFuture
     * @param uniqueId 唯一 ID
     * @return TimerFuture
     */
    public static TimerFuture fetchTimerFuture(Long uniqueId) {
        return CARGO.get(uniqueId);
    }


    private static void realSchedule(Long uniqueId, Long delayMS, TimerTask timerTask) {
        TimerFuture timerFuture = TIMER.schedule(() -> {
            CARGO.remove(uniqueId);
            timerTask.run();
        }, delayMS, TimeUnit.MILLISECONDS, uniqueId);

        if (delayMS > MIN_INTERVAL_MS) {
            CARGO.put(uniqueId, timerFuture);
        }
    }

}
