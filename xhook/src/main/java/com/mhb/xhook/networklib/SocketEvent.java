package com.mhb.xhook.networklib;


import java.util.regex.Pattern;

public class SocketEvent {
    private int sockettype;
    private double startTime;
    private int timeElapsed;
    private int returnValue;
    private int errorNumber;
    private String host;
    private String[] addressArray;
    private String dnsaddressArray;
    private String errormsg;
    private int fd;
    private a state;
    public int port;
    private boolean ishttpData;
    private boolean isUsed;
    private String connectAddr;

    private enum a
    {
        a,
        b,
        c
    }

    public SocketEvent() {
        this.state = a.a;
        this.ishttpData = false;
        this.isUsed = false;
        this.errormsg = "";
        this.errorNumber = 0;
    }
    
    public SocketEvent(final int type, final double startTime, final int timeElapsed, final int returnValue, final int errorNumber, final String desc, final int port) {
        this.sockettype = type;
        this.timeElapsed = timeElapsed;
        this.returnValue = returnValue;
        this.errorNumber = errorNumber;
        this.startTime = startTime;
        this.port = port;
        this.state = a.a;
        this.ishttpData = false;
        this.isUsed = false;
        if (null == desc) {
            this.errormsg = "";
        }
        else {
            this.errormsg = desc;
        }
    }
    
    public void stop() {
        this.state = a.c;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String[] getAddressArray() {
        return this.addressArray;
    }
    
    public void setAddressArray(final String[] addressArray) {
        this.addressArray = addressArray;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public int getSocketType() {
        return this.sockettype;
    }
    
    public void setSocketType(final int type) {
        this.sockettype = type;
    }
    
    public int getTimeElapsed() {
        return this.timeElapsed;
    }
    
    public void setTimeElapsed(final int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    
    public int getReturnValue() {
        return this.returnValue;
    }
    
    public void setReturnValue(final int returnValue) {
        this.returnValue = returnValue;
    }
    
    public int getErrorNumber() {
        return this.errorNumber;
    }
    
    public void setErrorNumber(final int errorNumber) {
        this.errorNumber = errorNumber;
    }
    
    public void setIsHttp(final boolean ishttpData) {
        this.ishttpData = ishttpData;
    }
    
    public boolean getIsHttp() {
        return this.ishttpData;
    }
    
    public void setIsUsed(final boolean isUsed) {
        this.isUsed = isUsed;
    }
    
    public boolean getIsUsed() {
        return this.isUsed;
    }
    
    public void setConnectAddr(final String ip) {
        this.connectAddr = ip;
    }
    
    public String getConnectAddr() {
        return this.connectAddr;
    }
    
    public String getErrormsg() {
        return this.errormsg;
    }
    
    public void setErrormsg(final String msg) {
        this.errormsg = msg;
    }
    
    public int getFd() {
        return this.fd;
    }
    
    public void setFd(final int fd) {
        this.fd = fd;
    }
    
    public String getDnsaddressArray() {
        return this.dnsaddressArray;
    }
    
    public void setDnsaddressArray(final String dnsaddressArray) {
        this.dnsaddressArray = dnsaddressArray;
    }
    
//    @Override
//    public String toString() {
//        final t t = com.networkbench.agent.impl.c.t.values()[this.sockettype];
//        final StringBuilder sb = new StringBuilder();
//        if (this.addressArray != null) {
//            for (final String s : this.addressArray) {
//                if (s != null) {
//                    sb.append(s).append(";");
//                }
//            }
//        }
//        final StringBuilder sb2 = new StringBuilder();
//        if (!TextUtils.isEmpty((CharSequence)this.host)) {
//            sb2.append(this.host);
//        }
//        return "type:" + t.name() + ", timeElapsed:" + this.timeElapsed + ", returnValue:" + this.returnValue + ", errorNumber:" + this.errorNumber + ", content:" + sb.toString() + ", host:" + sb2.toString() + ", port:" + this.port;
//    }
//
//    @Override
//    public JsonArray asJsonArray() {
//        final JsonArray jsonArray = new JsonArray();
//        if (this.sockettype == t.k.a()) {
//            jsonArray.add(new JsonPrimitive(2));
//        }
//        else if (this.sockettype == t.c.a()) {
//            jsonArray.add(new JsonPrimitive(2));
//        }
//        else if (this.sockettype == t.e.a()) {
//            jsonArray.add(new JsonPrimitive(1));
//        }
//        else {
//            jsonArray.add(new JsonPrimitive(this.sockettype));
//        }
//        String string = this.host;
//        if (this.sockettype == t.e.a() && this.connectAddr != null && this.connectAddr != "") {
//            string = string + "/" + this.connectAddr;
//        }
//        else if (this.sockettype == t.k.a() || this.sockettype == t.c.a()) {
//            string = this.connectAddr + ":" + this.port;
//            if (this.host != null && !this.host.equals("null") && isHostName(this.host)) {
//                string = this.host + "/" + string;
//            }
//        }
//        jsonArray.add(new JsonPrimitive(string));
//        jsonArray.add(new JsonPrimitive(this.timeElapsed));
//        jsonArray.add(new JsonPrimitive(this.errorNumber));
//        jsonArray.add(new JsonPrimitive(this.errormsg));
//        return jsonArray;
//    }
    
    private static boolean isHostName(final String str) {
        boolean b = false;
        if (Pattern.compile("[A-Za-z]+").matcher(str).find()) {
            b = true;
        }
        return b;
    }
    

}
