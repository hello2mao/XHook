package com.mhb.xhook.xposed.hook;

import java.util.EventObject;

public class MethodHookEvent extends EventObject {

    // event source
    private Object source;

    public MethodHookEvent(Object source) {
        super(source);
        this.source = source;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
