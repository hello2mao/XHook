package com.mhb.xhook.xposed.launch;

import com.mhb.xhook.xposed.apimonitor.ApiMonitorHookManager;

public class HookTarget {

    /**
     * Called when zygote initialize
     */
    public static void hookWhenZygoteInit() {

    }

    /**
     * Called when target package loaded
     */
    public static void hookWhenPackageLoaded() {
        ApiMonitorHookManager.getInstance().startMonitor();
    }
}
