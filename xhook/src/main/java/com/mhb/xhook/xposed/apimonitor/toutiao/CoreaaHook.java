package com.mhb.xhook.xposed.apimonitor.toutiao;

import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.collecter.ModuleContext;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class CoreaaHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        Class<?> p1Class = null;
        Class<?> p2Class = null;
        Class<?> p3Class = null;
        Class<?> p4Class = null;
        try {
            p1Class = XposedHelpers.findClass("com.bytedance.frameworks.core.a.d",
                    ModuleContext.getInstance().getBaseClassLoader());
            p2Class = XposedHelpers.findClass("com.bytedance.frameworks.core.a.a",
                    ModuleContext.getInstance().getBaseClassLoader());
        } catch (XposedHelpers.ClassNotFoundError e) {
            e.printStackTrace();
        }

        if ((p1Class != null) && (p2Class != null)) {
            Method method = RefInvoke.findMethodExact(
                    "com.bytedance.frameworks.core.a.a",
                    ModuleContext.getInstance().getBaseClassLoader(),
                    "a",
                    p1Class
                    );
            if (null == method) {
                LOG.error("findMethodExact failed");
                return;
            }
            hookHelper.hookMethod(method, new StartHook());
        } else {
            LOG.debug("class not exist!");
        }


    }

    private class StartHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {
            LOG.debug("CoreaaHook beforeHookedMethod");

        }

        @Override
        public void afterHookedMethod(HookParam param) {
            LOG.debug("CoreaaHook afterHookedMethod");
        }
    }

}