package com.xxl.job.scheduler.repo.newJob.dao;

import com.xxl.job.scheduler.repo.newJob.model.JobConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:12 下午
 */
@Mapper
public interface JobConfigMapper {

    List<JobConfig> queryScheduleJobs(@Param("maxNextTriggerTime") long maxNextTriggerTime, @Param("limit") int limit);

    int updateNextTriggerTime(JobConfig config);

    int batchUpdateNextTriggerTime(List<JobConfig> jobConfigs);

    int saveBatch(List<JobConfig> jobConfigs);

}
