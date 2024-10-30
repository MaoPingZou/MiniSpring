package com.minis.beans;

public interface BeanFactory {

    Object getBean(String beanName) throws NotSuchBeanDefinitionException;

    void registerBeanDefinition(BeanDefinition beanDefinition);
}
