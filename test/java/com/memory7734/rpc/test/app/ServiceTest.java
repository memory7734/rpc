package com.memory7734.rpc.test.app;

import com.memory7734.rpc.master.MasterFuture;
import com.memory7734.rpc.master.MasterClient;
import com.memory7734.rpc.master.SlaveInfo;
import com.memory7734.rpc.master.proxy.IAsyncObjectProxy;
import com.memory7734.rpc.test.master.HelloService;
import com.memory7734.rpc.test.master.Person;
import com.memory7734.rpc.test.master.PersonService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:master-spring.xml")
public class ServiceTest {

    @Autowired
    private MasterClient masterClient;

    @Test
    public void helloTest1() {
        HelloService helloService = masterClient.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);
        Assert.assertEquals("Hello! World", result);
    }

    @Test
    public void memory() {

        SlaveInfo helloService = masterClient.create(SlaveInfo.class);
        System.out.println("getFreeMemory结果为：" + helloService.getFreeMemory(""));
//        System.out.println("getMaxMemory结果为：" + helloService.getMaxMemory(""));
//        System.out.println("getUsedMemory结果为：" + helloService.getUsedMemory(""));
//        System.out.println("getProcessors结果为：" + helloService.getProcessors(""));
    }

    @Test
    public void helloTest2() {
        HelloService helloService = masterClient.create(HelloService.class);
        Person person = new Person("Yong", "Huang");
        String result = helloService.hello(person);
        Assert.assertEquals("Hello! Yong Huang", result);
    }

    @Test
    public void helloPersonTest() {
        PersonService personService = masterClient.create(PersonService.class);
        int num = 5;
        List<Person> persons = personService.GetTestPerson("xiaoming", num);
        List<Person> expectedPersons = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
        }
        assertThat(persons, equalTo(expectedPersons));

        for (int i = 0; i < persons.size(); ++i) {
            System.out.println(persons.get(i));
        }
    }

    @Test
    public void helloFutureTest1() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy helloService = masterClient.createAsync(HelloService.class);
        MasterFuture result = helloService.call("hello", "World");
        Assert.assertEquals("Hello! World", result.get());
    }

    @Test
    public void helloFutureTest2() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy helloService = masterClient.createAsync(HelloService.class);
        Person person = new Person("Yong", "Huang");
        MasterFuture result = helloService.call("hello", person);
        Assert.assertEquals("Hello! Yong Huang", result.get());
    }

    @Test
    public void helloPersonFutureTest1() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy helloPersonService = masterClient.createAsync(PersonService.class);
        int num = 5;
        MasterFuture result = helloPersonService.call("GetTestPerson", "xiaoming", num);
        List<Person> persons = (List<Person>) result.get();
        List<Person> expectedPersons = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
        }
        assertThat(persons, equalTo(expectedPersons));

        for (int i = 0; i < num; ++i) {
            System.out.println(persons.get(i));
        }
    }

    @After
    public void setTear() {
        if (masterClient != null) {
            masterClient.stop();
        }
    }

}
