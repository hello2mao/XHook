package com.mhb.xhook.networklib;


import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class EventCache {

    private static final BasicLog LOG = XhookLogManager.getInstance();
    private static final long f = 1000L;
    private static final ScheduledExecutorService g
            = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentLinkedQueue<Object> h
            = new ConcurrentLinkedQueue<Object>();
    // TODO:When to clear?
    public static final ConcurrentHashMap<String, DnsEvent> dnsEventMap
            = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, JavaSocketEvent> b
            = new ConcurrentHashMap<String, JavaSocketEvent>();
    public static final ConcurrentHashMap<String, HttpsSocketEvent> c
            = new ConcurrentHashMap<String, HttpsSocketEvent>();
    public static final ConcurrentHashMap<Integer, HttpsSocketEvent> httpsSocketEventMap
            = new ConcurrentHashMap<Integer, HttpsSocketEvent>();
    public static final ConcurrentHashMap<String, Integer> socketMap = new ConcurrentHashMap<>();
//    private static c i = com.networkbench.agent.impl.e.d.a();
//    private static final Runnable j = new Runnable() {
//        @Override
//        public void run() {
//            i();
//        }
//    };
    private static Future<?> k;

    public static void a(final Object o) {
        EventCache.h.add(o);
    }
    
    public static void addHttpsSocketEvent(final HttpsSocketEvent httpsSocketEvent) {
        final int fd = httpsSocketEvent.getFd();
        if (fd > 2) {
            EventCache.httpsSocketEventMap.put(fd, httpsSocketEvent);
        }
    }
    
    public static void addDnsEvent(final DnsEvent dnsEvent) {
        EventCache.dnsEventMap.put(dnsEvent.getHost(), dnsEvent);
    }
    
    public static void a(final JavaSocketEvent javaSocketEvent) {
        String ipAddress = javaSocketEvent.getIpAddress();
        try {
            final InetAddress byName = InetAddress.getByName(ipAddress);
            if (byName instanceof Inet6Address) {
                ipAddress = byName.getHostAddress();
            }
            else if (byName instanceof Inet4Address) {
                ipAddress = byName.getHostAddress();
            }
        }
        catch (UnknownHostException ex) {}
        javaSocketEvent.setIpAddress(ipAddress);
        EventCache.b.put(ipAddress, javaSocketEvent);
    }
    
    public static void b(final HttpsSocketEvent httpsSocketEvent) {
        String ipAddress = httpsSocketEvent.getIpAddress();
        try {
            final InetAddress byName = InetAddress.getByName(ipAddress);
            if (byName instanceof Inet6Address) {
                ipAddress = byName.getHostAddress();
            }
            else if (byName instanceof Inet4Address) {
                ipAddress = byName.getHostAddress();
            }
        }
        catch (UnknownHostException ex) {}
        httpsSocketEvent.setIpAddress(ipAddress);
        EventCache.c.put(ipAddress, httpsSocketEvent);
    }
    
//    public static void a() {
//        for (final Map.Entry<String, a> entry : EventCache.a.entrySet()) {
//            if (null != entry) {
//                final a socketdata = entry.getValue();
//                if (null == socketdata || socketdata.isHttp() || !socketdata.isSend()) {
//                    continue;
//                }
//                final String c = socketdata.c();
//                HttpsSocketEvent httpsSocketEvent = null;
//                if (null != c) {
//                    final String[] split = c.split(";");
//                    if (split != null && split.length > 0) {
//                        for (int i = 0; i < split.length; ++i) {
//                            final Integer n = EventCache.e.get(split[i]);
//                            if (n != null) {
//                                socketdata.a(split[i]);
//                                httpsSocketEvent = EventCache.httpsSocketEventMap.get(n);
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (null != httpsSocketEvent) {
//                    httpsSocketEvent.setHost(socketdata.a());
//                }
//                Harvest.addSocketDatasInfo(socketdata);
//            }
//        }
//        for (final Map.Entry<Integer, HttpsSocketEvent> entry2 : EventCache.httpsSocketEventMap.entrySet()) {
//            if (null != entry2) {
//                final HttpsSocketEvent socketdata2 = entry2.getValue();
//                if (null == socketdata2 || socketdata2.isHttp() || !socketdata2.isSend()) {
//                    continue;
//                }
//                Harvest.addSocketDatasInfo(socketdata2);
//            }
//        }
//    }
    
    public static DnsEvent getDnsEvent(final String host) {
        return EventCache.dnsEventMap.get(host);
    }
    
    public static JavaSocketEvent b(final String s) {
        return EventCache.b.get(s);
    }
    
//    public static void b() {
//        EventCache.g.execute(EventCache.j);
//    }
    
//    public static void c() {
//        final Future<?> submit = EventCache.g.submit(EventCache.j);
//        try {
//            submit.get();
//        }
//        catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        catch (ExecutionException ex2) {
//            ex2.printStackTrace();
//        }
//    }
    
//    public static void d() {
//        if (EventCache.k != null) {
//            return;
//        }
//        EventCache.k = EventCache.g.scheduleAtFixedRate(EventCache.j, 0L, 1000L, TimeUnit.MILLISECONDS);
//    }
    
    public static void e() {
        if (EventCache.k == null) {
            return;
        }
        EventCache.k.cancel(true);
        EventCache.k = null;
    }
    
    public static int f() {
        return EventCache.h.size();
    }
    
    public static void g() {
        EventCache.h.clear();
    }
    

}
