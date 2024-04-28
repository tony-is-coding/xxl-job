package com.xxl.job.scheduler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @desc TODO
 *
 * @author tony
 * @createDate 2024/4/27 6:05 下午
 */
@NoArgsConstructor
@Slf4j
public class PeriodicScheduler{

    private Runnable runnable;
    private long delayMs;
    private long intervalMs;
    private String scheduleName;

    public ScheduleController startGracefully(){
        Thread t = new Thread(() ->{
            try{
                if(delayMs > 0){
                    Thread.sleep(delayMs);
                }
            }catch (Exception ignore){
            }
            while (true){
                try{
                    runnable.run();
                }catch (Exception e){
                    log.error("调度失败....");
                    e.printStackTrace();
                }finally {
                    try{
                        Thread.sleep(intervalMs);
                    }catch (Exception ignore){
                    }
                }
            }
        });
        t.setDaemon(true);
        t.setName(scheduleName);
        t.start();

        return new ScheduleController() {
            @Override
            public boolean isCancel() {
                return t.isAlive();
            }
            @Override
            public void cancel() {
                if(!t.isInterrupted()){
                    t.interrupt();
                }
            }
        };
    }

    public static PeriodicScheduler factory(Runnable runnable, long intervalMs,long delayMs, String scheduleName){
        PeriodicScheduler periodicScheduler = new PeriodicScheduler();
        periodicScheduler.runnable = runnable;
        periodicScheduler.intervalMs = intervalMs;
        periodicScheduler.delayMs=delayMs;
        periodicScheduler.scheduleName = scheduleName;
        return periodicScheduler;
    }

    public interface ScheduleController {
        boolean isCancel();

        void cancel();
    }
}
