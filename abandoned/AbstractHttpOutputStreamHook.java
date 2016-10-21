package com.mhb.xhook.hookclass.abandoned;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by maohongbin01 on 16/7/27.
 */
public class AbstractHttpOutputStreamHook extends MethodHook{

    private static final String mClassName = "libcore.net.http.AbstractHttpOutputStream";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();

    private AbstractHttpOutputStreamHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    //

    private enum Methods {
        write
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for(Methods method : Methods.values())
            methodHookList.add(new AbstractHttpOutputStreamHook(method));

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
        log.debug("AbstractHttpOutputStreamHook");
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }
}
