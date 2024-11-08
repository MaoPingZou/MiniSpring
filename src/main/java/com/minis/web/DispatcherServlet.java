package com.minis.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class DispatcherServlet extends HttpServlet {

    private String sContextConfigLocation;
    private Map<String, MappingValue> mappingValueMap;
    // 存放 bean 得类定义
    private final Map<String, Class<?>> mappingClzMap = new HashMap<>();
    // 存放 bean 实例
    private final Map<String, Object> mappingObjMap = new HashMap<>();

    public void init(ServletConfig config) throws ServletException {
        System.out.println("init 自定义 init.......");
        super.init(config);

        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmPath = null;

        try {
            xmPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final Resource rs = new ClassPathXmlResource(xmPath);
        final XmlConfigReader reader = new XmlConfigReader();
        mappingValueMap = reader.loadConfig(rs);

        refresh();
    }

    // 对所有的 mappingValues 中注册的类进行实例化，默认构造函数
    protected void refresh() {
        for (Map.Entry<String, MappingValue> entry : mappingValueMap.entrySet()) {
            final String id = entry.getKey();
            final String className = entry.getValue().getClz();
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(className);
                obj = clz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mappingClzMap.put(id, clz);
            mappingObjMap.put(id, obj);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doGet.......");
        final String sPath = request.getServletPath();
        System.out.println("sPath: " + sPath);
        if (this.mappingValueMap.get(sPath) == null) {
            return;
        }

        final Class<?> clz = this.mappingClzMap.get(sPath); // 获取bean类定义
        final Object obj = this.mappingObjMap.get(sPath); // 获取bean实例
        final String methodName = this.mappingValueMap.get(sPath).getMethod(); // 获取调用方法名
        Object objResult = null;

        try {
            final Method method = clz.getMethod(methodName);
            objResult = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将方法返回值写入Response
        System.out.println("objResult ---> " + objResult);
        response.getWriter().append(objResult.toString());
        System.out.println("碰到问题，就去 Debug.......");
    }

}
