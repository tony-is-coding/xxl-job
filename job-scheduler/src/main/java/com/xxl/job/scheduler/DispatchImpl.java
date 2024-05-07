package com.xxl.job.scheduler;

import com.xxl.job.scheduler.repo.newJob.InstanceStatus;
import com.xxl.job.scheduler.repo.newJob.dao.JobInstanceMapper;
import com.xxl.job.scheduler.repo.newJob.model.JobConfig;
import com.xxl.job.scheduler.repo.newJob.model.JobInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 5:05 下午
 */
@Service
@Slf4j
public class DispatchImpl implements Dispatcher{

    @Resource
    private JobInstanceMapper jobInstanceMapper;

    public void dispatch(JobConfig jobInfo, long jobInstanceId) {
//        log.info("插入指针|调度任务{}| jobId:{}|插入指针", jobInstanceId, jobInfo.getId());
        // 允许从外部传入实例信息，减少 io 次数
        // 检查当前任务是否被取消
        long start = System.currentTimeMillis();
        JobInstance instanceInfo = jobInstanceMapper.selectById(jobInstanceId);
        if (InstanceStatus.CANCELED.getV() == instanceInfo.getStatus()) {
            return;
        }
        // 已经被派发过则不再派发
        // fix 并发场景下重复派发的问题
        if (instanceInfo.getStatus() != InstanceStatus.WAITING_DISPATCH.getV()) {
            return;
        }
        String dbInstanceParams = instanceInfo.getSchedulerParams() == null ? "" : instanceInfo.getSchedulerParams();
//        log.info("[Dispatcher-{}|{}] start to dispatch job: {};instancePrams: {}.", jobId, jobInstanceId, jobInfo, dbInstanceParams);
        // 开始进行实际的派发,具体取决于 派发方式
        instanceInfo.setActuallyTriggerTime(System.currentTimeMillis());
        try{
            dispatchInvoke(instanceInfo);
            instanceInfo.setStatus(InstanceStatus.SUCCEED.getV());
        }catch (Exception ignore){
            instanceInfo.setStatus(InstanceStatus.FAILED.getV());
        }finally {
            instanceInfo.setFinishTime(System.currentTimeMillis());
        }
        jobInstanceMapper.updateById(instanceInfo);
        log.info("JOB_SUCCESS|{}, cost:{}", jobInstanceId, System.currentTimeMillis() - start);
    }

    private void dispatchInvoke(JobInstance instanceInfo) throws Exception {

    }
}
