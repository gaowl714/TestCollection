package com.example.demo.expand;

import com.example.demo.common.Utils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ExpandApplicationContextAware implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(applicationContext.getClass().getName());
        Utils.printTrack("applicationContext is set to " + applicationContext);
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext(){
        return this.applicationContext;
    }
}
