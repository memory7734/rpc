package com.memory7734.rpc.test.slave;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap1 {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("slave-spring-1.xml");
    }
}
