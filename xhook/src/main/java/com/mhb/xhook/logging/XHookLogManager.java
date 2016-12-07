package com.mhb.xhook.logging;

public class XhookLogManager {

    private volatile static DefaultBasicLog instance = null;

    private XhookLogManager() {}

    public static BasicLog getInstance() {
        if (null == instance) {
            synchronized (XhookLogManager.class) {
                if (null == instance) {
                    instance = new DefaultBasicLog();
                }
            }
        }
        return instance;
    }

    public static void setXhookLog(BasicLog instance2) {
        instance.setImpl(instance2);
    }
}
