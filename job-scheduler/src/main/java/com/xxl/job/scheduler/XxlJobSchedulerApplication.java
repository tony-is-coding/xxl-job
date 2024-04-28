package com.xxl.job.scheduler;

import com.xxl.job.commons.timewheel.HashedWheelTimer;
import com.xxl.job.commons.timewheel.Timer;
import com.xxl.job.commons.timewheel.TimerFuture;
import com.xxl.job.scheduler.repo.newJob.InstanceStatus;
import com.xxl.job.scheduler.repo.newJob.dao.JobConfigMapper;
import com.xxl.job.scheduler.repo.newJob.dao.JobInstanceMapper;
import com.xxl.job.scheduler.repo.newJob.model.JobConfig;
import com.xxl.job.scheduler.repo.newJob.model.JobInstance;
import com.xxl.job.scheduler.task.ScheduleTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobSchedulerApplication implements ApplicationRunner {

	public static void main(String[] args) {
        SpringApplication.run(XxlJobSchedulerApplication.class, args);
	}

	@Resource
	private JobConfigMapper jobConfigMapper;

	@Resource
	private JobInstanceMapper jobInstanceMapper;

	@Resource
	private Dispatcher dispatcher;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ScheduleTask scheduleTask = new ScheduleTask();
		scheduleTask.setDispatcher(dispatcher);
		scheduleTask.setJobConfigMapper(jobConfigMapper);
		scheduleTask.setJobInstanceMapper(jobInstanceMapper);
		for(int i =0;i < 5;i++){
			PeriodicScheduler.factory(scheduleTask, ScheduleTask.SCHEDULE_RATE, 0, "ScheduleTask" + i).startGracefully();
		}
//		initDBJob();
	}

	public void initDBJob(){

		List<JobConfig> configs = new LinkedList<>();

		for(int i =0;i < 200; i++){
			JobConfig config = new JobConfig();
			config.setNextTriggerTime(0L);
			config.setOpenSwitch(1);
			config.setJobDesc("测试任务" + i);
			config.setOwner("tony");
			config.setScheduleType(2);
			int per = i % 10 + 5;
			config.setScheduleConf("*/" + per + " * * * * *");
			configs.add(config);
		}
		jobConfigMapper.saveBatch(configs);
	}
}