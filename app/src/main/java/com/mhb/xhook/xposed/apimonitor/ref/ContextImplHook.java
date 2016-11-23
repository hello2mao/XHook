package com.mhb.xhook.xposed.apimonitor.ref;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;
import java.util.Iterator;

public class ContextImplHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        Method registerReceivermethod = RefInvoke.findMethodExact(
                "android.app.ContextImpl", ClassLoader.getSystemClassLoader(),
                "registerReceiver", BroadcastReceiver.class,IntentFilter.class);
        hookHelper.hookMethod(registerReceivermethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Register BroatcastReceiver");
                LOG.debug("The BroatcastReceiver ClassName = "+param.args[0].getClass().toString());
                if(param.args[1] != null){
                   String intentstr = descIntentFilter((IntentFilter) param.args[1]);
                   LOG.debug("Intent Action = ["+intentstr+"]");
                }
            }
        });
    }

    public String descIntentFilter(IntentFilter intentFilter){
        StringBuilder sb = new StringBuilder();
        Iterator<String> actions =intentFilter.actionsIterator();
        String action = null;
        while(actions.hasNext()){
            action = actions.next();
            sb.append(action+",");
        }
        return sb.toString();

    }

}
