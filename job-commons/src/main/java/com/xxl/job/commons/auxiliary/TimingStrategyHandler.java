package com.xxl.job.commons.auxiliary;


import com.xxl.job.commons.auxiliary.impl.TimeExpressionType;

import java.util.List;

/**
 * @author Echo009
 * @since 2022/2/24
 */
public interface TimingStrategyHandler {

    /**
     * 校验表达式
     *
     * @param timeExpression 时间表达式
     */
    void checkValid(String timeExpression);

    /**
     * 计算下次触发时间
     *
     * @param preTriggerTime 上次触发时间 (not null)
     * @param timeExpression 时间表达式
     * @return next trigger time
     */
    Long calculateNextTriggerTime(Long preTriggerTime, String timeExpression);


    List<Long>  calculateNextTriggerTimes(Long preTriggerTime, String timeExpression, int times);

    /**
     * 支持的定时策略
     *
     * @return TimeExpressionType
     */
    TimeExpressionType expressionType();
}
