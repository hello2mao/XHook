package com.mhb.xhook.nativehook;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.util.ReflectionUtils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketImpl;

import okhttp3.Request;
import okhttp3.internal.http.Http1xStream;
import okhttp3.internal.http.HttpEngine;

public class HookCallbacks {

    private static final BasicLog LOG = XHookLogManager.getInstance();

    public String victim(int a, long b, char c) {
        Object receiver = HookManager.retrieveReceiver(this, false);
        LOG.debug("hook victim called: " + receiver + ", a=" + a + ", b=" + b + ", c=" + c);
//        safe();
        // before
        String result = (String) HookManager.invokeOrigin("victim", receiver, a, b, c);
        // after
        return result + " SUCCESS";
    }

    public void writeRequestHeaders(Request request) throws IOException {
        Object receiver = HookManager.retrieveReceiver(this, false);
        LOG.debug("writeRequestHeaders called");
//        LOG.debug("request: " + request.toString());
        HookManager.invokeOrigin("writeRequestHeaders", receiver, request);
        Http1xStream httpStream = (Http1xStream)receiver;

        try {
            Field field = httpStream.getClass().getDeclaredField("streamAllocation");
            field.setAccessible(true);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void setHttpEngine(HttpEngine httpEngine) {
        Object receiver = HookManager.retrieveReceiver(this, false);
        LOG.debug("setHttpEngine called");
        try {
            Field field3 = httpEngine.getClass().getDeclaredField("networkRequest");
            field3.setAccessible(true);
            Request request = (Request) field3.get(httpEngine);
            Socket socket = httpEngine.streamAllocation.connection().socket;
            Field field = Socket.class.getDeclaredField("impl");
            field.setAccessible(true);
            SocketImpl impl = (SocketImpl)field.get(socket);
            LOG.debug("impl=" + impl.toString());
            Class<?> clazz = Class.forName("java.net.PlainSocketImpl");
            Field field2 = ReflectionUtils.getField(clazz, "fd");
            FileDescriptor fd = (FileDescriptor) field2.get(impl);
            LOG.debug("url=" + request.url() + ", fd=" + fd.toString());

//            Field field2 = impl.getClass().getDeclaredField("fd");
//            field2.setAccessible(true);
//            FileDescriptor fd = (FileDescriptor) field2.get(impl);
//            LOG.debug("fdddd" + fd.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        LOG.debug("socket: " + httpEngine.streamAllocation.connection().socket.get);
        HookManager.invokeOrigin("setHttpEngine", receiver, httpEngine);

    }

    private void safe() {
        LOG.debug("safe");
        System.gc();
    }
}
