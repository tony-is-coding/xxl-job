package com.xxl.job.scheduler.task;

import com.google.common.collect.Lists;
import com.xxl.job.commons.auxiliary.impl.TimeExpressionType;
import com.xxl.job.commons.auxiliary.TimingStrategyHandlerHolder;
import com.xxl.job.commons.timewheel.TimerTask;
import com.xxl.job.commons.timewheel.holder.InstanceTimeWheelService;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.scheduler.Dispatcher;
import com.xxl.job.scheduler.repo.newJob.InstanceStatus;
import com.xxl.job.scheduler.repo.newJob.dao.JobConfigMapper;
import com.xxl.job.scheduler.repo.newJob.dao.JobInstanceMapper;
import com.xxl.job.scheduler.repo.newJob.model.JobConfig;
import com.xxl.job.scheduler.repo.newJob.model.JobInstance;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/26 6:18 下午
 */
@Setter
@Slf4j
public class ScheduleTask implements TimerTask {

    public static final long SCHEDULE_RATE = 1000;

    public static final long MAX_PER_TIME_SCHEDULE_JOB = 1000;
    public static final int MAX_PER_RANGE_SCHEDULE = 20;

    private Dispatcher dispatcher;
    private JobConfigMapper jobConfigMapper;
    private JobInstanceMapper jobInstanceMapper;

    ReentrantLock reentrantLock = new ReentrantLock();


    ThreadPoolExecutor BATCH_EXECUTOR_POOL = new ThreadPoolExecutor(
            8,16 , 60 , TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public void run() {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        reentrantLock.lock();
        // lock
        // 查询最近 未来20s 内需要执行的SQL job
        long nowTime = System.currentTimeMillis();
        List<JobConfig> jobConfigLists = jobConfigMapper.queryScheduleJobs(nowTime + SCHEDULE_RATE * 2, 1000);
        if(jobConfigLists.isEmpty()){
            return;
        }

        final Map<String, String> ctx = MDC.getCopyOfContextMap();
        List<List<JobConfig>> partition = Lists.partition(jobConfigLists, MAX_PER_RANGE_SCHEDULE);
        CountDownLatch latch = new CountDownLatch(partition.size());

        for (List<JobConfig> jobConfigs : partition) {
            BATCH_EXECUTOR_POOL.submit(() -> {
               try{
                   if(ctx != null){
                       MDC.setContextMap(ctx);
                   }
                   Map<Long, JobConfig> id2JobMap = jobConfigs.stream().collect(Collectors.toMap(JobConfig::getId, e -> e));
                   // 1. 创建任务实例, 批量写入
                   // 2. 同时推任务到时间轮
                   List<JobInstance> instances = new LinkedList<>();

                   long now0 = System.currentTimeMillis() ; // 当前时间往前的都计划为当前调度
                   for (JobConfig jobInfo : jobConfigs) {
                       JobInstance instance = createInstance(jobInfo,now0);
                       instances.add(instance);
                   }
                   jobInstanceMapper.saveBatch(instances);

                   for (JobInstance instance : instances) {
                       JobConfig jobInfo = id2JobMap.get(instance.getJobId());
                       long delay = 0;
                       long now = System.currentTimeMillis();
                       if(instance.getExpectedTriggerTime() > now){
                           delay = jobInfo.getNextTriggerTime() - now;
                       }
                       log.info("分派任务{}|delay:{}, triggerTime:{}, jobId:{}", instance.getId(), delay, DateUtil.fromLong(instance.getExpectedTriggerTime()), instance.getJobId());
                       InstanceTimeWheelService.schedule(instance.getId(),delay, () -> {
                           if(ctx != null){
                               MDC.setContextMap(ctx);
                           }
                           dispatcher.dispatch(jobInfo, instance.getId());
                       });
                   }
                   // 计算下一次执行时间为多少, 然后更新到job中
                   //job 回写到库
                   for (JobConfig jobConfig : jobConfigs) {
                       Long nextTriggerTime = TimingStrategyHandlerHolder
                               .getHandler(TimeExpressionType.of(jobConfig.getScheduleType()))
                               .calculateNextTriggerTime(jobConfig.getNextTriggerTime(), jobConfig.getScheduleConf());
                       jobConfig.setNextTriggerTime(nextTriggerTime);
                   }
                   jobConfigMapper.batchUpdateNextTriggerTime(jobConfigs);
               }finally {
                   latch.countDown();
               }
            });
        }
        reentrantLock.unlock();
        // unlock
    }


    private JobInstance createInstance(JobConfig jobConfig, long now){
        JobInstance instance = new JobInstance();
        instance.setExpectedTriggerTime(Math.max(jobConfig.getNextTriggerTime(), now));
        instance.setJobId(jobConfig.getId());
        instance.setActuallyTriggerTime(0L);
        instance.setScheduleType(jobConfig.getScheduleType());
        instance.setStatus(InstanceStatus.WAITING_DISPATCH.getV());
        instance.setFinishTime(0L);
        instance.setSchedulerParams("hello world");

        // TODO: 将 config 转化为 调度参数, 具体调度交给 实际的调度者来处理

        return instance;

    }

}
