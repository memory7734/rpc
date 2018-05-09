package com.memory7734.rpc.test.slave;

import com.memory7734.rpc.registry.ServiceRegistry;
import com.memory7734.rpc.slave.SlaveServer;
import com.memory7734.rpc.test.master.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootstrapWithoutSpring {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapWithoutSpring.class);

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:18867";
        ServiceRegistry serviceRegistry = new ServiceRegistry("127.0.0.1:2181");
        SlaveServer slaveServer = new SlaveServer(serverAddress, serviceRegistry);
        HelloService helloService = new HelloServiceImpl();
        slaveServer.addService("HelloService", helloService);
        try {
            slaveServer.start();
        } catch (Exception ex) {
            logger.error("Exception: {}", ex);
        }
    }
}
