package com.mhb.xhook.hookclass.abandoned.url;

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
public class RetryableOutputStreamHook extends  MethodHook {

    private static final String mClassName = "libcore.net.http.RetryableOutputStream";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();
    private static long time;

    private RetryableOutputStreamHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // public synchronized void write(byte[] buffer, int offset, int count) throws IOException

    private enum Methods {
        write
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new RetryableOutputStreamHook(method));
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
                argNames = "buffer|offset|count";
            }
        }
//        methodLog(uid, param, argNames, "before");
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
        if (!httpTransaction.checkNetworkAPI(HttpTransaction.NetworkAPI.HttpURLConnection)) {
            log.error("checkNetworkAPI failed");
            return;
        }
        if (mMethod == Methods.write) {
            if (param.args.length == 3) {
                argNames = "buffer|offset|count";
                // 累加，从而统计出sendSize
                long lastSendSize = httpTransaction.getElementData().getSendSize();
                httpTransaction.getElementData().setSendSize(lastSendSize + ((int) param.args[2]));
            }
        }
//        methodLog(uid, param, argNames, "after");
    }
}
