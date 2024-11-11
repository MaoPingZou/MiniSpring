package com.minis.web;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.Autowired;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class DispatcherServlet extends HttpServlet {

    private String sContextConfigLocation;
    // 存储需要扫描的 package 列表
    private List<String> packageNames = new ArrayList<>();
    // 存储 controller 名称的数组列表
    private List<String> controllerNames = new ArrayList<>();
    private Map<String, Object> controllerObjs = new HashMap<>();
    private Map<String, Class<?>> controllerClasses = new HashMap<>();

    // 存放 bean 得类定义
    private List<String> urlMappingNames = new ArrayList<>();
    // 存放 bean 实例
    private final Map<String, Object> mappingObjs = new HashMap<>();
    // 存放映射的方法
    private final Map<String, Method> mappingMethods = new HashMap<>();

    private WebApplicationContext webApplicationContext;

    // 初始化函数
    public void init(ServletConfig config) throws ServletException {
        System.out.println("init 自定义 init.......");
        // 先调用父类的初始化函数
        super.init(config);

        // 拿到启动时的 wac
        this.webApplicationContext = (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmPath = null;

        try {
            xmPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.packageNames = XmlScanComponentHelper.getNodeValue(xmPath);

        refresh();
    }

    // 对所有的 mappingValues 中注册的类进行实例化，默认构造函数
    protected void refresh() {
        initController(); // 初始化 controller
        initMapping(); // 初始化 url 映射
    }

    protected void initMapping() {
        for (String controllerName : this.controllerNames) {
            // clazz 是用来反射获取类中的属性和方法的，在框架中极其常见
            final Class<?> clazz = this.controllerClasses.get(controllerName);
            final Object obj = this.controllerObjs.get(controllerName);

            final Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                if (isRequestMapping) {
                    final String urlMapping = method.getAnnotation(RequestMapping.class).value();
                    // 存储 url映射路径
                    this.urlMappingNames.add(urlMapping);
                    this.mappingObjs.put(urlMapping, obj);
                    this.mappingMethods.put(urlMapping, method);
                }
            }
        }
    }

    protected void initController() {
        // 扫描包，获取所有的类名
        this.controllerNames = this.scanPackages(this.packageNames);
        for (String controllerName : this.controllerNames) {
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(controllerName); // 反射加载类
                this.controllerClasses.put(controllerName, clz);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                obj = clz.newInstance(); // 实例化 bean

                populateBean(obj, controllerName);


                this.controllerObjs.put(controllerName, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Object populateBean(Object bean, String beanName) throws BeansException {
        Object result = bean;

        final Class<?> clazz = bean.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            final boolean isAutowired = field.isAnnotationPresent(Autowired.class);
            if (isAutowired) {
                final String fieldName = field.getName();
                final Object autowiredObj = this.webApplicationContext.getBean(fieldName);
                try {
                    field.setAccessible(true);
                    field.set(bean, autowiredObj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private List<String> scanPackages(List<String> packages) {
        final List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            // 分别递归扫描每个包
            tempControllerNames.addAll(this.scanPackage(packageName));
        }
        return tempControllerNames;
    }

    // 测试 scanPackage 递归
//    public static void main(String[] args) {
//        final DispatcherServlet ds = new DispatcherServlet();
//        final List<String> re = ds.scanPackage("com.minis.test");
//        for (String s : re) {
//            System.out.println("s = " + s);
//        }
//    }

    private List<String> scanPackage(String packageName) {
        final List<String> tempControllerNames = new ArrayList<>();
        URI uri = null;
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final File dir = new File(uri);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) { // 对子目录进行递归扫描
                final List<String> dirRe = scanPackage(packageName + "." + file.getName());
                // 将递归结果放入结果集
                tempControllerNames.addAll(dirRe);
            } else { // 类文件
                final String controllerName = packageName + "." + file.getName().replaceAll(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doGet.......");
        final String requestPath = request.getServletPath();
        System.out.println("requestPath: " + requestPath);
        if (!this.urlMappingNames.contains(requestPath)) {
            response.getWriter().append("This path does not exist......");
            System.out.println("碰到问题，就去 Debug.......");
            return;
        }

        Object obj = null;
        Object objResult = null;

        try {
            final Method method = this.mappingMethods.get(requestPath);
            obj = this.mappingObjs.get(requestPath);
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
