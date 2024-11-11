package com.test;

import com.minis.web.RequestMapping;

/**
 * @author 邹茂萍
 * @date 2024-11-11
 */
public class HelloWorldBean {
    @RequestMapping("/test")
    public String doTest() {
        System.out.println("走这了没------------<<<<");
        return "hello world for doGet!";
    }
}
