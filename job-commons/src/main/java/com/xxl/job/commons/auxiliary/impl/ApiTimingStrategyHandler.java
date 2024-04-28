package com.xxl.job.commons.auxiliary.impl;

import com.xxl.job.commons.auxiliary.AbstractTimingStrategyHandler;

/**
 * @author Echo009
 * @since 2022/3/22
 */
public class ApiTimingStrategyHandler extends AbstractTimingStrategyHandler {
    @Override
    public TimeExpressionType expressionType() {
        return TimeExpressionType.API;
    }

    @Override
    protected Long calculateNextTriggerTime0(Long preTriggerTime, String timeExpression) {
        return null;
    }
}
