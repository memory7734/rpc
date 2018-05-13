package com.memory7734.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println(context.getScheduler().getContext().get("task"));;

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
