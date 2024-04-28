package com.xxl.job.scheduler.task;

import java.util.List;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 4:26 下午
 */
public class FailMonitorTask implements Runnable{

    @Override
    public void run() {
        try {

//            List<Long> failLogIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findFailJobLogIds(1000);
//            if (failLogIds!=null && !failLogIds.isEmpty()) {
//                for (long failLogId: failLogIds) {
//
//                    // lock log
//                    int lockRet = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, 0, -1);
//                    if (lockRet < 1) {
//                        continue;
//                    }
//                    XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(failLogId);
//                    XxlJobInfo info = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(log.getJobId());
//
//                    // 1、fail retry monitor
//                    if (log.getExecutorFailRetryCount() > 0) {
//                        JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount()-1), log.getExecutorShardingParam(), log.getExecutorParam(), null);
//                        String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_type_retry") +"<<<<<<<<<<< </span><br>";
//                        log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
//                        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(log);
//                    }
//
//                    // 2、fail alarm monitor
//                    int newAlarmStatus = 0;		// 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
//                    if (info != null) {
//                        boolean alarmResult = XxlJobAdminConfig.getAdminConfig().getJobAlarmer().alarm(info, log);
//                        newAlarmStatus = alarmResult?2:3;
//                    } else {
//                        newAlarmStatus = 1;
//                    }
//
//                    XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, -1, newAlarmStatus);
//                }
//            }

        } catch (Exception e) {
            //
        }

    }
}
