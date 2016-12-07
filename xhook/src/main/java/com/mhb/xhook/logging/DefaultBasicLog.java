package com.mhb.xhook.logging;

import com.mhb.xhook.config.GlobalConfig;


public class DefaultBasicLog implements BasicLog {

    public static final String DEBUG_TAG = GlobalConfig.CONF_TAG;

    private BasicLog impl;

    public DefaultBasicLog() {
        impl = new NullXhookLog();
    }

    public void setImpl(BasicLog impl) {
        synchronized (this) {
            this.impl = impl;
        }
    }

    public void debug(String message) {
        synchronized (this) {
            impl.debug(message);
        }
    }

    public void info(String message) {
        synchronized (this) {
            impl.info(message);
        }
    }

    public void verbose(String message) {
        synchronized (this) {
            impl.verbose(message);
        }
    }

    public void warning(String message) {
        synchronized (this) {
            impl.warning(message);
        }
    }

    public void error(String message) {
        synchronized (this) {
            impl.error(message);
        }
    }

    public void error(String message, Throwable cause) {
        synchronized (this) {
            impl.error(message, cause);
        }
    }

    public int getLevel() {
        synchronized (this) {
            return impl.getLevel();
        }

    }

    public void setLevel(int level) {
        synchronized (this) {
            impl.setLevel(level);
        }
    }
}
