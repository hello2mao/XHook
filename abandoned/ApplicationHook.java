package com.mhb.xhook.hookclass.abandoned;


import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.xposed.util.NativeEntry;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class ApplicationHook extends MethodHook {

    private static final String mClassName = "android.app.Application";

    public ApplicationHook(Methods method) {
        super(mClassName, method.name());
    }

    // public void onCreate()
    // frameworks/base/core/java/android/app/Application.java
    // http://developer.android.com/reference/android/app/Application.html

    private enum Methods {
        onCreate
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<MethodHook>();
        methodHookList.add(new ApplicationHook(Methods.onCreate));
        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        String ret = NativeEntry.initSystemNativeHook();
// log.debug(ret);
    }

}
