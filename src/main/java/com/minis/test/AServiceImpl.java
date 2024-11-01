package com.minis.test;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class AServiceImpl implements AService {
    private String property1;
    private String property2;
    private String name;
    private int level;
    private BaseService ref1;

    public AServiceImpl() {
    }

    public AServiceImpl(String name, int level) {
        this.name = name;
        this.level = level;
        System.out.println("AServiceImpl constructor param -> " + this.name + "," + this.level);
    }

    @Override
    public void sayHello() {
        System.out.println(this.property1 + "," + this.property2);
    }

    public BaseService getRef1() {
        return ref1;
    }

    public void setRef1(BaseService bs) {
        this.ref1 = bs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public String getProperty1() {
        return property1;
    }

    public String getProperty2() {
        return property2;
    }
}
