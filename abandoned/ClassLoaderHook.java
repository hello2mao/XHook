package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ClassLoaderHook extends MethodHook {

    private static final String mClassName = "java.lang.ClassLoader";

    private ClassLoaderHook(Methods method) {
        super(mClassName, method.name());
    }

    // @formatter:off
    // public Class<?> loadClass(String className) throws ClassNotFoundException
    // libcore/libart/src/main/java/java/lang/ClassLoader.java
    // @formatter:on

    private enum Methods {

        loadClass
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<MethodHook>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new ClassLoaderHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        if (uid <= 1000) {
            return;
        }

        if (param.args.length != 1) {
            return;
        }

    }
}
