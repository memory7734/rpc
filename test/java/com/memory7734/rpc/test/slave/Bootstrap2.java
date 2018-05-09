package com.memory7734.rpc.test.slave;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap2 {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("slave-spring-2.xml");
    }
}
