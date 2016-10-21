package com.mhb.xhook.hookclass.abandoned.url;

import com.mhb.xhook.xposed.collecter.HttpTransaction;
import com.mhb.xhook.xposed.collecter.HttpTransactions;
import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class URLHook extends MethodHook {

    private static final String CLASS_NAME = "java.net.URL";
    private Methods method = null;
    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTP_TRANSACTIONS = HttpTransactions.getInstance();

    private URLHook(Methods method) {
        super(CLASS_NAME, method.name());
        this.method = method;
    }

    // public URLConnection openConnection (Proxy proxy)
    // public URLConnection openConnection ()
    // libcore/luni/src/main/java/java/net/URL.java
    // http://developer.android.com/reference/java/net/URL.html

    private enum Methods {
        openConnection
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new URLHook(method));
        }
        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        if (method == Methods.openConnection) {
            if (param.args.length == 0) { // public URLConnection openConnection ()
                argNames = "";
                HttpTransaction httpTransaction = HTTP_TRANSACTIONS.getRunningHttp(threadId);
                if (null != httpTransaction) {
                    // maybe, in this thread open another connection, so finish last http transaction
                    // which is not finish due to some reason
                    httpTransaction.setTransactionState(HttpTransaction.TransactionState.finish);
                }
                // start a new http transaction
                HTTP_TRANSACTIONS.add(new HttpTransaction((URLConnection) param.getResult(), threadId));
            }
        }
        methodLogLong(param, argNames, "after");
    }
    
}
