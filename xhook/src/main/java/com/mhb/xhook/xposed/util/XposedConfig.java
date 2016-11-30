package com.mhb.xhook.xposed.util;

public class XposedConfig {

    private static final String DEFAULT_SELF_PACKAGE_NAME = "com.mhb.xhook";
    private static final String DEFAULT_HOOK_TARGET_APP = "com.baidu.test";

    private static XposedConfig instance = null;
    private static String selfPackageName = DEFAULT_SELF_PACKAGE_NAME;
    private static String hookTargetApp = DEFAULT_HOOK_TARGET_APP;

    private XposedConfig() {
    }

    public static XposedConfig getInstance() {
        if (null == instance) {
            synchronized (XposedConfig.class) {
                if (null == instance) {
                    instance = new XposedConfig();
                }
            }
        }
        return instance;
    }

    public String getSelfPackageName() {
        return selfPackageName;
    }

    public void setSelfPackageName(String selfPackageName) {
        this.selfPackageName = selfPackageName;
    }

    public String getHookTargetApp() {
        return hookTargetApp;
    }

    public void setHookTargetApp(String hookTargetApp) {
        this.hookTargetApp = hookTargetApp;
    }
}
