package com.memory7734.rpc.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BootStrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("server-spring.xml");
    }

}
