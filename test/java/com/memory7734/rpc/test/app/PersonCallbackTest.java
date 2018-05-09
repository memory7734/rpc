package com.memory7734.rpc.test.app;

import com.memory7734.rpc.master.AsyncRPCCallback;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.MasterFuture;
import com.memory7734.rpc.test.master.Person;
import com.memory7734.rpc.test.master.PersonService;
import com.memory7734.rpc.master.proxy.IAsyncObjectProxy;
import com.memory7734.rpc.registry.ServiceDiscovery;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by luxiaoxun on 2016/3/17.
 */
public class PersonCallbackTest {
    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
        final MasterClient masterClient = new MasterClient(serviceDiscovery);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            IAsyncObjectProxy client = masterClient.createAsync(PersonService.class);
            int num = 5;
            MasterFuture helloPersonFuture = client.call("GetTestPerson", "xiaoming", num);
            helloPersonFuture.addCallback(new AsyncRPCCallback() {
                @Override
                public void success(Object result) {
                    List<Person> persons = (List<Person>) result;
                    for (int i = 0; i < persons.size(); ++i) {
                        System.out.println(persons.get(i));
                    }
                    countDownLatch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    System.out.println(e);
                    countDownLatch.countDown();
                }
            });

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        masterClient.stop();

        System.out.println("End");
    }
}
