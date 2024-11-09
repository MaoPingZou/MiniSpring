package com.minis.test;

import com.minis.web.RequestMapping;

/**
 * @author 邹茂萍
 * @date 2024-11-04
 */
public class HelloWorldBean {

    @RequestMapping("/test")
    public String doTest() {
        return "hello world for doGet!";
    }

    public String doGet() {
        return "hello world!";
    }

    public String doPost() {
        return "hello world!";
    }

}
