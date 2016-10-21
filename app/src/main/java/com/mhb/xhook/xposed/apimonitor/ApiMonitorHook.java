package com.mhb.xhook.xposed.apimonitor;

import com.mhb.xhook.AppConfig;
import com.mhb.xhook.xposed.hook.HookHelperFactory;
import com.mhb.xhook.xposed.hook.HookHelperInterface;

import org.apache.log4j.Logger;

public abstract class ApiMonitorHook {

    protected HookHelperInterface hookHelper = HookHelperFactory.getHookHelper();
    protected static final Logger LOG = Logger.getLogger(AppConfig.CONF_TAG);

    public abstract void startHook();
}
