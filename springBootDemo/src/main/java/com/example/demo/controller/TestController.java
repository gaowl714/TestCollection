package com.example.demo.controller;

import com.example.demo.expand.ExpandApplicationContextAware;
import com.example.demo.expand.ExpandBeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {

    @Autowired
    private ExpandBeanNameAware expandBeanNameAware;

    @Autowired
    private ExpandApplicationContextAware expandApplicationContextAware;

    @RequestMapping("/hello")
    public String hello() {

        String[] beanDefinitionNames = expandApplicationContextAware.getApplicationContext().getBeanDefinitionNames();

        StringBuilder stringBuilder = new StringBuilder();

        int arrayLength = 0;

        arrayLength = beanDefinitionNames.length;
        //将所有bean的名称拼接成字符串（带html的换行符号<br>）
        for (String name : beanDefinitionNames) {
            stringBuilder.append(name).append("<br>");
        }

        return "hello, "
                + new Date()
                + "<br><br>CustomizeBeanNameAware instance bean name : "
                + expandBeanNameAware.getBeanName()
                + "<br><br>bean definition names, size "
                + arrayLength
                + ", detail :<br><br>"
                + stringBuilder;
    }
}
