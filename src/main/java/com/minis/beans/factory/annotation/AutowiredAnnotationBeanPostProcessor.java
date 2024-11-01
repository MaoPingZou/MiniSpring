package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author 邹茂萍
 * @date 2024-10-31
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("调用 AutowiredAnnotationBeanPostProcessor 的 postProcessBeforeInitialization -> beanName: " + beanName);
        Object result = bean;
        final Class<?> clazz = bean.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            final boolean isAutowired = field.isAnnotationPresent(Autowired.class);
            if (isAutowired) {
                final String fieldName = field.getName();
                final Object autowiredObj = this.getBeanFactory().getBean(fieldName);
                // 设置属性值，完成注入
                try {
                    field.setAccessible(true);
                    field.set(bean, autowiredObj);
                    System.out.println("autowired " + fieldName + " for bean " + beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("调用 AutowiredAnnotationBeanPostProcessor 的 postProcessAfterInitialization -> beanName: " + beanName);
        return null;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
