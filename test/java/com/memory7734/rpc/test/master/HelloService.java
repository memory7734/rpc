package com.memory7734.rpc.test.master;

public interface HelloService {
    String hello(String name);

    String hello(Person person);
}
