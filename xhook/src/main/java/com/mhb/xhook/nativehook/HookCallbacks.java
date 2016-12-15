package com.mhb.xhook.nativehook;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.util.ReflectionUtils;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketImpl;

import javax.net.ssl.SSLSocket;

import okhttp3.Request;
import okhttp3.internal.http.HttpEngine;

public class HookCallbacks {

    private static final BasicLog LOG = XhookLogManager.getInstance();
    private static final HookManager HOOK_MANAGER = HookManager.getInstance();

    public void setHttpEngine(HttpEngine httpEngine) {
        LOG.debug("In proxy: setHttpEngine");
        Object receiver = HOOK_MANAGER.retrieveReceiver(this, false);
        HOOK_MANAGER.invokeOrigin("setHttpEngine", receiver, httpEngine);
        LOG.debug("setHttpEngine called");
        try {
            Field field3 = httpEngine.getClass().getDeclaredField("networkRequest");
            field3.setAccessible(true);
            Request request = (Request) field3.get(httpEngine);
            Socket socket = httpEngine.streamAllocation.connection().socket;
            if (socket instanceof SSLSocket) {
                LOG.debug("<Https>");
                Class<?> clazz = httpEngine.streamAllocation.connection().socket.getClass();
                Field field = ReflectionUtils.getField(clazz, "socket");
                if (field != null) {
                    field.setAccessible(true);
                } else {
                    LOG.error("can not get field: socket");
                    return;
                }
                // update socket
                socket = (Socket) field.get(socket);
            } else {
                LOG.debug("<Http>");
            }
            Field field = Socket.class.getDeclaredField("impl");
            field.setAccessible(true);
            SocketImpl impl = (SocketImpl)field.get(socket);
//            LOG.debug("impl=" + impl.toString());
            Class<?> clazz = Class.forName("java.net.PlainSocketImpl");
            Field field2 = ReflectionUtils.getField(clazz, "fd");
            if (field2 != null) {
                field2.setAccessible(true);
            } else {
                LOG.error("can not get field: fd");
                return;
            }
            FileDescriptor fd = (FileDescriptor) field2.get(impl);
            LOG.debug("url=" + request.url() + ", fd=" + fd.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void safe() {
        LOG.debug("safe");
        System.gc();
    }
}
