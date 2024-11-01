package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class DefaultListableBeanFactory
        extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory {

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionNames.toArray(new String[0]);
    }

    // 返回所有与给定类型匹配的 Bean 名称
    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        final List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            boolean matchFound = false;
            final BeanDefinition mbd = this.getBeanDefinition(beanName);
            final Class<?> classToMatch = mbd.getClass();
            if (type.isAssignableFrom(classToMatch)) {
                matchFound = true;
            }
            if (matchFound) {
                result.add(beanName);
            }
        }
        return result.toArray(new String[0]);
    }

    // 返回所有与给定类型匹配的 Bean 的名称和实例，以映射的形式返回
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBeansOfType(Class<?> type) throws BeansException {
        final String[] beanNames = getBeanNamesForType(type);
        final Map<String, T> result = new LinkedHashMap<>(beanNames.length);
        for (String beanName : beanNames) {
            final Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }
}
