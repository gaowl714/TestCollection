package com.example.demo;

import com.example.demo.expand.ExpandAbstractApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication springBootApplication = new SpringApplication(DemoApplication.class);
        springBootApplication.setApplicationContextClass(ExpandAbstractApplicationContext.class);
        springBootApplication.run();
    }

}
