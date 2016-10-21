package com.mhb.xhook.logging;

public interface BasicLog {

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
}
