package com.example.demo.expand;

import com.example.demo.common.Utils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

@Component
public class ExpandBeanNameAware implements BeanNameAware {

    private String beanName;

    @Override
    public void setBeanName(String s) {
        Utils.printTrack("beanName is set to " + beanName);
        this.beanName = s;
    }

    public String getBeanName() {
        return this.beanName;
    }
}
