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

import okhttp3.Request;
import okhttp3.internal.http.HttpEngine;

public class Xhook {

    public static final String VERSION = "1.0.0";
    public static final String MINOR_VERSION = "1";
    private static final BasicLog LOG = XhookLogManager.getInstance();
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
            LOG.debug("Xhook " + Xhook.VERSION + "." + Xhook.MINOR_VERSION + " is already running.");
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
            LOG.info("Start Xhook " + Xhook.VERSION + "." + Xhook.MINOR_VERSION);
            started = true;
        } catch (Throwable e) {
            LOG.error("Error occurred while starting the Xhook!", e);
        }


    }

    public void initialize(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        String libPath = "/data/data/" + packageName + "/lib/" + GlobalConfig.LIB_NAME;
        LOG.debug("libPath=" + libPath);
        new HookManager().initNativeHook(libPath, android.os.Build.VERSION.RELEASE, HookManager.getVmVersion());
        HookManager.registerCallbackClass(HookCallbacks.class);
        try {
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
