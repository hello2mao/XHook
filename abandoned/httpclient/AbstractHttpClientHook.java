package com.mhb.xhook.hookclass.abandoned.httpclient;

import android.os.Binder;

import com.mhb.xhook.xposed.collecter.ElementData;
import com.mhb.xhook.xposed.collecter.HttpTransaction;
import com.mhb.xhook.xposed.collecter.HttpTransactions;
import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class AbstractHttpClientHook extends MethodHook {

    private static final String mClassName = "org.apache.http.impl.client.AbstractHttpClient";
    private Methods mMethod = null;
    private BasicLog log = XHookLogManager.getInstance();

    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();

    private AbstractHttpClientHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // TODO: in eight execute, finally call this execute:
    // final HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
    // external/apache-http/src/org/apache/http/impl/client/AbstractHttpClient.java
    // http://developer.android.com/reference/org/apache/http/impl/client/AbstractHttpClient.html

    private enum Methods {
        execute
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new AbstractHttpClientHook(method));
        }
        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        if (mMethod == Methods.execute) {
            if ((param.args.length == 3) && (param.args[0] instanceof HttpHost)) {
                if ((param.args[2] == null) | (param.args[2] instanceof HttpContext)) {
                    argNames = "target|request|context";
                    HttpTransaction httpTransaction = HTTPTRANSACTINS
                            .getActiveHttpTransactionByThreadId(threadId);
                    if (null != httpTransaction) {
                        // maybe, in this thread open another connection, so finish last http transaction
                        // which is not finish due to some reason
                        httpTransaction.updateTransactionState(HttpTransaction.TransactonState.finish);
                    }
                    HTTPTRANSACTINS.add(new HttpTransaction(
                            new ElementData(),
                            (HttpClient) param.thisObject,
                            (HttpRequest) param.args[1],
                            threadId,
                            HttpTransaction.NetworkAPI.HttpClient));
                    methodLog(uid, param, argNames, "before");
                }
            }
        }
//        log.debug("threadId=" + threadId);
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
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
        if (mMethod == Methods.execute) {
            if ((param.args.length == 3) && (param.args[0] instanceof HttpHost)) {
                if ((param.args[2] == null) | (param.args[2] instanceof HttpContext)) {
                    argNames = "target|request|context";
                    httpTransaction.finishHttpClientExecute((HttpResponse) param.getResult());
                    methodLog(uid, param, argNames, "after");
                }
            }
        }
//        log.debug("threadId=" + threadId);
    }
}
