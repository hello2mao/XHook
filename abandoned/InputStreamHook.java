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
 * Created by maohongbin01 on 16/7/27.
 */
public class InputStreamHook extends MethodHook {

    private static final String mClassName = "java.io.InputStream";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();
    private static long time;

    private InputStreamHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // abstract int read()
    // int read(byte[] b, int off, int len)
    // int read(byte[] b)
    // https://developer.android.com/reference/java/io/InputStream.html

    private enum Methods {
        read
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new InputStreamHook(method));
        }

        return methodHookList;
    }

    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        HttpTransaction httpTransaction = HTTPTRANSACTINS.getActiveHttpTransactionByThreadId(threadId);
        if (null == httpTransaction) {
            return;
        }
        if (mMethod == Methods.read) {
            if (param.args.length == 1) {
                argNames = "b";
                time = System.currentTimeMillis();
                httpTransaction.setDownloadStartTime(time);
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
        if (mMethod == Methods.read) {
            if (param.args.length == 1) {
                argNames = "b";
                // 返回不为-1，则累加，从而统计出downloadSize
                if (((int) param.getResult()) != -1) {
                    long lastDownloadSize = httpTransaction.getElementData().getDownloadSize();
                    httpTransaction.getElementData().setDownloadSize(lastDownloadSize
                            + ((int) param.getResult()));
                }
                time = System.currentTimeMillis();
                httpTransaction.setDownloadEndTime(time);
                methodLog(uid, param, argNames, "after");
            }
        }

    }
}
