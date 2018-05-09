package com.memory7734.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.*;

public class QuertzApp {
    public static void main(String[] args) throws SchedulerException {
        QuertzApp simpleQuertzApp = new QuertzApp();
        simpleQuertzApp.run();
    }

    private void run() throws SchedulerException {
        //创建一个调度器
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();

        //执行
        scheduler.start();

        JobDetail job = newJob(QuartzJob.class).withIdentity("myJob", "group1").build();

        Trigger trigger = newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

        scheduler.scheduleJob(job, trigger);

    }


}
