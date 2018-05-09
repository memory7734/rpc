package com.memory7734.rpc.test.master;

import java.util.List;

/**
 * Created by luxiaoxun on 2016-03-10.
 */
public interface PersonService {
    List<Person> GetTestPerson(String name, int num);
}
