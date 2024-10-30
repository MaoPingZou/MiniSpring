package com.minis.context;

import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;
import com.minis.beans.*;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class ClassPathXmlApplicationContext implements BeanFactory {
    BeanFactory beanFactory;

    // context 负责整合容器的启动过程，读外部配置，解析Bean 定义，创建BeanFactory
    public ClassPathXmlApplicationContext(String fileName) {
        final Resource resource = new ClassPathXmlResource(fileName);
        final SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // Ooh!
        reader.loadBeanDefinition(resource);
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getBean(String beanName) throws NotSuchBeanDefinitionException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanDefinition);
    }
}
