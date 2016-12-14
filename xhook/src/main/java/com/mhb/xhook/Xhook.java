package com.mhb.xhook;

import android.content.Context;

import com.mhb.xhook.config.GlobalConfig;
import com.mhb.xhook.logging.AndroidXhookLog;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.NullXhookLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.nativehook.HookCallbacks;
import com.mhb.xhook.nativehook.HookManager;

import java.lang.reflect.Method;

import okhttp3.internal.http.HttpEngine;

public class Xhook {

    private static final BasicLog LOG = XhookLogManager.getInstance();
    private static final HookManager HOOK_MANAGER = HookManager.getInstance();
    private static boolean started = false;
    private boolean loggingEnabled;
    private int logLevel;
    private String token;
    private static boolean javaHook = false;
    private static boolean nativeHook = false;

    private Xhook(String token) {
        // TODO:
        loggingEnabled = true;
        // TODO:
        logLevel = 3;
        // TODO:
        this.token = token;
    }

    public static Xhook withToken(String token) {
        return new Xhook(token);
    }

    public Xhook withLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
        return this;
    }

    public Xhook withLogLevel(int level) {
        logLevel = level;
        return this;
    }

    public Xhook withJavaHook(boolean enabled) {
        javaHook = enabled;
        return this;
    }

    public Xhook withNativeHook(boolean enabled) {
        nativeHook = enabled;
        return this;
    }

    public void start(Context context) {
        // 如果已经开启，将不再重复开启
        if (started) {
            LOG.debug("Xhook " + GlobalConfig.VERSION + "." + GlobalConfig.MINOR_VERSION + " is already running.");
            return;
        }
        try {
            // LOG init
            BasicLog basicLog;
            if (loggingEnabled) {
                basicLog = new AndroidXhookLog();
            } else {
                basicLog = new NullXhookLog();
            }
            XhookLogManager.setXhookLog(basicLog);
            LOG.setLevel(logLevel);

            // Xhook init
            initialize(context);
            LOG.info("Start Xhook " + GlobalConfig.VERSION + "." + GlobalConfig.MINOR_VERSION);
            started = true;
        } catch (Throwable e) {
            LOG.error("Error occurred while starting the Xhook!", e);
        }


    }

    public void initialize(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        String libPath = "/data/data/" + packageName + "/lib/" + GlobalConfig.LIB_NAME;
        LOG.debug("libPath=" + libPath);
        HOOK_MANAGER.initNativeHook(libPath, android.os.Build.VERSION.RELEASE, HOOK_MANAGER.getVmVersion());
        HOOK_MANAGER.registerCallbackClass(HookCallbacks.class);
        try {
            Class<?> clazz = null;
            try {
                // TODO: need to make sure class is loaded
                clazz = Class.forName("okhttp3.internal.http.Http1xStream");
                Method method = clazz.getDeclaredMethod("setHttpEngine", HttpEngine.class);
                HOOK_MANAGER.replaceMethod(method, "setHttpEngine");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            LOG.error(e.toString());
        }
    }

}
