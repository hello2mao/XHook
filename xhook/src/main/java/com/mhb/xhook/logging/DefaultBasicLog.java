package com.mhb.xhook.logging;

import android.util.Log;

import com.mhb.xhook.config.GlobalConfig;


public class DefaultBasicLog implements BasicLog {

    public static final String DEBUG_TAG = GlobalConfig.CONF_TAG;

    public void debug(String message) {
        Log.d(DEBUG_TAG, message);
    }

    public void info(String message) {
        Log.i(DEBUG_TAG, message);
    }

    public void verbose(String message) {
        Log.v(DEBUG_TAG, message);
    }

    public void warning(String message) {
        Log.w(DEBUG_TAG, message);
    }

    public void error(String message) {
        Log.e(DEBUG_TAG, message);
    }

    public void error(String message, Throwable cause) {
        Log.e(DEBUG_TAG, message, cause);
    }
}
