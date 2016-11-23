package com.mhb.xhook.networklib;


public class HttpsSocketEvent extends SocketData {
    private int fd;
    private String ipAddress;
    private int port;
    private double socketCreateTime;
    private double connectStartTime;
    private double writeOverTime;
    private int tcpHandshakePeriod;
    private int firstPacketPeriod;
    private int sslHandshakePeriod;
    private int connecterrorCode;
    private String host;
    
    public HttpsSocketEvent() {
        this.ipAddress = "";
        this.host = "";
    }

    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setConnecterrorCode(final int connecterrorCode) {
        this.connecterrorCode = connecterrorCode;
    }
    
    public double getConnectStartTime() {
        return this.connectStartTime;
    }
    
    public void setConnectStartTime(final double connectStartTime) {
        this.connectStartTime = connectStartTime;
    }
    
    public int getFd() {
        return this.fd;
    }
    
    public void setFd(final int fd) {
        this.fd = fd;
    }
    
    public int getFirstPacketPeriod() {
        return this.firstPacketPeriod;
    }
    
    public void setFirstPacketPeriod(final int firstPacketPeriod) {
        this.firstPacketPeriod = firstPacketPeriod;
    }
    
    public int getSslHandshakePeriod() {
        return this.sslHandshakePeriod;
    }
    
    public double getWriteOverTime() {
        return this.writeOverTime;
    }
    
    public void setWriteOverTime(final double writeOverTime) {
        this.writeOverTime = writeOverTime;
    }
    
    public void setSslHandshakePeriod(final int sslHandshakePeriod) {
        this.sslHandshakePeriod = sslHandshakePeriod;
    }
    
    public String getIpAddress() {
        return this.ipAddress;
    }
    
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getTcpHandshakePeriod() {
        return this.tcpHandshakePeriod;
    }
    
    public void setTcpHandshakePeriod(final int tcpHandshakePeriod) {
        this.tcpHandshakePeriod = tcpHandshakePeriod;
    }
    
    public double getSocketCreateTime() {
        return this.socketCreateTime;
    }
    
    public void setSocketCreateTime(final double socketCreateTime) {
        this.socketCreateTime = socketCreateTime;
    }
    
    public void initData() {
        this.fd = -1;
        this.ipAddress = "";
        this.port = 0;
        this.socketCreateTime = 0.0;
        this.connectStartTime = 0.0;
        this.writeOverTime = 0.0;
        this.tcpHandshakePeriod = 0;
        this.firstPacketPeriod = 0;
        this.sslHandshakePeriod = 0;
    }

    @Override
    public void setDataFormat() {
        this.eventtype = 2;
        this.target = this.ipAddress + ":" + this.port;
        if (!this.host.isEmpty()) {
            this.target = this.host + "/" + this.target;
        }
        this.duration = this.tcpHandshakePeriod;
        this.network_error_code = this.connecterrorCode;
        this.desc = "";
    }

    @Override
    public String toString() {
        return "fd = " + this.fd + ", ipAddress = " + this.ipAddress + ", port = " + this.port + ", socketCreateTime = " + this.socketCreateTime;
    }
}
