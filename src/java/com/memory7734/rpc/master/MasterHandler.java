package com.memory7734.rpc.master;

import com.memory7734.protocol.Request;
import com.memory7734.protocol.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class MasterHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger logger = LoggerFactory.getLogger(MasterHandler.class);

    private ConcurrentHashMap<String, MasterFuture> pendingRPC = new ConcurrentHashMap<>();

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
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        String requestId = response.getRequestId();
        MasterFuture masterFuture = pendingRPC.get(requestId);
        if (masterFuture != null) {
            pendingRPC.remove(requestId);
            masterFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("master caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public MasterFuture sendRequest(Request request) {
        final CountDownLatch latch = new CountDownLatch(1);
        MasterFuture masterFuture = new MasterFuture(request);
        pendingRPC.put(request.getRequestId(), masterFuture);
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        return masterFuture;
    }
}
