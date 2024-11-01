package com.minis.context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class SimpleApplicationEventPublisher implements ApplicationEventPublisher {
    List<ApplicationListener> listeners = new ArrayList<>();

    @Override
    public void publishEvent(ApplicationEvent event) {
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.listeners.add(listener);
    }
}
