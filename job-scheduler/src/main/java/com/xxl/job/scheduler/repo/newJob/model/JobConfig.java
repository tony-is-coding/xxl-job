package com.xxl.job.scheduler.repo.newJob.model;

import lombok.Data;

import java.util.Date;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:11 下午
 */
@Data
public class JobConfig {
    private long id;
    private String jobDesc;
    private String owner;
    private int scheduleType;
    private String scheduleConf;
    private int openSwitch;
    private long nextTriggerTime;
    private Date createTime;
    private Date updateTime;
}
