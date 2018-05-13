package com.memory7734.scheduler;

import com.memory7734.protocol.Task;
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
        Task task = new Task();

        task.setTaskGroup("group1");
        task.setTaskID("0001");
        task.setName("task1");
        task.setUploadTime(new Date());
        task.setClassName("com.memory7734.rpc.test.master.HelloService");
        task.setMethodName("hello");
        Object[] parameters = new Object[1];
        parameters[0] = "王杰";
        task.setTrigger(newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build());
        task.setParameters(parameters);
        scheduler.getContext().put("task",task);

        JobDetail job = newJob(QuartzJob.class).withIdentity("myJob", "group1").build();

        Trigger trigger = newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

        scheduler.scheduleJob(job, task.getTrigger());

    }


}
