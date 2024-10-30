package com.minis.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class SimpleBeanFactory implements BeanFactory {
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();

    public SimpleBeanFactory() {
    }

    // getBean 容器的核心方法
    @Override
    public Object getBean(String beanName) throws NotSuchBeanDefinitionException {
        // 先尝试直接拿Bean 实例
        Object singleton = singletons.get(beanName);
        if (singleton == null) {
            final int i = beanNames.indexOf(beanName);
            if (i == -1) {
                throw new NotSuchBeanDefinitionException();
            } else {
                // 获取Bean定义
                final BeanDefinition beanDefinition = beanDefinitions.get(i);
                try {
                    singleton = Class.forName(beanDefinition.getClassName()).newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // 注册Bean 实例
                singletons.put(beanDefinition.getId(), singleton);
            }
        }
        return singleton;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
        this.beanNames.add(beanDefinition.getId());
    }
}
