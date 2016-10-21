package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by maohongbin01 on 16/7/25.
 */
public class URLConnectionHook extends MethodHook {

    private static final String mClassName = "java.net.URLConnection";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();

    private URLConnectionHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // public void setDoOutput(boolean dooutput)
    // TODO:abstract seems can not be hook
    // abstract void connect()
    // libcore/luni/src/main/java/java/net/URLConnection.java
    // https://developer.android.com/reference/java/net/URLConnection.html

    private enum Methods {
        setDoOutput
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new URLConnectionHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = null;

        if (mMethod == Methods.setDoOutput) {
            argNames = "dooutput";
        }
        log.debug("mMethod=" + mMethod.toString());

//        methodLog(uid, param, argNames);
    }

}
