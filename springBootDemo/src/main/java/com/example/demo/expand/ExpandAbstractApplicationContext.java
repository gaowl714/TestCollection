package com.example.demo.expand;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

public class ExpandAbstractApplicationContext extends AnnotationConfigServletWebServerApplicationContext {

    @Override
    protected void initPropertySources() {
        super.initPropertySources();
        //把"MYSQL_HOST"作为启动的时候必须验证的环境变量
        //getEnvironment().setRequiredProperties("MYSQL_HOST");
    }
}
