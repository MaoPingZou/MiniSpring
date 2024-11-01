package com.minis.test;

import com.minis.beans.factory.annotation.Autowired;

/**
 * @author 邹茂萍
 * @date 2024-10-31
 */
public class BaseService {

    @Autowired
    private BaseBaseService bbs;

    public BaseBaseService getBbs() {
        return this.bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public BaseService() {
    }

    public void sayHello() {
        System.out.print("Base Service says hello");
        bbs.sayHello();
    }
}
