package com.mhb.xhook.xposed.apimonitor.ref;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class ConnectivityManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method setMobileDataEnabledmethod = RefInvoke.findMethodExact(
                "android.net.ConnectivityManager", ClassLoader.getSystemClassLoader(),
                "setMobileDataEnabled",boolean.class);
        hookHelper.hookMethod(setMobileDataEnabledmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                boolean status = (Boolean) param.args[0];
                LOG.debug("Set MobileDataEnabled = "+status);
            }
        });

    }

}
