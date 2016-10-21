package com.mhb.xhook.xposed.util;

public class NativeEntry {

    public native static String initSystemNativeHook();
    public native static String initCustomNativeHook(String libName);
    public native static boolean logFilePathFromFd(int uid, int pid, int fd, int id);

    static{
        System.loadLibrary(Util.NATIVE_LIB);
    }
}
