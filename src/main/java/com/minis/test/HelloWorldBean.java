package com.minis.test;

/**
 * @author 邹茂萍
 * @date 2024-11-04
 */
public class HelloWorldBean implements IHelloWorldBean {

    public String doGet() {
        return "hello world!";
    }

    public String doPost() {
        return "hello world!";
    }

}
