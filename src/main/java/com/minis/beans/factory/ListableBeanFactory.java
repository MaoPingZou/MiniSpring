package com.minis.beans.factory;

import com.minis.beans.BeansException;

import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    // 根据类型获取所有的相关beanName
    String[] getBeanNamesForType(Class<?> type);

    <T> Map<String, T> getBeansOfType(Class<?> type) throws BeansException;
}
