package com.memory7734.scheduler.server;

import com.memory7734.protocol.Task;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.MasterFuture;
import com.memory7734.rpc.master.proxy.IAsyncObjectProxy;
import org.quartz.*;

import java.util.concurrent.ExecutionException;

public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        Task task = (Task) schedulerContext.get("task");
        MasterClient masterClient = (MasterClient) schedulerContext.get("masterClient");
        Object result = null;
        try {
            IAsyncObjectProxy service = masterClient.createAsync(Class.forName(task.getClassName()));
            MasterFuture future = service.call(task.getMethodName(), task.getParameters());
            result = future.get();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        schedulerContext.put("result", result);
    }
}
