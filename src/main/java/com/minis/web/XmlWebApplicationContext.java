package com.minis.web;

import com.minis.context.ClassPathXmlApplicationContext;
import jakarta.servlet.ServletContext;

/**
 * @author 邹茂萍
 * @date 2024-11-12
 */
public class XmlWebApplicationContext extends ClassPathXmlApplicationContext implements WebApplicationContext {
    private ServletContext servletContext;

    public XmlWebApplicationContext(String fileName) {
        super(fileName);
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
