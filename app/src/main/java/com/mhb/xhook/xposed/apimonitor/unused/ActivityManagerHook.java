package com.mhb.xhook.xposed.apimonitor.unused;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class ActivityManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method killBackgroundProcessesmethod = RefInvoke.findMethodExact(
                "android.app.ActivityManager", ClassLoader.getSystemClassLoader(),
                "killBackgroundProcesses", String.class);
        hookHelper.hookMethod(killBackgroundProcessesmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                String packageName = (String) param.args[0];
                LOG.debug("kill packagename = "+packageName);
            }
        });

        Method forceStopPackagemethod = RefInvoke.findMethodExact(
                "android.app.ActivityManager", ClassLoader.getSystemClassLoader(),
                "forceStopPackage", String.class);
        hookHelper.hookMethod(forceStopPackagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                String packageName = (String) param.args[0];
                LOG.debug("kill packagename = "+packageName);
            }
        });
    }

}
