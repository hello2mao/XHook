package com.mhb.xhook.hookclass.network.inet;

import com.mhb.xhook.xposed.hook.MethodHook;
import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

/**
 * InetAddress类提供了将主机名解析为IP地址（或反之）的方法
 */
public class InetAddressHook extends MethodHook {

    private Methods method = null;
    private static final String CLASS_NAME = "java.net.InetAddress";
    private static final BasicLog LOG = XHookLogManager.getInstance();
    
    private InetAddressHook(Methods method) {
        super(CLASS_NAME, method.name());
        this.method = method;
    }

    // public static InetAddress[] getAllByName(String host)
    // libcore/luni/src/main/java/java/net/InetAddress.java    
    // http://developer.android.com/reference/java/net/InetAddress.html
    
    /**
     * (1) HttpURLConnection在libcore.net.http.HttpConnection的构造函数中
     *     使用了InetAddress类静态方法getAllByName(String host)进行DNS解析
     * (2) HttpClient
     */
    private enum Methods {
        getAllByName
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<>();
        for(Methods method : Methods.values()) {
            methodHookList.add(new InetAddressHook(method));
        }
        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {

//        int uid = Binder.getCallingUid();
//        String argNames = "";
//        long threadId = Thread.currentThread().getId();
//        HttpTransaction httpTransaction = HTTPTRANSACTINS.getActiveHttpTransactionByThreadId(threadId);
//        if (null == httpTransaction) {
//            return;
//        }
//        if (this.method == Methods.getAllByName) {
//            if (param.args.length == 1) {
//                argNames = "host";
//                time = System.currentTimeMillis();
//                httpTransaction.setDNSStartTime(time);
//                methodLog(uid, param, argNames, "before");
//            }
//        }
//        log.debug("threadId=" + threadId);

    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
//        int uid = Binder.getCallingUid();
//        String argNames = "";
//        long threadId = Thread.currentThread().getId();
//        HttpTransaction httpTransaction = HTTPTRANSACTINS.getActiveHttpTransactionByThreadId(threadId);
//        if (null == httpTransaction) {
//            return;
//        }
//        if (mMethod == Methods.getAllByName) {
//            if (param.args.length == 1) {
//                // HttpURLConnection
//                argNames = "host";
//                time = System.currentTimeMillis();
//                httpTransaction.setDNSEndTime(time);
//                methodLog(uid, param, argNames, "after");
//            }
//        }
//        log.debug("threadId=" + threadId);

    }
}
