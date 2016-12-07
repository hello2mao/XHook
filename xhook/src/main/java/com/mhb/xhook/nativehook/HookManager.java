package com.mhb.xhook.nativehook;

import com.mhb.xhook.config.GlobalConfig;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.networklib.WebEventStore;
import com.mhb.xhook.util.DeviceCheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HookManager {

    private static boolean isLoadLibrary;
    private static int vmVersion = -1;
    private static Class<?> callbackClass = null;
    private static Map<String, Method> methodCache = new HashMap<>();
    private static final BasicLog LOG = XhookLogManager.getInstance();
    private volatile static HookManager instance = null;

    static {
        try {
            System.loadLibrary("xhooknative");
            isLoadLibrary = true;
            LOG.info("Load xhooknative success! LIB_VERSION = " + GlobalConfig.LIB_VERSION);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            LOG.error("Nativie library not found! Please copy xhooknative into your project");
            isLoadLibrary = false;
        } catch (Throwable t) {
            isLoadLibrary = false;
            LOG.error("Failed to load library: " + t.getMessage());
        }

        if (DeviceCheck.isDalvikMode()) {
            // dalvik vm
            LOG.debug("Dalvik vm");
        } else if (android.os.Build.VERSION.RELEASE.startsWith("4.4") ) {
            vmVersion = 0;
            LOG.debug("ART 4.4");
        } else if (android.os.Build.VERSION.RELEASE.startsWith("5.0")) {
            vmVersion = 1;
            LOG.debug("ART 5.0");
        } else if (android.os.Build.VERSION.RELEASE.startsWith("5.1")) {
            vmVersion = 2;
            LOG.debug("ART 5.1");
        }
    }



    private HookManager() {}

    public static HookManager getInstance() {
        if (null == instance) {
            synchronized (XhookLogManager.class) {
                if (null == instance) {
                    instance = new HookManager();
                }
            }
        }
        return instance;
    }

    public int getVmVersion() {
        return vmVersion;
    }

    public synchronized void callback(final int fd,
                                      final int type,
                                      final double startTime,
                                      final int timeElapsed,
                                      final int returnValue,
                                      final int errorNum,
                                      final String host,
                                      final String address,
                                      final String desc,
                                      final int port) {
        LOG.debug("HookManager: in callback");
        WebEventStore.setSocketEvent(fd, type, startTime, timeElapsed, returnValue, errorNum, host,
                address, desc, port);
    }


    public void registerCallbackClass(Class<?> callback) {
        if (callbackClass != null) {
            throw new RuntimeException("CallbackClass has been registered");
        }
        callbackClass = callback;
    }

    public void replaceMethod(Method origin, String proxy) {
        if (callbackClass == null) {
            throw new NullPointerException("CallbackClass hasn't been registered yet");
        }

        Method[] ms = callbackClass.getDeclaredMethods();
        for (Method m : ms) {
            if (m.getName().equals(proxy)) {
                if (methodCache.get(proxy) != null) {
                    throw new IllegalArgumentException("hook " + proxy + " duplicated");
                }
                methodCache.put(proxy, m);
                hookMethod(origin, m);
                return;
            }
        }
        throw new IllegalArgumentException("didn't find " + proxy + " in " + callbackClass);
    }

    public Object invokeOrigin(String methodName, Object receiver, Object... args) {
        Method m = methodCache.get(methodName);
        if (methodName == null) {
            throw new RuntimeException(methodName + " has not been used to hook, please verify");
        }
        if (vmVersion < 0) {
            return invokeDvmMethod(m, receiver, args, m.getParameterTypes(), m.getReturnType());
        }
        try {
            m.setAccessible(true);
            return m.invoke(receiver, args);
        } catch (IllegalAccessException e) {
            LOG.debug(e.toString());
        } catch (IllegalArgumentException e) {
            LOG.debug(e.toString());
        } catch (InvocationTargetException e) {
            LOG.debug(e.toString());
        }
        return null;
    }

    public Object retrieveReceiver(Object thiz, boolean isStatic) {
        if (isStatic) {
            return null;
        }
        return (Object) thiz;
    }

    /**
     *
     * @param origin
     * @param proxy
     */
    private native void hookMethod(Method origin, Method proxy);

    /**
     *
     * @param method
     * @param receiver
     * @param args
     * @param typeParameter
     * @param returnType
     * @return
     */
    // TODO:static method
    private static native Object invokeDvmMethod(Method method, Object receiver, Object[] args,
                                                 Class<?>[] typeParameter, Class<?> returnType);

    /**
     * init Native
     * @param libPath     xhooknative.so path
     * @param release     Android version
     * @param vmVersion   vm version
     */
    public native void initNativeHook(String libPath, String release, int vmVersion);

}
