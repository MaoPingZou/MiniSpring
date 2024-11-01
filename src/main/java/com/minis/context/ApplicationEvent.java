package com.minis.context;

import java.util.EventObject;

/**
 * @author 邹茂萍
 * @date 2024-10-30
 */
public class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    protected String msg = null;

    public ApplicationEvent(Object arg0) {
        super(arg0);
        this.msg = arg0.toString();
    }
}
