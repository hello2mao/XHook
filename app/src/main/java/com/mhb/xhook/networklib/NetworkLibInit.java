package com.mhb.xhook.networklib;

import com.mhb.xhook.AppConfig;

import org.apache.log4j.Logger;

public class NetworkLibInit {
    public static final String LIB_VERSION = "1.0.0";
    private static boolean isLoadLibrary;
    private static final Logger LOG = Logger.getLogger(AppConfig.CONF_TAG);


    static {
        try {
            System.loadLibrary("xhooknative");
            isLoadLibrary = true;
            LOG.debug("Load xhooknative success! LIB_VERSION = " + LIB_VERSION);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            LOG.error("Nativie library not found! Please copy xhooknative into your project");
            isLoadLibrary = false;
        } catch (Throwable t) {
            isLoadLibrary = false;
            LOG.error("Failed to load library ElfHook: " + t.getMessage());
        }
    }

    public native void initNativeHook(String libPath, String release);

    public synchronized void callback(final int fd,
                                      final int type,
                                      final double startTime,
                                      final int timeElapsed,
                                      final int returnValue,
                                      final int errorNum,
                                      final String host,
                                      final String address,
                                      final String desc,
                                      final int port) {
        LOG.debug("NetworkLibInit: in callback");
        WebEventStore.setSocketEvent(fd, type, startTime, timeElapsed, returnValue, errorNum, host,
                address, desc, port);
    }
}
