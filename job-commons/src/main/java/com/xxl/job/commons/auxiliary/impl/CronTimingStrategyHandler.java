package com.xxl.job.commons.auxiliary.impl;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.xxl.job.commons.auxiliary.AbstractTimingStrategyHandler;
import com.xxl.job.commons.auxiliary.TimingStrategyHandler;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * @author Echo009
 * @since 2022/2/24
 */
public class CronTimingStrategyHandler extends AbstractTimingStrategyHandler {

    private final CronParser cronParser;

    /**
     * @see CronDefinitionBuilder#instanceDefinitionFor
     * <p>
     * Enhanced quartz cron，Support for specifying both a day-of-week and a day-of-month parameter.
     * https://github.com/PowerJob/PowerJob/issues/382
     */
    public CronTimingStrategyHandler() {
        CronDefinition cronDefinition = CronDefinitionBuilder.defineCron()
                .withSeconds().withValidRange(0, 59).and()
                .withMinutes().withValidRange(0, 59).and()
                .withHours().withValidRange(0, 23).and()
                .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()
                .withMonth().withValidRange(1, 12).and()
                .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(2).supportsHash().supportsL().supportsQuestionMark().and()
                .withYear().withValidRange(1970, 2099).withStrictRange().optional().and()
                .instance();
        this.cronParser = new CronParser(cronDefinition);
    }


    @Override
    public void checkValid(String timeExpression) {
        cronParser.parse(timeExpression);
    }

    @Override
    public Long calculateNextTriggerTime0(Long preTriggerTime, String timeExpression) {
        Cron cron = cronParser.parse(timeExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Instant instant = Instant.ofEpochMilli(preTriggerTime);
        ZonedDateTime preZonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Optional<ZonedDateTime> opt = executionTime.nextExecution(preZonedDateTime);
        if (opt.isPresent()) {
            return opt.get().toEpochSecond() * 1000;
        }
        return null;
    }

    @Override
    public TimeExpressionType expressionType() {
        return TimeExpressionType.CRON;
    }
}
