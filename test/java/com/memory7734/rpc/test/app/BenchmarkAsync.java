package com.memory7734.rpc.test.app;

import com.memory7734.rpc.master.MasterFuture;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.proxy.IAsyncObjectProxy;
import com.memory7734.rpc.test.master.HelloService;
import com.memory7734.rpc.registry.ServiceDiscovery;

import java.util.concurrent.TimeUnit;

/**
 * Created by luxiaoxun on 2016/3/16.
 */
public class BenchmarkAsync {
    public static void main(String[] args) throws InterruptedException {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
        final MasterClient masterClient = new MasterClient(serviceDiscovery);

        int threadNum = 10;
        final int requestNum = 20;
        Thread[] threads = new Thread[threadNum];

        long startTime = System.currentTimeMillis();
        //benchmark for async call
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            IAsyncObjectProxy client = masterClient.createAsync(HelloService.class);
                            MasterFuture helloFuture = client.call("hello", Integer.toString(i));
                            String result = (String) helloFuture.get(3000, TimeUnit.MILLISECONDS);
                            //System.out.println(result);
                            if (!result.equals("Hello! " + i))
                                System.out.println("error = " + result);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("Async call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum * threadNum)) / timeCost * 1000);
        System.out.println(msg);

        masterClient.stop();

    }
}
