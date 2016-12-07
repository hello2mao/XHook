package com.mhb.xhook.xposed.apimonitor;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.xposed.hook.HookHelperFactory;
import com.mhb.xhook.xposed.hook.HookHelperInterface;

public abstract class ApiMonitorHook {

    protected HookHelperInterface hookHelper = HookHelperFactory.getHookHelper();
    protected static final BasicLog LOG = XhookLogManager.getInstance();

    public abstract void startHook();
}
