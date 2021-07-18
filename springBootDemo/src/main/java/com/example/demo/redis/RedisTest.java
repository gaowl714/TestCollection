package com.example.demo.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @author: GaoWL
 * @create: 2021-06-17 21:37
 **/
public class RedisTest {


    public static void main(String[] args) {
        List<Person> list = new ArrayList<>(Arrays.asList(new Person("1", "abc"), new Person("2", "defg")));
        list.stream()

    }
}

class Person {
    private String id;
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
