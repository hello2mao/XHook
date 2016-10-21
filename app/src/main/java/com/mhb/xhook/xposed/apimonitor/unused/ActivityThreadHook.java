package com.mhb.xhook.xposed.apimonitor.unused;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class ActivityThreadHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        try {
            Class receiverDataClass = Class.forName("android.app.ActivityThread$ReceiverData");
            if (receiverDataClass != null) {
                Method handleReceiverMethod = RefInvoke.findMethodExact("android.app.ActivityThread", ClassLoader.getSystemClassLoader(),
                        "handleReceiver", receiverDataClass);
                hookHelper.hookMethod(handleReceiverMethod, new AbstractBehaviorHookCallBack() {

                    @Override
                    public void descParam(HookParam param) {
                        LOG.debug("The Receiver Information:");
                        Object data = param.args[0];
                        LOG.debug(data.toString());

                    }
                });
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
