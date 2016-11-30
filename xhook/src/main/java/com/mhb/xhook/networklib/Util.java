package com.mhb.xhook.networklib;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Util {

    public static String getByName(String s) {
        if (null == s) {
            return null;
        }
        try {
            final InetAddress byName = InetAddress.getByName(s);
            if (byName instanceof Inet6Address) {
                s = byName.getHostAddress();
            } else if (byName instanceof Inet4Address) {
                s = byName.getHostAddress();
            } else {
                s = "";
            }
        } catch (UnknownHostException ex) {
            s = "";
        }
        return s;
    }
}
