package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.RequestMapping;
import com.minis.web.WebApplicationContext;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public class RequestMappingHandlerMapping implements HandlerMapping {

    WebApplicationContext wac;
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    public RequestMappingHandlerMapping(WebApplicationContext wac) {
        this.wac = wac;
        initMapping();
    }

    // 建立 URL 与调用方法和实例的映射关系，存储在 mappingRegistry 中
    protected void initMapping() {
        Class<?> clz = null;
        Object obj = null;
        final String[] controllerNames = this.wac.getBeanDefinitionNames();
        // 扫描 wac 中存放的所有 bean
        for (String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (ClassNotFoundException | BeansException e) {
                throw new RuntimeException(e);
            }

            final Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                final boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                if (isRequestMapping) {
                    final String methodName = method.getName();
                    final String urlMapping = method.getAnnotation(RequestMapping.class).value();

                    // 将 urlMapping 映射关系存储在 mappingRegistry 中
                    this.mappingRegistry.getUrlMappingNames().add(urlMapping);
                    this.mappingRegistry.getMappingObjs().put(urlMapping, obj);
                    this.mappingRegistry.getMappingMethods().put(urlMapping, method);
                }
            }
        }

    }


    @Override
    public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
        final String urlMapping = request.getServletPath();
        if (!this.mappingRegistry.getUrlMappingNames().contains(urlMapping)) {
            return null;
        }
        final Method method = this.mappingRegistry.getMappingMethods().get(urlMapping);
        final Object obj = this.mappingRegistry.getMappingObjs().get(urlMapping);
        // Ooh!!!
        final HandlerMethod handlerMethod = new HandlerMethod(method, obj);

        return handlerMethod;
    }
}
