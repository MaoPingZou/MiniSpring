package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    // context 负责整合容器的启动过程，读外部配置，解析Bean 定义，创建BeanFactory
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        final Resource resource = new ClassPathXmlResource(fileName);
        final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // Ooh!
        reader.loadBeanDefinition(resource);

        this.beanFactory = beanFactory;

        if (isRefresh) {
            try {
                this.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void registerListeners() {
        final ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    void initApplicationEventPublisher() {
        final SimpleApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    void registerBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    public void refresh() throws BeansException, IllegalStateException {
        // 往当前的bean工厂中 注册bean处理器
        registerBeanPostProcessors(this.beanFactory);

        // Initialize other special beans in specific context subclasses.
        onRefresh();
    }

    private void registerBeanPostProcessors(DefaultListableBeanFactory bf) {
        //if (supportAutowire) {
        bf.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
        //}
    }

    @Override
    void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    void finishRefresh() {
        publishEvent(new ContextRefreshEvent("Context Refreshed..."));
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }
}
