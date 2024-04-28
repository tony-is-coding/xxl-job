package com.xxl.job.commons.auxiliary;


import com.xxl.job.commons.auxiliary.impl.TimeExpressionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Echo009
 * @since 2022/3/22
 */
@Slf4j
public abstract class AbstractTimingStrategyHandler implements TimingStrategyHandler {
    @Override
    public void checkValid(String timeExpression) {
        // do nothing
    }

    @Override
    public Long calculateNextTriggerTime(Long preTriggerTime, String timeExpression) {
        long now = System.currentTimeMillis();
        if(preTriggerTime < now){
            preTriggerTime = now;
        }
        // 开始计算
        return calculateNextTriggerTime0(preTriggerTime, timeExpression);
    }

    @Override
    public List<Long> calculateNextTriggerTimes(Long preTriggerTime, String timeExpression, int times) {
        List<Long> triggerTimeList = new ArrayList<>(times);
        Long nextTriggerTime = System.currentTimeMillis();
        do {
            nextTriggerTime = calculateNextTriggerTime(nextTriggerTime, timeExpression);
            if (nextTriggerTime == null) {
                break;
            }
            triggerTimeList.add(nextTriggerTime);
        } while (triggerTimeList.size() < times);

        if (triggerTimeList.isEmpty()) {
            return new LinkedList<>();
        }
        return triggerTimeList;
//        return triggerTimeList.stream().map(t -> DateFormatUtils.format(t, OmsConstant.TIME_PATTERN)).collect(Collectors.toList());
    }

    protected abstract Long calculateNextTriggerTime0(Long preTriggerTime, String timeExpression);
}
