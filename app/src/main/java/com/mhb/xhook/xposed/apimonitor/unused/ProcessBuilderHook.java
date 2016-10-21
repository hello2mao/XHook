package com.mhb.xhook.xposed.apimonitor.unused;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;
import java.util.List;

public class ProcessBuilderHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        Method execmethod = RefInvoke.findMethodExact(
                "java.lang.ProcessBuilder", ClassLoader.getSystemClassLoader(),
                "start");
        hookHelper.hookMethod(execmethod, new AbstractBehaviorHookCallBack() {
            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Create New Process ->");
                ProcessBuilder pb = (ProcessBuilder) param.thisObject;
                List<String> cmds = pb.command();
                StringBuilder sb = new StringBuilder();
                for(int i=0 ;i <cmds.size(); i++){
                   sb.append("CMD"+i+":"+cmds.get(i)+" ");
                }
                LOG.debug("Command" + sb.toString());
            }
        });
    }

}
