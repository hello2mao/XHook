package com.mhb.xhook.xposed.collecter;

/**
 * 元素数据--URL
 */
public class ElementData {

    private String url;
    private String requestMethod;
    private String requestHeader;
    private String requestMimeType;
    private long sendSize;
    private String responseHeader;
    private String responseMimeType;
    private long downloadSize;
    private int responseCode;

    private long dnsStart;
    private long dnsEnd;
    private long tcpStart;
    private long tcpEnd;
    private long sslStart;
    private long sslEnd;
    private long requestStart;
    private long requestEnd;
    private long serverResponseStart;
    private long serverResponseEnd;
    private long downloadStart;
    private long downloadEnd;

    public ElementData() {
        this.url = "";
        this.requestMethod = "";
        this.requestHeader = "";
        this.requestMimeType = "";
        this.sendSize = 0;
        this.responseHeader = "";
        this.responseMimeType = "";
        this.downloadSize = 0;
        this.responseCode = 0;
        this.dnsStart = 0;
        this.dnsEnd = 0;
        this.tcpStart = 0;
        this.tcpEnd = 0;
        this.sslStart = 0;
        this.sslEnd = 0;
        this.requestStart = 0;
        this.requestEnd = 0;
        this.serverResponseStart = 0;
        this.serverResponseEnd = 0;
        this.downloadStart = 0;
        this.downloadEnd = 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestMimeType() {
        return requestMimeType;
    }

    public void setRequestMimeType(String requestMimeType) {
        this.requestMimeType = requestMimeType;
    }

    public long getSendSize() {
        return sendSize;
    }

    public void setSendSize(long sendSize) {
        this.sendSize = sendSize;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getResponseMimeType() {
        return responseMimeType;
    }

    public void setResponseMimeType(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getDnsStart() {
        return dnsStart;
    }

    public void setDnsStart(long dnsStart) {
        this.dnsStart = dnsStart;
    }

    public long getDnsEnd() {
        return dnsEnd;
    }

    public void setDnsEnd(long dnsEnd) {
        this.dnsEnd = dnsEnd;
    }

    public long getTcpStart() {
        return tcpStart;
    }

    public void setTcpStart(long tcpStart) {
        this.tcpStart = tcpStart;
    }

    public long getTcpEnd() {
        return tcpEnd;
    }

    public void setTcpEnd(long tcpEnd) {
        this.tcpEnd = tcpEnd;
    }

    public long getSslStart() {
        return sslStart;
    }

    public void setSslStart(long sslStart) {
        this.sslStart = sslStart;
    }

    public long getSslEnd() {
        return sslEnd;
    }

    public void setSslEnd(long sslEnd) {
        this.sslEnd = sslEnd;
    }

    public long getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(long requestStart) {
        this.requestStart = requestStart;
    }

    public long getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(long requestEnd) {
        this.requestEnd = requestEnd;
    }

    public long getServerResponseStart() {
        return serverResponseStart;
    }

    public void setServerResponseStart(long serverResponseStart) {
        this.serverResponseStart = serverResponseStart;
    }

    public long getServerResponseEnd() {
        return serverResponseEnd;
    }

    public void setServerResponseEnd(long serverResponseEnd) {
        this.serverResponseEnd = serverResponseEnd;
    }

    public long getDownloadStart() {
        return downloadStart;
    }

    public void setDownloadStart(long downloadStart) {
        this.downloadStart = downloadStart;
    }

    public long getDownloadEnd() {
        return downloadEnd;
    }

    public void setDownloadEnd(long downloadEnd) {
        this.downloadEnd = downloadEnd;
    }
}
