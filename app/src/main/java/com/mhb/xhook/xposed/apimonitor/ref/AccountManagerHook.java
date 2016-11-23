package com.mhb.xhook.xposed.apimonitor.ref;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class AccountManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method getAccountsMethod = RefInvoke.findMethodExact(
                "android.accounts.AccountManager", ClassLoader.getSystemClassLoader(),
                "getAccounts");
        hookHelper.hookMethod(getAccountsMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Get Account ->");
            }
        });

        Method getAccountsByTypeMethod = RefInvoke.findMethodExact(
                "android.accounts.AccountManager", ClassLoader.getSystemClassLoader(),
                "getAccountsByType",String.class);
        hookHelper.hookMethod(getAccountsByTypeMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                String type = (String) param.args[0];
                LOG.debug("Get Account By Type ->");
                LOG.debug("type :" +type);
            }
        });
    }

}
