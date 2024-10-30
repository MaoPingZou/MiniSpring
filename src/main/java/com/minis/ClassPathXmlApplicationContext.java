package com.minis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class ClassPathXmlApplicationContext {
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();

    public ClassPathXmlApplicationContext(String fileName) {
        this.readXML(fileName);
        this.instanceBeans();
    }

    // 构造器获取外部配置，解析出 Bean 的定义，形成内存映像
    private void readXML(String fileName) {
        final SAXReader saxReader = new SAXReader();
        try {
            final URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
            final Document document = saxReader.read(xmlPath);
            final Element rootElement = document.getRootElement();
            // 对配置文件中的每一个 <bean>，进行处理
            for (Element element : rootElement.elements()) {
                final String beanId = element.attributeValue("id");
                final String beanClassName = element.attributeValue("class");
                final BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
                beanDefinitions.add(beanDefinition);
            }
        } catch (Exception e) {
            throw new RuntimeException();
            // Ignore
        }
    }

    private void instanceBeans() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                singletons.put(
                        beanDefinition.getId(),
                        Class.forName(beanDefinition.getClassName()).newInstance()
                );
            } catch (Exception e) {
                System.out.println("instanceBeans error");
                throw new RuntimeException();
                // Ignore
            }
        }
    }

    // 这是对外的一个方法, 让外部程序从容器中获取 Bean 实例，会逐步演化成核心方法
    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }
}
