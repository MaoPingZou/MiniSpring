package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class XmlBeanDefinitionReader {
    BeanFactory beanFactory;

    public XmlBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinition(Resource resource) {
        while (resource.hasNext()) {
            final Element element = (Element) resource.next();
            final String beanId = element.attributeValue("id");
            final String beanClassName = element.attributeValue("class");
            final BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
            // Oh!
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}
