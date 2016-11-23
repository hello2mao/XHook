package com.mhb.xhook.networklib;

public class DnsEvent extends SocketData {
    private String host;
    private int dnsConsumeTime;
    private double beginTimeStamp;
    private String destIpList;
    private int errorNumber;
    private int retValue;
    private int port;
    private String desc;
    private String q;

    public DnsEvent() {
        this.host = "";
        this.destIpList = "";
        this.desc = "";
        this.q = "";
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getDnsConsumeTime() {
        return dnsConsumeTime;
    }

    public void setDnsConsumeTime(int dnsConsumeTime) {
        this.dnsConsumeTime = dnsConsumeTime;
    }

    public double getBeginTimeStamp() {
        return beginTimeStamp;
    }

    public void setBeginTimeStamp(double beginTimeStamp) {
        this.beginTimeStamp = beginTimeStamp;
    }

    public String getDestIpList() {
        return destIpList;
    }

    public void setDestIpList(String destIpList) {
        this.destIpList = destIpList;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public int getRetValue() {
        return retValue;
    }

    public void setRetValue(int retValue) {
        this.retValue = retValue;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public void setDataFormat() {
        this.eventtype = 1;
        String host = this.host;
        if (!this.q.isEmpty()) {
            host = host + "/" + this.q;
        }
        this.target = host;
        this.duration = this.dnsConsumeTime;
        this.network_error_code = this.errorNumber;
        this.desc = this.desc;
    }

    @Override
    public String toString() {
        return "DnsEvent{host='" + this.host + '\''
                + ", dnsConsumeTime=" + this.dnsConsumeTime
                + ", beginTimeStamp=" + this.beginTimeStamp
                + ", destIpList='" + this.destIpList + '\''
//                + ", isHttp=" + this.g
                + ", errorNumber=" + this.errorNumber
                + ", retValue=" + this.retValue
                + ", port=" + this.port
                + ", desc='" + this.desc + '\'' + '}';
    }
}
