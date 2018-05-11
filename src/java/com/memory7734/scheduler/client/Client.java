package com.memory7734.scheduler.client;


import com.memory7734.protocol.Decoder;
import com.memory7734.protocol.Encoder;
import com.memory7734.protocol.Status;
import com.memory7734.protocol.Task;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.MasterHandler;
import com.memory7734.scheduler.server.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private volatile static Client client;

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private CopyOnWriteArrayList<MasterHandler> connectedHandlers = new CopyOnWriteArrayList<>();
    private Map<InetSocketAddress, ClientHandler> connectedServerNodes = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRuning = true;

    private String serverAddress;
    private MasterClient masterClient = null;

    private InetSocketAddress remotePeer;

    private Client() {
    }

    public static Client getInstance() {
        if (client == null) {
            synchronized (Client.class) {
                if (client == null) {
                    client = new Client();
                }
            }
        }
        return client;
    }

    public Client(String serverAddress) {
        this.serverAddress = serverAddress;
        String[] array = serverAddress.split(":");
        if (array.length == 2) { // Should check IP and port
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            this.remotePeer = new InetSocketAddress(host, port);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
        Scanner scanner = new Scanner(System.in);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    public void start() throws Exception {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline cp = ch.pipeline();
                                cp.addLast(new Encoder(Task.class));
                                cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                                cp.addLast(new Decoder(Status.class));
                                cp.addLast(new ClientHandler());
                            }
                        });

                ChannelFuture channelFuture = b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            logger.debug("Successfully connect to remote slave. remote peer = " + remotePeer);
                            ClientHandler handler = channelFuture.channel().pipeline().get(ClientHandler.class);
                        }
                    }
                });
            }
        });
//        if (bossGroup == null && workerGroup == null) {
//            bossGroup = new NioEventLoopGroup();
//            workerGroup = new NioEventLoopGroup();
//            ServerBootstrap bootstrap = new ServerBootstrap();
//            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel channel) throws Exception {
//                            channel.pipeline()
//                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
//                                    .addLast(new Encoder(Task.class))
//                                    .addLast(new Decoder(Status.class))
//                                    .addLast(new ClientHandler());
//                        }
//                    })
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            String[] array = serverAddress.split(":");
//            String host = array[0];
//            int port = Integer.parseInt(array[1]);
//
//            ChannelFuture future = bootstrap.bind(host, port).sync();
//            logger.info("Server started on port {}", port);
//
//            future.channel().closeFuture().sync();
//        }
    }

    public void stop() {
//        if (bossGroup != null) {
//            bossGroup.shutdownGracefully();
//        }
//        if (workerGroup != null) {
//            workerGroup.shutdownGracefully();
//        }
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (Server.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L,
                            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }
}
