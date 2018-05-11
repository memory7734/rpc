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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;


public class ServerHandler extends SimpleChannelInboundHandler<Task> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private final MasterClient masterClient;

    public ServerHandler(MasterClient masterClient) {
        this.masterClient = masterClient;
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Task task) throws Exception {
        logger.info("服务器收到了一个消息");
        ctx.writeAndFlush("服务器收到了一个消息");
//        Server.submit(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("接到一个任务");
//                logger.info("Receive Task " + task.getTaskGroup() + task.getTaskID());
//                Status status = new Status();
//                status.setTaskID(task.getTaskID());
//                status.setTaskGroup(task.getTaskGroup());
////                try {
////                    status.setResult(handle(task));
////                } catch (Throwable throwable) {
////                    status.setError(throwable.toString());
////                    logger.error("Server Task handle request error", throwable);
////                }
//                ctx.writeAndFlush(status).addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture future) throws Exception {
//                        System.out.println("发出一个反馈");
//                        logger.info("send status for task " + task.getTaskGroup() + task.getTaskID());
//                    }
//                });
//            }
//        });
    }

    private Object handle(Task task) {
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
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Server catch exception", cause);
        ctx.close();
    }
}
