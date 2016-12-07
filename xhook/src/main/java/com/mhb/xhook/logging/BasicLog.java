package com.mhb.xhook.logging;

import com.mhb.xhook.config.GlobalConfig;

public interface BasicLog {

    String TAG = GlobalConfig.CONF_TAG;

    int DEBUG = 5;
    int VERBOSE = 4;
    int INFO = 3;
    int WARNING = 2;
    int ERROR = 1;

    void debug(String s);

    void verbose(String s);

    void info(String s);

    void warning(String s);

    void error(String s);

    void error(String s, Throwable throwable);

    int getLevel();

    void setLevel(int i);
}
