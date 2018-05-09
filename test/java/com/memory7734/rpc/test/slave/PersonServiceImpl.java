package com.memory7734.rpc.test.slave;

import com.memory7734.rpc.slave.SlaveService;
import com.memory7734.rpc.test.master.PersonService;
import com.memory7734.rpc.test.master.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luxiaoxun on 2016-03-10.
 */
@SlaveService(PersonService.class)
public class PersonServiceImpl implements PersonService {

    @Override
    public List<Person> GetTestPerson(String name, int num) {
        List<Person> persons = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            persons.add(new Person(Integer.toString(i), name));
        }
        return persons;
    }
}
