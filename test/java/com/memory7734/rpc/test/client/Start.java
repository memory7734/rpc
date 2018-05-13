package com.memory7734.rpc.test.client;

import com.memory7734.protocol.Task;
import com.memory7734.scheduler.client.Client;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Date;
import java.util.Properties;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Start {
    public static void main(String[] args) {
        Resource resource = new ClassPathResource("rpc.properties");
        ApplicationContext context = new ClassPathXmlApplicationContext("task-spring.xml");
        Task task = (Task) context.getBean("task");
        task.setUploadTime(new Date());
        task.setTrigger(newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).withRepeatCount(5)).build());
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            Client client = new Client(props.getProperty("server.address"));
            client.start();
            while (true) {
                if (!client.isRuning()) {
                    client.getHandler().sendTask(task);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


//        task.setTaskGroup("group1");
//        task.setTaskID("0001");
//        task.setName("task1");
//        task.setClassName("com.memory7734.rpc.test.master.HelloService");
//        task.setMethodName("hello");
//        Object[] parameters = new Object[1];
//        parameters[0] = "王杰";
//        task.setParameters(parameters);

    }
}
