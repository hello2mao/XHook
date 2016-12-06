package com.mhb.xhook.nativehook;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.networklib.WebEventStore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HookManager {
    public static final String LIB_VERSION = "1.0.0";
    private static boolean isLoadLibrary;
    private static int vmVersion = -1;
    private static Class<?> sCallbackClass = null;
    private static Map<String, Method> sMethodCache = new HashMap<>();
    private static final BasicLog LOG = XHookLogManager.getInstance();

    static {
        try {
            System.loadLibrary("xhooknative");
            isLoadLibrary = true;
            LOG.debug("Load xhooknative success! LIB_VERSION = " + LIB_VERSION);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            LOG.error("Nativie library not found! Please copy xhooknative into your project");
            isLoadLibrary = false;
        } catch (Throwable t) {
            isLoadLibrary = false;
            LOG.error("Failed to load library: " + t.getMessage());
        }

        String lib = "";
        try {
            Class<?> properties = Class.forName("android.os.SystemProperties");
            Method get = properties.getDeclaredMethod("get", String.class, String.class);
            lib = (String) get.invoke(null, "persist.sys.dalvik.vm.lib", lib);
        } catch (ClassNotFoundException e) {
            LOG.error(e.toString());
        } catch (NoSuchMethodException e) {
            LOG.error(e.toString());
        } catch (IllegalAccessException e) {
            LOG.error(e.toString());
        } catch (IllegalArgumentException e) {
            LOG.error(e.toString());
        } catch (InvocationTargetException e) {
            LOG.error(e.toString());
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            // dalvik vm
            LOG.debug("dalvik vm");
        } else if (android.os.Build.VERSION.RELEASE.startsWith("4.4") && "libart.so".equals(lib)) {
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

    public static int getVmVersion() {
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


    public static void registerCallbackClass(Class<?> callback) {
        if (sCallbackClass != null) {
            throw new RuntimeException("CallbackClass has been registered");
        }
        sCallbackClass = callback;
    }

    public static void replaceMethod(Method origin, String proxy) {
        if (sCallbackClass == null) {
            throw new NullPointerException("CallbackClass hasn't been registered yet");
        }

        Method[] ms = sCallbackClass.getDeclaredMethods();
        for (Method m : ms) {
            if (m.getName().equals(proxy)) {
                if (sMethodCache.get(proxy) != null) {
                    throw new IllegalArgumentException("hook " + proxy + " duplicated");
                }
                sMethodCache.put(proxy, m);
                hookYposedMethod(origin, m);
                return;
            }
        }
        throw new IllegalArgumentException("didn't find " + proxy + " in " + sCallbackClass);
    }

    public static Object invokeOrigin(String methodName, Object receiver, Object... args) {
        Method m = sMethodCache.get(methodName);
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

    public static Object retrieveReceiver(Object thiz, boolean isStatic) {
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
    private static native void hookYposedMethod(Method origin, Method proxy);

    /**
     *
     * @param method
     * @param receiver
     * @param args
     * @param typeParameter
     * @param returnType
     * @return
     */
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
