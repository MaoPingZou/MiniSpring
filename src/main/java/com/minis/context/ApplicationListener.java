package com.minis.context;

import java.util.EventListener;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class ApplicationListener implements EventListener {
    void onApplicationEvent(ApplicationEvent event) {
        System.out.println(event.toString());
    }
}
