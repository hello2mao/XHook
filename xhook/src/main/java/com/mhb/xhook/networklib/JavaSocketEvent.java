package com.mhb.xhook.networklib;

public class JavaSocketEvent {
    private int fd;
    private String ipAddress;
    private int port;
    private boolean connectSuccess;
    private int errorNumber;
    private double socketCreateTime;
    private double connectFromTime;
    private double pollOutTime;
    private double lastSendTime;
    private double pollInTime;
    private int handshakePeriod;
    private int firstPakcetPeriod;
    
    public double getLastSendTime() {
        return this.lastSendTime;
    }
    
    public void setLastSendTime(final double lastSendTime) {
        this.lastSendTime = lastSendTime;
    }
    
    public int getFd() {
        return this.fd;
    }
    
    public void setFd(final int fd) {
        this.fd = fd;
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
    
    public boolean isConnectSuccess() {
        return this.connectSuccess;
    }
    
    public void setConnectSuccess(final boolean connectSuccess) {
        this.connectSuccess = connectSuccess;
    }
    
    public int getErrorNumber() {
        return this.errorNumber;
    }
    
    public void setErrorNumber(final int errorNumber) {
        this.errorNumber = errorNumber;
    }
    
    public double getSocketCreateTime() {
        return this.socketCreateTime;
    }
    
    public void setSocketCreateTime(final double socketCreateTime) {
        this.socketCreateTime = socketCreateTime;
    }
    
    public double getConnectFromTime() {
        return this.connectFromTime;
    }
    
    public void setConnectFromTime(final double connectFromTime) {
        this.connectFromTime = connectFromTime;
    }
    
    public double getPollOutTime() {
        return this.pollOutTime;
    }
    
    public void setPollOutTime(final double pollOutTime) {
        this.pollOutTime = pollOutTime;
    }
    
    public double getPollInTime() {
        return this.pollInTime;
    }
    
    public void setPollInTime(final double pollInTime) {
        this.pollInTime = pollInTime;
    }
    
    public int getHandshakePeriod() {
        return this.handshakePeriod;
    }
    
    public void setHandshakePeriod(final int handshakePeriod) {
        this.handshakePeriod = handshakePeriod;
    }
    
    public int getFirstPakcetPeriod() {
        return this.firstPakcetPeriod;
    }
    
    public void setFirstPakcetPeriod(final int firstPakcetPeriod) {
        this.firstPakcetPeriod = firstPakcetPeriod;
    }
    
    @Override
    public String toString() {
        return "JavaSocketEvent{fd=" + this.fd + ", ipAddress='" + this.ipAddress + '\'' + ", port=" + this.port + ", connectSuccess=" + this.connectSuccess + ", errorNumber=" + this.errorNumber + ", socketCreateTime=" + this.socketCreateTime + ", connectFromTime=" + this.connectFromTime + ", pollOutTime=" + this.pollOutTime + ", lastSendTime=" + this.lastSendTime + ", pollInTime=" + this.pollInTime + ", handshakePeriod=" + this.handshakePeriod + ", firstPakcetPeriod=" + this.firstPakcetPeriod + '}';
    }
}
