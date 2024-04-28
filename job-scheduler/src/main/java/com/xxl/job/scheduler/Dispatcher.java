package com.xxl.job.scheduler;

import com.xxl.job.scheduler.repo.newJob.model.JobConfig;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 2:19 下午
 */
public interface Dispatcher {

    void dispatch(JobConfig jobInfo, long jobInstanceId);
}
