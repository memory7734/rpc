package com.memory7734.rpc.master;

import com.memory7734.protocol.Decoder;
import com.memory7734.protocol.Encoder;
import com.memory7734.protocol.Request;
import com.memory7734.protocol.Response;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class MasterClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new Encoder(Request.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new Decoder(Response.class));
        cp.addLast(new MasterHandler());
    }
}
