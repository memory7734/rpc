package com.memory7734.scheduler.client;

import com.memory7734.protocol.Status;
import com.memory7734.protocol.Task;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Date;

public class ClientHandler extends SimpleChannelInboundHandler<Status> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        logger.error("remotePeer是" + remotePeer);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Status status) throws Exception {
        Task task = new Task();
        task.setTaskGroup("group1");
        task.setTaskID("0001");
        task.setUploadTime(new Date());

        task.setClassName("java.lang.String");
        task.setMethodName("toString");

        Client.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送一个任务");
                logger.info("Send Task " + task.getTaskGroup() + task.getTaskID());
                ctx.writeAndFlush(task).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        logger.info("send task " + task.getTaskGroup() + task.getTaskID());
                    }
                });
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

}
