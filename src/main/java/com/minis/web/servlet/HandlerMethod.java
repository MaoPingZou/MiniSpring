package com.minis.web.servlet;

import java.lang.reflect.Method;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public class HandlerMethod {
    private Object bean;
    private Class<?> beanType;
    private Method method;
    private MethodParameter[] methodParameters;
    private Class<?> returnType;
    private String description;
    private String className;
    private String methodName;

    public HandlerMethod(Method method, Object obj) {
        this.setMethod(method);
        this.setBean(obj);
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public MethodParameter[] getMethodParameters() {
        return methodParameters;
    }

    public void setMethodParameters(MethodParameter[] methodParameters) {
        this.methodParameters = methodParameters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
