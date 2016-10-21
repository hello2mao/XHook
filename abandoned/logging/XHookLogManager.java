package com.mhb.xhook.logging;

public class XHookLogManager {

    private static DefaultBasicLog instance = null;

    private XHookLogManager() {}

    public static BasicLog getInstance() {
        if (null == instance) {
            synchronized (XHookLogManager.class) {
                if (null == instance) {
                    instance = new DefaultBasicLog();
                }
            }
        }
        return instance;
    }
}
