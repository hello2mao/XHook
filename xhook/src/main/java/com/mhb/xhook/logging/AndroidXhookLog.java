package com.mhb.xhook.logging;


import android.util.Log;

public class AndroidXhookLog implements BasicLog {
    private int level;
    public AndroidXhookLog() {
        level = 3;
    }

    public void debug(String message) {
        if (level == 5) {
            Log.d(TAG, message);
        }
    }

    public void verbose(String message) {
        if (level >= 4) {
            Log.v(TAG, message);
        }
    }

    public void info(String message) {
        if (level >= 3) {
            Log.i(TAG, message);
        }
    }

    public void warning(String message) {
        if (level >= 2) {
            Log.w(TAG, message);
        }
    }

    public void error(String message) {
        if (level >= 1) {
            Log.e(TAG, message);
        }
    }

    public void error(String message, Throwable cause) {
        if (level >= 1) {
            Log.e(TAG, message, cause);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level <= 5 && level >= 1) {
            this.level = level;
        } else {
            throw new IllegalArgumentException("Log level is not between ERROR and DEBUG");
        }
    }
}
