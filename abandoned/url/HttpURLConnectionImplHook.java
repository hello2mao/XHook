package com.mhb.xhook.hookclass.abandoned.url;

import android.os.Binder;

import com.mhb.xhook.xposed.collecter.BasicHttp;
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
public class HttpURLConnectionImplHook extends MethodHook{

    private static final String CLASS_NAME = "libcore.net.http.HttpURLConnectionImpl";
    private Methods method = null;
    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTP_TRANSACTIONS = HttpTransactions.getInstance();

    private HttpURLConnectionImplHook(Methods method) {
        super(CLASS_NAME, method.name());
        this.method = method;
    }

    // public final void connect() throws IOException
    // public final void disconnect()
    // public final InputStream getInputStream() throws IOException

    private enum Methods {
        connect, disconnect, getInputStream
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for (Methods method : Methods.values()) {
            methodHookList.add(new HttpURLConnectionImplHook(method));
        }
        return methodHookList;
    }

    /**
     * connect could be called in many place include:
     * (1) direct call: URLConnection.connect
     * (2) getOutputStream
     * (3) getInputStream
     * (4) ...
     * @author maohongbin01
     * @time 16/7/28 下午8:16
     */
    @Override
    public void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        HttpTransaction httpTransaction = HTTP_TRANSACTIONS.getRunningHttp(threadId);
        if (null == httpTransaction) {
            return;
        }
        if (!httpTransaction.checkNetworkAPI(HttpTransaction.NetworkAPI.URLConnection)) {
            log.error("checkNetworkAPI failed");
            return;
        }
        if (method == Methods.connect) {
            argNames = "";
            httpTransaction.init();
        } else if (method == Methods.getInputStream) {
            argNames = "";
        }
        methodLogLong(param, argNames, "before");
    }

    @Override
    public void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = "";
        long threadId = Thread.currentThread().getId();
        HttpTransaction httpTransaction = HTTP_TRANSACTIONS.getActiveHttpTransactionByThreadId(threadId);
        if (null == httpTransaction) {
            return;
        }
        if (!httpTransaction.checkNetworkAPI(HttpTransaction.NetworkAPI.HttpURLConnection)) {
            log.error("checkNetworkAPI failed");
            return;
        }
        if (mMethod == Methods.connect) {
            argNames = "";
//            log.debug(httpTransaction.toString());
        } else if (mMethod == Methods.getInputStream) {
            argNames = "";
            httpTransaction.finishHttpURLConnect();
        } else if (mMethod == Methods.disconnect) {
            argNames = "";
            httpTransaction.finishHttpURLDisconnect();
        }
        methodLog(uid, param, argNames, "after");
    }
}
