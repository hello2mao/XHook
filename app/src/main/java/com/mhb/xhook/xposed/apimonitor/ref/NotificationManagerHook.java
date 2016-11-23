package com.mhb.xhook.xposed.apimonitor.ref;


import android.app.Notification;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class NotificationManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        Method notifyMethod = RefInvoke.findMethodExact("android.app.NotificationManager", ClassLoader.getSystemClassLoader(), "notify",int.class,Notification.class);
        hookHelper.hookMethod(notifyMethod, new AbstractBehaviorHookCallBack() {
            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                Notification notification = (Notification) param.args[1];
                LOG.debug("Send Notification ->");
                LOG.debug(notification.toString());
            }
        });
    }

}
