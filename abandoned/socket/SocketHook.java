package com.mhb.xhook.hookclass.abandoned.socket;

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
 * Created by maohongbin01 on 16/7/25.
 */
public class SocketHook extends MethodHook {

    private static final String mClassName = "java.net.Socket";
    private Methods mMethod = null;

    private static BasicLog log = XHookLogManager.getInstance();
    private static final HttpTransactions HTTPTRANSACTINS = HttpTransactions.getInstance();

    private static long time;

    private SocketHook(Methods method) {
        super(mClassName, method.name());
        mMethod = method;
    }

    // public void bind(SocketAddress bindpoint)
    // public void connect(SocketAddress endpoint, int timeout)
    // public void connect(SocketAddress endpoint)
    // public void close()
    // libcore/luni/src/main/java/java/net/Socket.java
    // http://developer.android.com/reference/java/net/Socket.html

    /**
     * (1) HttpURLConnection在libcore.net.http.HttpConnection的构造函数中
     *     使用了java.net.Socket类connect(SocketAddress endpoint, int timeout)进行socket连接
     * @author maohongbin01
     * @time 16/7/29 上午11:00
     */
    private enum Methods {
        connect
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for(Methods method : Methods.values()) {
            methodHookList.add(new SocketHook(method));
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
        if (mMethod == Methods.connect) {
            if (param.args.length == 2) {
                argNames = "endpoint|timeout";
                time = System.currentTimeMillis();
                httpTransaction.setTCPStartTime(time);
                methodLog(uid, param, argNames, "before");
            }
        }
//        log.debug("threadId=" + threadId);


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
        if (mMethod == Methods.connect) {
            if (param.args.length == 2) {
                argNames = "endpoint|timeout";
                time = System.currentTimeMillis();
                httpTransaction.setTCPEndTime(time);
                methodLog(uid, param, argNames, "after");
            }
        }
//        log.debug("threadId=" + threadId);


    }

}
