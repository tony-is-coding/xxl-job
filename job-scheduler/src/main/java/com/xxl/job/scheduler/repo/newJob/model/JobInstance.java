package com.xxl.job.scheduler.repo.newJob.model;

import lombok.Data;

import java.util.Date;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:13 下午
 */
@Data
public class JobInstance {
    private Long id; // 对应 AUTO_INCREMENT
    private Long jobId; // 对应 job_id
    private Integer status; // 对应 status，smallint 对应 Java 的 Integer
    private Integer scheduleType; // 对应 schedule_type
    private Long expectedTriggerTime; // 对应 expected_trigger_time
    private Long actuallyTriggerTime; // 对应 actually_trigger_time
    private String schedulerParams; // 对应 scheduler_params
    private Long finishTime; // 对应 finish_time
    private Date createTime; // 对应 create_time
}
