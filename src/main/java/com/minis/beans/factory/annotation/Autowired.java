package com.minis.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 邹茂萍
 * @date 2024-10-31
 */
@Target(ElementType.FIELD) // 修饰成员变量
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface Autowired {
}
