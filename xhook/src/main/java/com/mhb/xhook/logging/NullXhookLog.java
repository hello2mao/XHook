package com.mhb.xhook.logging;

public class NullXhookLog implements BasicLog {

    public NullXhookLog() {
    }

    public void debug(String s) {
    }

    public void info(String s) {
    }

    public void verbose(String s) {
    }

    public void error(String s) {
    }

    public void error(String s, Throwable throwable) {
    }

    public void warning(String s) {
    }

    public int getLevel() {
        return 5;
    }

    public void setLevel(int i) {
    }
}