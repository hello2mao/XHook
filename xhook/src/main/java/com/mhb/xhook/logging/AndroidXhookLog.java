package com.mhb.xhook.logging;


import android.util.Log;

public class AndroidXhookLog implements BasicLog {
    private int level;
    public AndroidXhookLog() {
        level = INFO;
    }

    public void debug(String message) {
        if (level == DEBUG) {
            Log.d(TAG, message);
        }
    }

    public void verbose(String message) {
        if (level >= VERBOSE) {
            Log.v(TAG, message);
        }
    }

    public void info(String message) {
        if (level >= INFO) {
            Log.i(TAG, message);
        }
    }

    public void warning(String message) {
        if (level >= WARNING) {
            Log.w(TAG, message);
        }
    }

    public void error(String message) {
        if (level >= ERROR) {
            Log.e(TAG, message);
        }
    }

    public void error(String message, Throwable cause) {
        if (level >= ERROR) {
            Log.e(TAG, message, cause);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level <= DEBUG && level >= ERROR) {
            this.level = level;
        } else {
            throw new IllegalArgumentException("Log level is not between ERROR and DEBUG");
        }
    }
}
