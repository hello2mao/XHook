package com.mhb.xhook.xposed.hook;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;

import java.lang.reflect.Member;

import de.robv.android.xposed.XposedBridge;


public class XposeHookHelperImpl implements HookHelperInterface {

    protected static final BasicLog LOG = XhookLogManager.getInstance();

    @Override
    public void hookMethod(Member method, MethodHookCallBack callback) {
        LOG.debug("hook method: " + method.toString());
        XposedBridge.hookMethod(method, callback);
    }

}
