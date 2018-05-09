package com.memory7734.rpc.test.slave;

import com.memory7734.rpc.test.master.HelloService;
import com.memory7734.rpc.test.master.Person;
import com.memory7734.rpc.slave.SlaveService;

@SlaveService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    public HelloServiceImpl(){

    }

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
