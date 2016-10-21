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
public class ResponseHeadersHook extends MethodHook {

    private static final String mClassName = "libcore.net.http.ResponseHeaders";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();

    private ResponseHeadersHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // public RawHeaders getHeaders()

    private enum Methods {
        getHeaders
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new ResponseHeadersHook(method));
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

//        if (mMethod == Methods.getHeaders) {
//            argNames = "";
//            URLHook.getElementData().setResponseHeader(param.getResult().toString());
//        }

//        methodLog(uid, param, argNames);
    }
}
