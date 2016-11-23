package com.mhb.xhook.xposed.apimonitor.ref;


import android.telephony.PhoneStateListener;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class TelephonyManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method getLine1Numbermethod = RefInvoke.findMethodExact(
                "android.telephony.TelephonyManager", ClassLoader.getSystemClassLoader(),
                "getLine1Number");
        hookHelper.hookMethod(getLine1Numbermethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                LOG.debug("Read PhoneNumber ->");
            }
        });

        Method listenMethod = RefInvoke.findMethodExact(
                "android.telephony.TelephonyManager", ClassLoader.getSystemClassLoader(),
                "listen", PhoneStateListener.class,int.class);
        hookHelper.hookMethod(listenMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Listen Telephone State Change ->");
                LOG.debug("PhoneStateListener ClassName = "+param.args[0].getClass().getName());
                int event =  (Integer) param.args[1];
                if((event&PhoneStateListener.LISTEN_CELL_LOCATION) != 0){
                    LOG.debug("Listen Enent = "+"LISTEN_CELL_LOCATION");
                }
                if((event&PhoneStateListener.LISTEN_SIGNAL_STRENGTHS) != 0){
                    LOG.debug("Listen Enent = "+"LISTEN_SIGNAL_STRENGTHS");
                }
                if((event&PhoneStateListener.LISTEN_CALL_STATE) != 0){
                    LOG.debug("Listen Enent = "+"LISTEN_CALL_STATE");
                }
                if((event&PhoneStateListener.LISTEN_DATA_CONNECTION_STATE) != 0){
                    LOG.debug("Listen Enent = "+"LISTEN_DATA_CONNECTION_STATE");
                }
                if((event&PhoneStateListener.LISTEN_CELL_LOCATION) != 0){
                    LOG.debug("Listen Enent = "+"LISTEN_SERVICE_STATE");
                }

            }
        });

    }

}
