package com.mhb.xhook.xposed.hook;

public interface MethodHookListener {

    void methodHookBefore(MethodHookEvent event);

    void methodHookAfter(MethodHookEvent event);
}
