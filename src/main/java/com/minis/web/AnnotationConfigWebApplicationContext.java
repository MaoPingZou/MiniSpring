package com.minis.web;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.context.*;
import jakarta.servlet.ServletContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邹茂萍
 * @date 2024-11-11
 */
public class AnnotationConfigWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext {

    // IoC 容器上下文
    private final WebApplicationContext parentWebApplicationContext;

    private ServletContext servletContext;
    DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public AnnotationConfigWebApplicationContext(String fileName) {
        this(fileName, null);
    }

    public AnnotationConfigWebApplicationContext(String fileName, WebApplicationContext parentWebApplicationContext) {
        this.parentWebApplicationContext = parentWebApplicationContext;

        this.servletContext = this.parentWebApplicationContext.getServletContext();

        URL xmPath = null;

        try {
            xmPath = this.getServletContext().getResource(fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final List<String> packageNames = XmlScanComponentHelper.getNodeValue(xmPath);
        final List<String> controllerNames = scanPackages(packageNames);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        this.beanFactory = bf;
        this.beanFactory.setParent(this.parentWebApplicationContext.getBeanFactory());
        loadBeanDefinitions(controllerNames);

        if (true) {
            try {
                refresh();
            } catch (IllegalStateException | BeansException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadBeanDefinitions(List<String> controllerNames) {
        for (String controller : controllerNames) {
            final String beanID = controller;
            final String beanClassName = controller;

            final BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);

            this.beanFactory.registerBeanDefinition(beanID, beanDefinition);
        }
    }

    private List<String> scanPackages(List<String> packages) {
        final List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            // 分别递归扫描每个包
            tempControllerNames.addAll(this.scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private List<String> scanPackage(String packageName) {
        final List<String> tempControllerNames = new ArrayList<>();
        URI uri = null;
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final File dir = new File(uri);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) { // 对子目录进行递归扫描
                final List<String> dirRe = scanPackage(packageName + "." + file.getName());
                // 将递归结果放入结果集
                tempControllerNames.addAll(dirRe);
            } else { // 类文件
                final String controllerName = packageName + "." + file.getName().replaceAll(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
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
    public void registerListeners() {
        final ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public void registerBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    @Override
    public void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    public void finishRefresh() {
    }
}
