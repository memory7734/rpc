package com.memory7734.rpc.test.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientBootStrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("client-spring.xml");
    }
}
