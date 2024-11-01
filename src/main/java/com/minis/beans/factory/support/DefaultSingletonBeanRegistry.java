package com.minis.beans.factory.support;

import com.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    // 容器中存放所有bean的名称的列表
    protected final List<String> beanNames = new ArrayList<>();
    // 容器中存放所有 bean 实例的map
    protected final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    protected final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);
    protected final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);


    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            final Object oldObj = this.singletonObjects.get(beanName);
            if (oldObj != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObj + "] bound");
            }
            this.singletonObjects.put(beanName, singletonObject);
            this.beanNames.add(beanName);
            System.out.println(" bean registered ......." + beanName);
        }
    }

    // 注册 bean 的依赖关系
    public void registerDependentBean(String beanName, String dependentBeanName) {
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans != null && dependentBeans.contains(dependentBeanName)) {
            return;
        }

        // dependentBeanMap 记录 beanName 依赖的所有 dependentBeanName
        synchronized (this.dependentBeanMap) {
            dependentBeans = this.dependentBeanMap.get(beanName);
            if (dependentBeans == null) {
                dependentBeans = new LinkedHashSet<>(8);
                this.dependentBeanMap.put(beanName, dependentBeans);
            }
            dependentBeans.add(dependentBeanName);
        }

        // dependenciesForBeanMap 记录 dependentBeanName 被哪些 beanName 依赖
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBeans = this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBeans == null) {
                dependenciesForBeans = new LinkedHashSet<>(8);
                this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBeans);
            }
            dependenciesForBeans.add(beanName);
        }
    }

    public boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    // 这个方法用于获取指定 bean 的所有依赖于它的 bean 的名称（即，这些 bean 在配置时依赖于该 bean）。
    // 通过调用这个方法，可以确定哪些 bean 依赖于特定的 bean
    public String[] getDependentBeans(String beanName) {
        final Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        return dependentBeans.toArray(new String[0]);
    }

    // 这个方法用于获取指定 bean 所依赖的所有其他 bean 的名称（即，该 bean 在配置时所依赖的 bean）。
    // 通过调用这个方法，可以了解一个特定 bean 在实例化时需要哪些其他 bean，这对于处理 bean 的初始化顺序和依赖注入很重要
    public String[] getDependenciesForBean(String beanName) {
        final Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        return dependenciesForBean.toArray(new String[0]);
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.beanNames.remove(beanName);
        }
    }
}
