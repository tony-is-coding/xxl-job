package com.xxl.job.scheduler.repo.newJob.dao;

import com.xxl.job.scheduler.repo.newJob.model.JobInstance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:13 下午
 */
@Mapper
public interface JobInstanceMapper {

    int saveBatch(List<JobInstance> jobInstances);

    JobInstance selectById(Long id);

    void updateById(JobInstance jobInstance);

}
