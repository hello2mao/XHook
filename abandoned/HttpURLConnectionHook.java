package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by maohongbin01 on 16/7/26.
 */
public class HttpURLConnectionHook extends MethodHook {

    private static final String mClassName = "java.net.HttpURLConnection";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();

    private HttpURLConnectionHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // void setRequestMethod(String method)
    // ==>GET/POST/HEAD/OPTIONS/PUT/DELETE/TRACE
    // libcore/luni/src/main/java/java/net/HttpURLConnection.java
    // https://developer.android.com/reference/java/net/HttpURLConnection.html

    private enum Methods {
        setRequestMethod
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new HttpURLConnectionHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
        log.debug("hook java.net.HttpURLConnection");

    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = null;

        if (mMethod == Methods.setRequestMethod) {
            argNames = "method";
            // setRequestMethod在HttpURLConnection中实现，HttpsURLConnection继承了HttpURLConnection，
            // 也继承了这个方法，没有覆写

        }

//        methodLog(uid, param, argNames);
    }
}
