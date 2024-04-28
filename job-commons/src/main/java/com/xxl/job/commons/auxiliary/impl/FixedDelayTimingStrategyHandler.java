package com.xxl.job.commons.auxiliary.impl;

import com.xxl.job.commons.auxiliary.AbstractTimingStrategyHandler;

/**
 * @author Echo009
 * @since 2022/3/22
 */
public class FixedDelayTimingStrategyHandler extends AbstractTimingStrategyHandler {

    @Override
    public void checkValid(String timeExpression) {
        long delay;
        try {
            delay = Long.parseLong(timeExpression);
        } catch (Exception e) {
            throw new RuntimeException("invalid timeExpression!");
        }
        // 默认 120s ，超过这个限制应该考虑使用其他类型以减少资源占用
        int maxInterval = Integer.parseInt(System.getProperty("frequency-job.max-interval", "120000"));
        if (delay > maxInterval) {
            throw new RuntimeException("the delay must be less than " + maxInterval + "ms");
        }
        if (delay <= 0) {
            throw new RuntimeException("the delay must be greater than 0 ms");
        }
    }

    @Override
    protected Long calculateNextTriggerTime0(Long preTriggerTime, String timeExpression) {
        return null;
    }

    @Override
    public TimeExpressionType expressionType() {
        return TimeExpressionType.FIXED_DELAY;
    }
}
