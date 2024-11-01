package com.minis.core.env;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public interface Environment extends PropertyResolver {
    String[] getActiveProfiles();

    String[] getDefaultProfiles();

    // 检查当前环境是否接受指定的配置文件（profiles）
    // 可以用于动态决定哪些配置应该被激活或使用, 以便根据不同的环境（如开发、测试、生产）加载相应的配置
    boolean acceptsProfiles(String... profiles);
}
