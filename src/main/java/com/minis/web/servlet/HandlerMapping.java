package com.minis.web.servlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public interface HandlerMapping {
    HandlerMethod getHandler(HttpServletRequest request) throws Exception;
}
