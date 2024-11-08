package com.minis.web;

import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class XmlConfigReader {
    public XmlConfigReader() {
    }


    public Map<String, MappingValue> loadConfig(Resource res) {
        Map<String, MappingValue> mappings = new HashMap<>();
        while (res.hasNext()) {
            final Element element = (Element) res.next();
            final String beanId = element.attributeValue("id");
            final String beanClassName = element.attributeValue("class");
            final String beanMethod = element.attributeValue("value");

            mappings.put(beanId, new MappingValue(beanId, beanClassName, beanMethod));
        }
        return mappings;
    }
}
