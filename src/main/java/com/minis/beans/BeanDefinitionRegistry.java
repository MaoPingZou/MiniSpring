package com.minis.beans;

/**
 * 想当于一个bean仓库
 *
 * @author 邹茂萍
 * @date 2024-10-30
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition bd);

    void removeBeanDefinition(String name);

    BeanDefinition getBeanDefinition(String name);

    boolean containsBeanDefinition(String name);
}
