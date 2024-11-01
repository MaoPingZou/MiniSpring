package com.minis.context;

/**
 * @author 邹茂萍
 * @date 2024-11-01
 */
public class ContextRefreshEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public ContextRefreshEvent(Object arg0) {
        super(arg0);
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
