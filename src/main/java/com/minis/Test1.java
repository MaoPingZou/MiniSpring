package com.minis;

import com.minis.test.AService;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class Test1 {
    public static void main(String[] args) {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        final AService aService = (AService) ctx.getBean("aService");
        aService.sayHello();
    }
}
