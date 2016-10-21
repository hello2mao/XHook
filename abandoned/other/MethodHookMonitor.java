package com.mhb.xhook.xposed.hook;

import java.util.ArrayList;

public class MethodHookMonitor {

    private final ArrayList<MethodHookListener> methodHookListeners;

    public MethodHookMonitor() {
        methodHookListeners = new ArrayList<>();
    }

    public void addMethodHookListener(MethodHookListener listener) {
        synchronized (methodHookListeners) {
            methodHookListeners.add(listener);
        }
    }

    public void removeMethodHookListener(MethodHookListener listener) {
        synchronized (methodHookListeners) {
            methodHookListeners.remove(listener);
        }
    }

    public void notifyMethodHookBefore() {
        ArrayList<MethodHookListener> listeners;
        synchronized (methodHookListeners) {
            listeners = new ArrayList<>(methodHookListeners);
        }
        for (MethodHookListener listener : listeners) {
            listener.methodHookBefore(new MethodHookEvent(this));
        }
    }

    public void notifyMethodHookAfter() {
        ArrayList<MethodHookListener> listeners;
        synchronized (methodHookListeners) {
            listeners = new ArrayList<>(methodHookListeners);
        }
        for (MethodHookListener listener : listeners) {
            listener.methodHookAfter(new MethodHookEvent(this));
        }
    }
}
