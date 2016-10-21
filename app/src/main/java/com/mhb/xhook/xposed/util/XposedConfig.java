package com.mhb.xhook.xposed.util;

public class XposedConfig {

    private static final String DEFAULT_SELF_PACKAGE_NAME = "com.mhb.xhook";
    private static final String DEFAULT_HOOK_TARGET_APP = "com.android.mms";

    private static XposedConfig instance = null;
    private String selfPackageName;
    private String hookTargetApp;

    private XposedConfig() {
        this.selfPackageName = DEFAULT_SELF_PACKAGE_NAME;
        this.hookTargetApp = DEFAULT_HOOK_TARGET_APP;
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
