package com.minis.web.servlet;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public class MethodParameter {
    // 方法参数名称
    private volatile String parameterName;
    // 方法参数类型
    private volatile Class<?> parameterType;
    // 方法参数值对象
    private volatile Object parameterValue;
}
