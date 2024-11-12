package com.minis.web.servlet;

import com.minis.web.WebApplicationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {


    WebApplicationContext wac;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        this.wac = wac;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        final Method method = handlerMethod.getMethod();
        final Object obj = handlerMethod.getBean();
        Object objResult = null;
        try {
            // 反射调用方法
            objResult = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            response.getWriter().append(objResult.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
