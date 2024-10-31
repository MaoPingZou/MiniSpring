package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class XmlBeanDefinitionReader {
    SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinition(Resource resource) {
        while (resource.hasNext()) {
            final Element element = (Element) resource.next();
            final String beanId = element.attributeValue("id");
            final String beanClassName = element.attributeValue("class");
            final BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);

            // 处理构造器
            final List<Element> constructorElements = element.elements("constructor-arg");
            final ArgumentValues avs = new ArgumentValues();
            for (Element e : constructorElements) {
                final String type = e.attributeValue("type");
                final String name = e.attributeValue("name");
                final String value = e.attributeValue("value");
                avs.addArgumentValue(new ArgumentValue(type, name, value));
            }
            beanDefinition.setConstructorArgumentValues(avs);

            // 解析属性
            final List<Element> propertyElements = element.elements("property");
            final PropertyValues pvs = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                final String type = e.attributeValue("type");
                final String name = e.attributeValue("name");
                final String value = e.attributeValue("value");
                final String pRef = e.attributeValue("ref");
                // 最终的 value 值
                String pV = "";
                boolean isRef = false;
                // 先判断value是否有值
                if (value != null && !value.isEmpty()) {
                    pV = value;
                } else if (pRef != null && !pRef.isEmpty()) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                pvs.addPropertyValue(new PropertyValue(type, name, pV, isRef));
            }
            beanDefinition.setPropertyValues(pvs);

            // 设置依赖
            final String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);
            System.out.println("reader -->> " + this.simpleBeanFactory.beanNames);
            this.simpleBeanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }
}
