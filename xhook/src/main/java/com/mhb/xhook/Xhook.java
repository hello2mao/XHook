package com.mhb.xhook;

import android.content.Context;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.nativehook.HookCallbacks;
import com.mhb.xhook.nativehook.HookManager;

import java.lang.reflect.Method;

import okhttp3.Request;
import okhttp3.internal.http.HttpEngine;

public class Xhook {
    private static final BasicLog LOG = XHookLogManager.getInstance();
    private String token;

    private Xhook(String token) {
        this.token = token;
    }

    public static Xhook withToken(String token) {
        return new Xhook(token);
    }

    public Xhook withJavaHook(boolean enabled) {
        return this;
    }

    public void start(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        LOG.debug("libPath=" + "/data/data/" + packageName + "/lib/libxhooknative.so");
        new HookManager().initNativeHook("/data/data/" + packageName + "/lib/libxhooknative.so",
                android.os.Build.VERSION.RELEASE, HookManager.getVmVersion());
        HookManager.registerCallbackClass(HookCallbacks.class);
        try {
//            Method m = getClass().getDeclaredMethod("victim", int.class, long.class, char.class);
//            HookManager.replaceMethod(m, "victim");
            Class<?> clazz = null;
            try {
                // TODO: need to make sure class is loaded
                clazz = Class.forName("okhttp3.internal.http.Http1xStream");
                Method method = clazz.getDeclaredMethod("writeRequestHeaders", Request.class);
                HookManager.replaceMethod(method, "writeRequestHeaders");
                Method method2 = clazz.getDeclaredMethod("setHttpEngine", HttpEngine.class);
                HookManager.replaceMethod(method2, "setHttpEngine");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            LOG.error(e.toString());
        }
    }

}
