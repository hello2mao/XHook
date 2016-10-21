package com.mhb.xhook.xposed.hook;

import android.os.Process;

import com.mhb.xhook.hookclass.network.inet.InetAddressHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class HookTarget {

    private static final BasicLog LOG = XHookLogManager.getInstance();

    /**
     * Called when zygote initialize
     */
    public static void hookWhenZygoteInit() {
        hookGlobalSystemAPIs();
    }

    /**
     * Called when target package loaded
     */
    public static void hookWhenPackageLoaded() {
        hookTargetAppAPIs();

    }

    /**
     * Hook global system APIs
     */
    private static void hookGlobalSystemAPIs() {
        LOG.debug("start to hook global system APIs");

        // TODO:Add system APIs below which you want to hook when zygote initialize
        // Instrumentation - StartActivity
        // hookAll(InstrumentationHook.getMethodHookList());

        LOG.debug("end of hook global system APIs");
    }

    /**
     * Hook target App APIs
     */
    private static void hookTargetAppAPIs() {
        LOG.debug("start to hook target app APIs");

        // TODO: add target app APIs which you need to hook
        // Hook http
//        hookAll(URLHook.getMethodHookList());
//        hookAll(HttpURLConnectionImplHook.getMethodHookList());
        hookAll(InetAddressHook.getMethodHookList());
//        hookAll(SocketHook.getMethodHookList());
//        hookAll(RetryableOutputStreamHook.getMethodHookList());
//        hookAll(FixedLengthInputStreamHook.getMethodHookList());
//        hookAll(AbstractHttpClientHook.getMethodHookList());
//        hookAll(AbstractSessionInputBufferHook.getMethodHookList());
//        hookAll(AbstractSessionOutputBufferHook.getMethodHookList());
//        hookAll(InputStreamHook.getMethodHookList());
//        hookAll(OutputStreamHook.getMethodHookList());

        // Hook findLibrary method to invoke the native lib : findLibrary
//        hookAll(BaseDexClassLoaderHook.getMethodHookList());
//        // Hook android.app.Application.onCreate to hook system native lib : onCreate => initSystemNativeHook
//        hookAll(ApplicationHook.getMethodHookList());
//        // Hook Runtime to hook custom native lib : exec, load, loadLibrary => initCustomNativeHook
//        hookAll(RuntimeHook.getMethodHookList());

        // TODO:
        // Anti anti emulator
//        Util.hookBuildFields();

        // Hook classloader : loadClass
//        hookAll(ClassLoaderHook.getMethodHookList());

        // TODO:
        // Hook IO
        // Waring: do not hook this class at the beginning of zygote init,
        // or it will cause UnsatisfiedLinkException when load libnative.so
//            hookAll(IoBridgeHook.getMethodHookList());

        // Hook customized apis
//            hookCustomizedSystemApis();
//            hookCustomizedAppApis(appInfo.packageName, lpparam.classLoader);

        LOG.debug("end of hook target app APIs");
    }

    /**
     * hook all from the methodHookList
     */
    private static void hookAll(List<MethodHook> methodHookList) {
        for (MethodHook methodHook : methodHookList)
            hook(methodHook);
    }

    /**
     * hook method with default classLoader
     */
    private static void hook(MethodHook methodHook) {
        hook(methodHook, null);
    }

    /**
     * hook
     */
    private static void hook(final MethodHook methodHook, ClassLoader classLoader) {
        try {
            // Create hook method
            XC_MethodHook xcMethodHook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (Process.myUid() <= 0) {
                            return;
                        }
                        methodHook.before(param);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable())
                        try {
                            if (Process.myUid() <= 0) {
                                return;
                            }
                            methodHook.after(param);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                }
            };

            // Find hook class
            Class<?> hookClass = null;
            try {
                hookClass = findClass(methodHook.getClassName(), classLoader);
            } catch (XposedHelpers.ClassNotFoundError e) {
                e.printStackTrace();
            }
            if (hookClass == null) {
                LOG.error("Hook-Class not found: " + methodHook.getClassName());
                return;
            }

            // Add hook
            // Only methods and constructors can be hooked
            // Cannot hook interfaces
            // Cannot hook abstract methods
            if (methodHook.getMethodName().equals(hookClass.getSimpleName())) {
                for (Constructor<?> constructor : hookClass.getDeclaredConstructors()){
                    LOG.debug("hook constructor: " + constructor.toString());
                    XposedBridge.hookMethod(constructor, xcMethodHook);
                }
            } else {
                for (Method method : hookClass.getDeclaredMethods())
                    if (method.getName().equals(methodHook.getMethodName())) {
                        if (Modifier.isAbstract(method.getModifiers())) {
                            LOG.warning("can not hook abstract method: " + method.toString());
                        } else {
                            LOG.debug("hook method: " + method.toString());
                            XposedBridge.hookMethod(method, xcMethodHook);
                        }
                    }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
