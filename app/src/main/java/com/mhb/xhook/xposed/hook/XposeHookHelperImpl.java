package com.mhb.xhook.xposed.hook;

import com.mhb.xhook.AppConfig;

import org.apache.log4j.Logger;

import java.lang.reflect.Member;

import de.robv.android.xposed.XposedBridge;


public class XposeHookHelperImpl implements HookHelperInterface {

    private final Logger log = Logger.getLogger(AppConfig.CONF_TAG);

    @Override
    public void hookMethod(Member method, MethodHookCallBack callback) {
        log.debug("hook method: " + method.toString());
        XposedBridge.hookMethod(method, callback);
    }

}
