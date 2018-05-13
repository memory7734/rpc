package com.memory7734.scheduler.server;

import com.memory7734.protocol.Status;
import com.memory7734.protocol.Task;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.MasterFuture;
import com.memory7734.rpc.master.proxy.IAsyncObjectProxy;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import static org.quartz.JobBuilder.newJob;


public class ServerHandler extends SimpleChannelInboundHandler<Task> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private final MasterClient masterClient;

    public ServerHandler(MasterClient masterClient) {
        this.masterClient = masterClient;
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Task task) throws Exception {
        Server.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("Receive Task " + task.getTaskGroup() + task.getTaskID()+ task.getName());
                Status status = new Status();
                status.setTaskID(task.getTaskID());
                status.setTaskGroup(task.getTaskGroup());
                try {
                    JobDetail job = newJob(QuartzJob.class)
                            .withIdentity(task.getName(), task.getTaskGroup())
                            .build();
                    SchedulerFactory sf = new StdSchedulerFactory();
                    Scheduler scheduler = sf.getScheduler();
                    scheduler.start();
                    scheduler.getContext().put("task", task);
                    scheduler.getContext().put("masterClient", masterClient);
                    Matcher<JobKey> matcher = KeyMatcher.keyEquals(new JobKey(task.getName(), task.getTaskGroup()));
                    scheduler.getListenerManager().addJobListener(new JobListener() {
                        @Override
                        public String getName() {
                            return "taskListener";
                        }

                        @Override
                        public void jobToBeExecuted(JobExecutionContext context) {

                        }

                        @Override
                        public void jobExecutionVetoed(JobExecutionContext context) {

                        }

                        @Override
                        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
                            try {
                                status.setResult(context.getScheduler().getContext().get("result"));
                            } catch (SchedulerException e) {
                                e.printStackTrace();
                            }
                            ctx.writeAndFlush(status).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    logger.info("send status for task " + task.getTaskGroup() + task.getTaskID()+ task.getName());
                                }
                            });
                        }
                    }, matcher);
                    scheduler.scheduleJob(job, task.getTrigger());
                } catch (Throwable throwable) {
                    status.setError(throwable.toString());
                    logger.error("Server Task handle request error", throwable);
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Server catch exception", cause);
        ctx.close();
    }


}
