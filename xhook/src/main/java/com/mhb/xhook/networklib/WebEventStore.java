package com.mhb.xhook.networklib;


import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;

import java.util.regex.Pattern;

public class WebEventStore {
    private static final BasicLog LOG = XhookLogManager.getInstance();

    public static String eventTypeToString(int type) {
        String eventType = null;
        switch(type) {
            case 1:
                eventType = "socket created";
                break;
            case 2:
                eventType = "unknown type 2";
                break;
            case 3:
                eventType = "socket connected";
                break;
            case 4:
                eventType = "socket ssl";
                break;
            case 5:
                eventType = "socket pollout";
                break;
            case 6:
                eventType = "socket pollin";
                break;
            case 7:
                eventType = "DNS event";
                break;
            case 8:
                eventType = "socket send over";
                break;
            case 9:
                eventType = "socket received";
                break;
            default:
                eventType = "unknown type";
                break;
        }
        return eventType;
    }

    public static synchronized void setSocketEvent(final int fd,
                                                   final int type,
                                                   final double startTime,
                                                   final int timeElapsed,
                                                   final int returnValue,
                                                   final int errorNum,
                                                   final String host,
                                                   final String address,
                                                   final String desc,
                                                   final int port) {
        LOG.debug("WebEventStore setSocketEvent: fd = " + fd
                + ", type = " + eventTypeToString(type)
                + ", startTime = " + startTime
                + ", timeElapsed = " + timeElapsed
                + ", returnValue = " + returnValue
                + ", errorNum = " + errorNum
                + ", host = " + host
                + ", address = " + address
                + ", desc = " + desc
                + ", port = " + port);
        switch (type) {
            case 1: { // socket created
                HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                if (httpsSocketEvent == null) {
                    httpsSocketEvent = new HttpsSocketEvent();
                }
                httpsSocketEvent.initData();
                httpsSocketEvent.setFd(fd);
                httpsSocketEvent.setSocketCreateTime(startTime);
                LOG.debug("WebEventStore socket created, fd:" + fd + ", time:" + startTime);
                EventCache.httpsSocketEventMap.put(fd, httpsSocketEvent);
                break;
            }
            case 2: {
                break;
            }
            case 3: { // socket connected
                final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                if (httpsSocketEvent != null) {
                    httpsSocketEvent.setHttp(false);
                    httpsSocketEvent.setSend(false);
                    LOG.debug("WebEventStore socket connected, fd:" + fd
                            + ", timeElapsed:" + timeElapsed);
                    if (timeElapsed > 1) {
                        httpsSocketEvent.setTcpHandshakePeriod(timeElapsed);
                        httpsSocketEvent.setConnecterrorCode(errorNum);
                    }
                    httpsSocketEvent.setIpAddress(address);
                } else {
                    LOG.debug("WebEventStore socket connected, but no fd related, fd:" + fd);
                }
                break;
            }
            case 4: { // socket ssl
                final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                if (httpsSocketEvent != null) {
                    httpsSocketEvent.setSslHandshakePeriod(httpsSocketEvent.getSslHandshakePeriod()
                            + timeElapsed);
                    LOG.debug("WebEventStore socket ssl, fd:" + fd
                            + ", tcp handshake:" + httpsSocketEvent.getSslHandshakePeriod());
                } else {
                    LOG.debug("WebEventStore socket ssl, but no fd related, fd:" + fd);
                }
                break;
            }
            case 5: { // socket pollout 写数据不会导致阻塞
                final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                // TODO:why
                if (httpsSocketEvent != null && httpsSocketEvent.getTcpHandshakePeriod() == 0) {
                    httpsSocketEvent.setTcpHandshakePeriod((int)((startTime
                            - httpsSocketEvent.getSocketCreateTime()) * 1000 * 1000));
                    if (httpsSocketEvent.getIpAddress() == null || httpsSocketEvent.getIpAddress()
                            .isEmpty()) {
                        if (!address.equals("")) {
                            httpsSocketEvent.setIpAddress(address);
                        }
                    }
                    LOG.debug("WebEventStore socket pollout, fd:" + fd
                            + ", tcp handshake:" + httpsSocketEvent.getTcpHandshakePeriod());
                } else {
                    LOG.debug("WebEventStore socket pollout, but no fd related, fd:" + fd);
                }
                break;
            }
            case 6: { // socket pollin 有数据可读
                final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                if (httpsSocketEvent != null) {
                    httpsSocketEvent.setFirstPacketPeriod((int)((startTime
                            - httpsSocketEvent.getSocketCreateTime()) * 1000 * 1000
                            - httpsSocketEvent.getTcpHandshakePeriod()));
                    LOG.debug("WebEventStore socket pollin, fd:" + fd
                            + ", firstpacket period:" + httpsSocketEvent.getFirstPacketPeriod());
                    break;
                }
                LOG.debug("WebEventStore socket pollin, but no fd related, fd:" + fd + "or FirstPacket has set");
                break;
            }
            case 7: { // DNS event
                if (address == null) {
                    break;
                }
                if (address.isEmpty()) {
                    break;
                }
                final DnsEvent dnsEvent = new DnsEvent();
                dnsEvent.setHost(host);
                dnsEvent.setDestIpList(address);
                dnsEvent.setDesc(desc);
                dnsEvent.setBeginTimeStamp(startTime);
                dnsEvent.setDnsConsumeTime(timeElapsed);
                dnsEvent.setPort(port);
                dnsEvent.setErrorNumber(errorNum);
                dnsEvent.setRetValue(returnValue);
                EventCache.addDnsEvent(dnsEvent);
                LOG.debug("WebEventStore DNS event");
                break;
            }
            case 8: { // socket send over
                final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                if (httpsSocketEvent != null) {
                    httpsSocketEvent.setWriteOverTime(startTime);
                    LOG.debug("WebEventStore socket send over, fd:" + fd);
                } else {
                    LOG.debug("WebEventStore socket send over, fd:" + fd + ", no fd related");
                }
                break;
            }
            case 9: { // socket received
                if (returnValue > 0) {
                    final HttpsSocketEvent httpsSocketEvent = EventCache.httpsSocketEventMap.get(fd);
                    if (httpsSocketEvent != null && httpsSocketEvent.getFirstPacketPeriod() == 0) {
                        httpsSocketEvent.setFirstPacketPeriod((int)((startTime
                                - httpsSocketEvent.getWriteOverTime()) * 1000 * 1000 + timeElapsed));
                        EventCache.socketMap.put(httpsSocketEvent.getIpAddress(), httpsSocketEvent.getFd());
                        LOG.debug("WebEventStore socket received,  fd:" + fd
                                + ", firstPacket period:" + httpsSocketEvent.getFirstPacketPeriod()
                                + ", eventCached.getWriteOverTime:" + httpsSocketEvent.getWriteOverTime());
                    } else {
                        LOG.debug("WebEventStore socket received, fd:" + fd + ", no fd related or FirstPacket has set");
                    }
                    break;
                }
                break;
            }
            default:
                break;
        }

    }


    
    private static boolean isHostName(final String str) {
        LOG.debug("WebEventStore isHostName");
        boolean b = false;
        if (Pattern.compile("[A-Za-z]+").matcher(str).find()) {
            b = true;
        }
        return b;
    }
    
    public static synchronized void setWebViewEvent(final int respCode,
                                                    final String reasons,
                                                    final int contentlength,
                                                    final boolean chunkTranf,
                                                    final short btsend,
                                                    final String host,
                                                    final String reqMethod,
                                                    final String path,
                                                    final int period,
                                                    final String addr,
                                                    final double connStartTime,
                                                    final double connEndTime,
                                                    final double pollOutTime,
                                                    final double pollInTime,
                                                    final short port,
                                                    final short btrecv,
                                                    final double firstpackagetime,
                                                    final double ssltime) {
        LOG.debug("WebEventStore setWebViewEvent: respCode = " + respCode
                + ", reasons= " + reasons
                + ", contentlength = " + contentlength
                + ", chunkTranf = " + chunkTranf
                + ", btsend = " + btsend
                + ", host = " + host
                + ", reqMethod = " + reqMethod
                + ", path = " + path
                + ", period = " + period
                + ", addr = " + addr
                + ", connStartTime = " + connStartTime
                + ", connEndTime = " + connEndTime
                + ", pollOutTime = " + pollOutTime
                + ", pollInTime = " + pollInTime
                + ", port = " + port
                + ", btrecv = " + btrecv
                + ", firstpackagetime = " + firstpackagetime
                + ", ssltime = " + ssltime);

//        if (host == null || host == "") {
//            return;
//        }
//        final m m = new m();
//        m.x = respCode;
//        m.p = btsend;
//        if (host.endsWith("\r") || host.endsWith("\n")) {
//            m.r = host.substring(0, host.length() - 1);
//        }
//        else {
//            m.r = host;
//        }
//        m.s = reqMethod;
//        m.t = path;
//        m.w = period;
//        m.g = connStartTime;
//        m.h = connEndTime;
//        m.j = pollOutTime;
//        m.m = pollInTime;
//        m.f = port;
//        m.q = btrecv;
//        m.y = ssltime;
//        m.z = firstpackagetime;
//        m.a(addr);
//        LOG.debug("respCode = " + respCode
//                + ",contentlength = " + contentlength
//                + ", btsend = " + btsend
//                + ", host = " + m.r
//                + "reqMethod = " + reqMethod
//                + ", path = " + path
//                + ", period = " + period
//                + ", addr = " + addr
//                + ", connStartTime = " + connStartTime
//                + ", connEndTime = " + connEndTime
//                + ", pollOutTime = " + pollOutTime
//                + ", pollInTime = " + pollInTime
//                + "port = " + port
//                + ", btrecv = " + btrecv
//                + ", ssltime = " + ssltime);
//        ah.a(m);
    }
    
    public static synchronized void setUsefulInfo(final int type,
                                                  final String addr,
                                                  final double connecttime,
                                                  final double firstPgtime,
                                                  final double ssl_time) {
        LOG.debug("WebEventStore setUsefulInfo: addr = " + addr
                + ", connecttime = " + connecttime
                + ", firstPgtime = " + firstPgtime
                + ", ssl_time = " + ssl_time
                + ", type = " + type);
//        final UsefulData usefulData = new UsefulData();
//        usefulData.type = type;
//        usefulData.addr = addr;
//        usefulData.connecttime = connecttime;
//        usefulData.firstpgtime = firstPgtime;
//        usefulData.ssltime = ssl_time;
//        ah.a(usefulData);
    }
    
    public static synchronized void addHttpActionMeasurement(final String userAgent,
                                                             final String url,
                                                             String ipAddress,
                                                             final String appData,
                                                             final String urlParams,
                                                             String requestMethod,
                                                             int statusCode,
                                                             final int httpVisitNumber,
                                                             int errorCode,
                                                             final int totalTime,
                                                             final int firstPacketPeriod,
                                                             final int tcpHandShakePeriod,
                                                             final int sslPeriod,
                                                             final int bytesSent,
                                                             final int bytesReceived,
                                                             final long startTime) {
        LOG.debug("WebEventStore addHttpActionMeasurement");
//        requestMethod = requestMethod.toUpperCase();
//        final RequestMethodType value = RequestMethodType.valueOf(requestMethod);
//        final n impl = NBSAgent.getImpl();
//        if (impl == null) {
//            return;
//        }
//        final HarvestConfiguration q = impl.q();
//        if (q == null) {
//            return;
//        }
//        if (!al.b(url, q.getUrlFilterMode(), q.getUrlRules())) {
//            return;
//        }
//        if ((errorCode > 400 || errorCode == -1) && al.a(url, statusCode, q.getIgnoreErrRules())) {
//            statusCode = 200;
//            errorCode = 0;
//        }
//        ipAddress = z.b(ipAddress);
//        HttpLibType httpLibType;
//        if (userAgent.contains("Dalvik")) {
//            httpLibType = HttpLibType.URLConnection;
//        }
//        else if (userAgent.contains("Apache-HttpClient")) {
//            httpLibType = HttpLibType.HttpClient;
//        }
//        else if (userAgent.contains("okhttp")) {
//            httpLibType = HttpLibType.OkHttp;
//        }
//        else {
//            httpLibType = HttpLibType.Other;
//        }
//        ah.a(new a(url, ipAddress, statusCode, httpVisitNumber, startTime, totalTime, firstPacketPeriod, tcpHandShakePeriod, sslPeriod, bytesSent, bytesReceived, appData, urlParams, value, httpLibType));
    }
    
    public static synchronized void produceSocketEvent(final int fd,
                                                       final String address,
                                                       final int port,
                                                       final boolean connectSuccess,
                                                       final int errorNum,
                                                       final double socketCreateTime,
                                                       final double connectFromTime,
                                                       final double pollOutTime,
                                                       final double lastSendTime,
                                                       final double pollInTime,
                                                       final int handshakePeriod,
                                                       final int firstPakcetPeriod) {
        LOG.debug("WebEventStore produceSocketEvent");
//        final JavaSocketEvent javaSocketEvent = new JavaSocketEvent();
//        javaSocketEvent.setFd(fd);
//        javaSocketEvent.setIpAddress(address);
//        javaSocketEvent.setPort(port);
//        javaSocketEvent.setConnectSuccess(connectSuccess);
//        javaSocketEvent.setErrorNumber(errorNum);
//        javaSocketEvent.setSocketCreateTime(socketCreateTime);
//        javaSocketEvent.setConnectFromTime(connectFromTime);
//        javaSocketEvent.setPollOutTime(pollOutTime);
//        javaSocketEvent.setLastSendTime(lastSendTime);
//        javaSocketEvent.setPollInTime(pollInTime);
//        javaSocketEvent.setHandshakePeriod(handshakePeriod);
//        javaSocketEvent.setFirstPakcetPeriod(firstPakcetPeriod);
//        ah.a(javaSocketEvent);
    }
    
    public static synchronized void produceHttpsSocketEvent(final int fd,
                                                            final String ipAddress,
                                                            final int port,
                                                            final int tcpHandshakePeriod,
                                                            final double socketCreateTime) {
        LOG.debug("WebEventStore produceHttpsSocketEvent");
//        final HttpsSocketEvent httpsSocketEvent = new HttpsSocketEvent();
//        httpsSocketEvent.setFd(fd);
//        httpsSocketEvent.setIpAddress(ipAddress);
//        httpsSocketEvent.setPort(port);
//        httpsSocketEvent.setTcpHandshakePeriod(tcpHandshakePeriod);
//        httpsSocketEvent.setSocketCreateTime(socketCreateTime);
//        ah.b(httpsSocketEvent);
    }
    

    
    public static synchronized void produceDNSEvent(final String host,
                                                    final String address,
                                                    final String desc,
                                                    final double startTime,
                                                    final int timeElapsed,
                                                    final int port,
                                                    final int errorNumber,
                                                    final int retValue) {
        LOG.debug("WebEventStore produceDNSEvent");
//        final com.networkbench.agent.impl.c.a a = new com.networkbench.agent.impl.c.a();
//        a.b(host);
//        a.c(address);
//        a.setDesc(desc);
//        a.a(startTime);
//        a.a(timeElapsed);
//        a.d(port);
//        a.b(errorNumber);
//        a.c(retValue);
//        ah.a(a);
    }
    
    private static String[] separateAddr(final String address) {
        if (null == address || address == "") {
            return null;
        }
        return address.split(";");
    }
    
    private static synchronized String combineUrl(final String host, final String path, final int port) {
        LOG.debug("WebEventStore combineUrl");
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        final StringBuilder sb = new StringBuilder();
        String s;
        if (80 == port) {
            s = "http://";
        }
        else if (443 == port) {
            s = "https://";
        }
        else {
            s = "http://";
        }
        sb.append(s).append(host).append(path);
        return sb.toString();
    }
    
    public static class UsefulData
    {
        public int type;
        public String addr;
        public double connecttime;
        public double firstpgtime;
        public double ssltime;
        
        public UsefulData() {
            this.addr = "";
        }
    }


}
