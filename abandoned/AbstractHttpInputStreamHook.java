package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by maohongbin01 on 16/7/27.
 */
public class AbstractHttpInputStreamHook extends MethodHook{

    private static final String mClassName = "libcore.net.http.AbstractHttpInputStream";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();

    private AbstractHttpInputStreamHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    //

    private enum Methods {
        read
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for(Methods method : Methods.values())
            methodHookList.add(new AbstractHttpInputStreamHook(method));

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
        log.debug("AbstractHttpInputStreamHook");
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
        int uid = Binder.getCallingUid();
        String argNames = null;

        if (mMethod == Methods.read) {
            if (param.args.length == 0) {
                argNames = "";
            } else if (param.args.length == 1) {
                argNames = "b";
            } else if (param.args.length == 3) {
                argNames = "b|off|len";
            }
        }
//        methodLog(uid, param, argNames);
    }
}
