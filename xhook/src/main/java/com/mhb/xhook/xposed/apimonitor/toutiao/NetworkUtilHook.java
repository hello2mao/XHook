package com.mhb.xhook.xposed.apimonitor.toutiao;

import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.collecter.ModuleContext;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class NetworkUtilHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        Class<?> p1Class = null;
        Class<?> p2Class = null;
        Class<?> p3Class = null;
        Class<?> p4Class = null;
        try {
            p1Class = XposedHelpers.findClass("com.ss.android.common.util.NetworkUtils",
                    ModuleContext.getInstance().getBaseClassLoader());
            p2Class = XposedHelpers.findClass("com.ss.android.http.legacy.b.f",
                    ModuleContext.getInstance().getBaseClassLoader());
            p3Class = XposedHelpers.findClass("com.ss.android.common.http.RequestContext",
                    ModuleContext.getInstance().getBaseClassLoader());
        } catch (XposedHelpers.ClassNotFoundError e) {
            e.printStackTrace();
        }

        if ((p1Class != null) && (p2Class != null) && (p3Class != null)) {
            Method method = RefInvoke.findMethodExact(
                    "com.ss.android.common.util.NetworkUtils",
                    ModuleContext.getInstance().getBaseClassLoader(),
                    "executeGet",
                    int.class,
                    String.class,
                    boolean.class,
                    boolean.class,
                    List.class,
                    p2Class,
                    boolean.class,
                    p3Class);
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
            LOG.debug("beforeHookedMethod");

        }

        @Override
        public void afterHookedMethod(HookParam param) {
            LOG.debug("afterHookedMethod");
            String response = (String) param.getResult();
            if (response != null) {
                LOG.debug("NetworkUtils GET response: " + response);
            }

        }
    }

}