package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.xposed.util.Util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class BaseDexClassLoaderHook extends MethodHook {

    private static final String mClassName = "dalvik.system.BaseDexClassLoader";
    private Methods mMethod = null;

    private BaseDexClassLoaderHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // BaseDexClassLoader extends ClassLoader
    // Public Constructors:
    // public BaseDexClassLoader(String dexPath,File optimizedDirectory, String libraryPath,
    // ClassLoader parent)
    //
    // Public Methods:
    // public String findLibrary(String name)
    //
    // libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
    // http://developer.android.com/reference/dalvik/system/BaseDexClassLoader.html

    private enum Methods {

        BaseDexClassLoader, findLibrary
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for(Methods method : Methods.values())
            methodHookList.add(new BaseDexClassLoaderHook(method));

        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        if (mMethod == Methods.BaseDexClassLoader) {
            String argNames = "dexPath|optimizedDirectory|libraryPath|parent";
// methodLog(uid, param, argNames);
        } else if (mMethod == Methods.findLibrary) {
            String libName = (String) param.args[0];
            // TODO: why ?
            // Set the native lib path
            if (Util.NATIVE_LIB.equals(libName) && param.getResult() == null) {
//                log.debug("Set the native lib path: " + Util.NATIVE_LIB_PATH);
                param.setResult(Util.NATIVE_LIB_PATH);
            }
        }
    }
}
