package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 邹茂萍
 * @date 2024-10-31
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    protected List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public AbstractBeanFactory() {
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        // 先尝试直接拿Bean 实例
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            // 如果没有实例，则尝试从毛胚实例中获取
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                // bbs 还是null的？？？ 因为是根据beanName名字获取的, beans.xml文件中的id取名匹配不上
                final BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                singleton = createBean(beanDefinition);
                this.registerBean(beanName, singleton);

                // 进行 beanPostProcessor 处理
                applyBeanPostProcessorsBeforeInitialization(singleton, beanName);
                if (beanDefinition.getInitMethodName() != null && !beanDefinition.getInitMethodName().isEmpty()) {
                    invokeInitMethod(beanDefinition, singleton);
                }
                applyBeanPostProcessorsAfterInitialization(singleton, beanName);
            }
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition bd, Object obj) {
        Class<?> clz = obj.getClass();
        Method method = null;
        try {
            method = clz.getMethod(bd.getInitMethodName());
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean containsBean(String name) {
        return containsSingleton(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name, bd);
        this.beanDefinitionNames.add(name);
        if (!bd.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);

    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        // 创建毛胚bean实例
        Object obj = doCreateBean(beanDefinition);
        // 存放到毛胚实例緩存中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);

        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 处理属性
        populateBean(beanDefinition, clz, obj);

        return obj;
    }

    // doCreateBean创建毛胚实例，仅仅调用构造方法，没有进行属性处理
    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            clz = Class.forName(bd.getClassName());

            //handle constructor
            ConstructorArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (argumentValues.isEmpty()) { // 无参构造方法
                obj = clz.newInstance();
            } else { // 有参构造方法
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue()).intValue();
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                try {
                    // 使用构造方法生成一个实例
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + obj.toString());

        return obj;
    }

    private void populateBean(BeanDefinition bd, Class<?> clz, Object obj) {
        handleProperties(bd, clz, obj);
    }

    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean: " + beanDefinition.getId());
        final PropertyValues propertyValues = beanDefinition.getPropertyValues();
        // 如果有属性
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                // 对每一个属性，分数据类型分别处理
                final PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                final String pType = propertyValue.getType();
                final String pName = propertyValue.getName();
                final Object pValue = propertyValue.getValue();
                final boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) { // 如果不是ref，只是普通属性
                    // 对每一个属性，分数据类型进行处理
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = pValue;
                } else { // is Ref, 创建依赖beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        paramValues[0] = getBean((String) pValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // 按照setXxxx规范查找setter方法，调用setter方法设置属性
                final String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    abstract public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    abstract public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;
}
