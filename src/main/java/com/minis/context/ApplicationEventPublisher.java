package com.minis.context;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
}
