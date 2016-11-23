package com.mhb.xhook.xposed.apimonitor.ref;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AlarmManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // public void set(int type, long triggerAtMillis, PendingIntent operation)
        Method setImplmethod = RefInvoke.findMethodExact(
                "android.app.AlarmManager", ClassLoader.getSystemClassLoader(),
                "set", int.class, long.class, PendingIntent.class);
        hookHelper.hookMethod(setImplmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                LOG.debug("The Alarm Information:");
                PendingIntent intent = (PendingIntent) param.args[2];
                descPendingIntent(intent);
                LOG.debug("type = "+param.args[0]);
                LOG.debug("triggerAtMillis = "+param.args[1]);
            }
        });

    }

    private void descPendingIntent(PendingIntent pintent){
        Method getIntentMethod = RefInvoke.findMethodExact(
                "android.app.PendingIntent", ClassLoader.getSystemClassLoader(),
                "getIntent");
        try {
            Intent intent = (Intent) getIntentMethod.invoke(pintent, new Object[]{});
            ComponentName cn = intent.getComponent();
            if(cn != null){
                LOG.debug("The ComponentName = "+cn.getPackageName()+"/"+cn.getClassName());
            }
            LOG.debug("The Intent Action = "+intent.getAction());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
