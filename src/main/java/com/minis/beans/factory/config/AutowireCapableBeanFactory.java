package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

/**
 * @author 邹茂萍
 * @date 2024-10-31
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRED_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;

    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;
}
