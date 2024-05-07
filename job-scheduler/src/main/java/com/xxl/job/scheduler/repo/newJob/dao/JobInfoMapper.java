package com.xxl.job.scheduler.repo.newJob.dao;

import com.xxl.job.scheduler.repo.newJob.model.JobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface JobInfoMapper {

	public int batchInsertJobInfo(List<JobInfo> info);

}
