package com.mhb.xhook.xposed.apimonitor.unused;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.io.File;
import java.lang.reflect.Method;

public class RuntimeHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method execmethod = RefInvoke.findMethodExact(
                "java.lang.Runtime", ClassLoader.getSystemClassLoader(),
                "exec", String[].class,String[].class,File.class);
        hookHelper.hookMethod(execmethod, new AbstractBehaviorHookCallBack() {
            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Create New Process ->");
                String[] progs = (String[]) param.args[0];
                for(int i=0 ;i <progs.length; i++){
                   LOG.debug("Command" + i + " = "+progs[i]);
                }
            }
        });

    }

}
