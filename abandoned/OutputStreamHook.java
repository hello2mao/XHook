package com.mhb.xhook.hookclass.abandoned;

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
 * Created by maohongbin01 on 16/7/26.
 */
public class OutputStreamHook extends MethodHook {

    private static final String mClassName = "java.io.OutputStream";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();
    private static long time;

    private OutputStreamHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // public void write(byte[] b)
    // public void write(byte[] b, int off, int len)
    // TODO:
    // public abstract void write(int b)
    // https://developer.android.com/reference/java/io/OutputStream.html

    private enum Methods {
        write
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new OutputStreamHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = "";
        if (mMethod == Methods.write) {
            if (param.args.length == 1) {
                argNames = "b";
                methodLog(uid, param, argNames, "before");
            }
        }

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
        if (mMethod == Methods.write) {
            if (param.args.length == 1) {
                argNames = "b";
                // 累加，从而统计出sendSize
                long lastSendSize = httpTransaction.getElementData().getSendSize();
                httpTransaction.getElementData().setSendSize(lastSendSize + ((int) param.args[0]));
                methodLog(uid, param, argNames, "after");
            }
        }
    }
}
