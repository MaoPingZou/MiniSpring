package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

/**
 * 维护Bean之间的依赖关系以及支持 Bean处理器
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    // 注册一个bean的依赖关系
    void registerDependentBean(String beanName, String dependentBeanName);

    // 这个方法用于获取指定 bean 的所有依赖于它的 bean 的名称（即，这些 bean 在配置时依赖于该 bean）。
    // 通过调用这个方法，可以确定哪些 bean 依赖于特定的 bean
    String[] getDependentBeans(String beanName);

    // 这个方法用于获取指定 bean 所依赖的所有其他 bean 的名称（即，该 bean 在配置时所依赖的 bean）。
    // 通过调用这个方法，可以了解一个特定 bean 在实例化时需要哪些其他 bean，这对于处理 bean 的初始化顺序和依赖注入很重要
    String[] getDependenciesForBean(String beanName);

}
