package com.mhb.xhook.hookclass.abandoned.httpclient;

import android.os.Binder;

import com.mhb.xhook.xposed.collecter.HttpTransaction;
import com.mhb.xhook.xposed.collecter.HttpTransactions;
import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by maohongbin01 on 16/8/4.
 */
public class AbstractSessionOutputBufferHook extends MethodHook {

    private static final String mClassName = "org.apache.http.impl.io.AbstractSessionOutputBuffer";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();
    private static long time;

    private AbstractSessionOutputBufferHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // write(byte[]) : void
    // write(int) : void
    // write(byte[], int, int) : void

    private enum Methods {
        write
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new AbstractSessionOutputBufferHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
        int uid = Binder.getCallingUid();
        String argNames = "";
        if (mMethod == Methods.write) {
            if (param.args.length == 3) {
                argNames = "buffer|offset|len";
            } else if (param.args.length == 1) {
                if (param.args[0] instanceof byte[]) {
                    argNames = "buffer";
                } else {
                    argNames = "b";
                }
            } else if (param.args.length == 0) {
                // TODO:
                log.error("error,need fix 0");
            }
        }
        methodLog(uid, param, argNames, "before");
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        HttpTransaction httpTransaction = HTTPTRANSACTINS.getActiveHttpTransactionByThreadId(threadId);
        if (null == httpTransaction) {
            return;
        }
        if (!httpTransaction.checkNetworkAPI(HttpTransaction.NetworkAPI.HttpClient)) {
            log.error("checkNetworkAPI failed");
            return;
        }
        if (mMethod == Methods.write) {
            if (param.args.length == 1) {
                argNames = "buffer";
                // 累加，从而统计出sendSize
                long lastSendSize = httpTransaction.getElementData().getSendSize();
                httpTransaction.getElementData().setSendSize(lastSendSize + (((byte[]) param.args[0]).length));
            }
        }
//        methodLog(uid, param, argNames, "after");
    }
}
