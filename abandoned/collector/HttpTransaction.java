package com.mhb.xhook.xposed.collecter;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by maohongbin01 on 16/7/27.
 */
public class HttpTransaction {

    private static BasicLog log = XHookLogManager.getInstance();

    // main
    private ElementData elementData;
    // URLConnection
    private URLConnection connection;
    // HttpClient
    HttpClient httpClient;
    HttpRequest request;
    HttpResponse response;

    private long threadId = -1;
    private NetworkAPI networkAPI = NetworkAPI.defaultAPI;
    private TransactionState transactionState = TransactionState.idle;

    // NetwrokAPI
    public enum NetworkAPI {
        defaultAPI, URLConnection, HttpClient, OkHttp, Retrofit
    }

    // http transaction state
    public enum TransactionState {
        idle, running, finish
    }

    // HttpURLConnection
    public HttpTransaction(URLConnection connection, long threadId) {
        this.connection = connection;
        this.threadId = threadId;
        this.networkAPI = NetworkAPI.URLConnection;
        this.elementData = new ElementData();
    }

    // HttpClient
    public HttpTransaction(HttpClient httpClient, HttpRequest request, long threadId) {
        this.httpClient = httpClient;
        this.request = request;
        this.threadId = threadId;
        this.networkAPI = NetworkAPI.HttpClient;
        this.elementData = new ElementData();
        this.initHttpClientElementData();
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public NetworkAPI getNetworkAPI() {
        return networkAPI;
    }

    public void setNetworkAPI(NetworkAPI networkAPI) {
        this.networkAPI = networkAPI;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public boolean checkNetworkAPI(NetworkAPI networkAPI) {
        if (networkAPI == this.networkAPI) {
            return true;
        }
        return false;
    }

    public void initHttpURLElementData() {
//        if ((elementData.getUrl() != null) && (elementData.getUrl() != ""))  {
//            log.debug("element data already init");
//            return;
//        }
        if (this.connection instanceof HttpsURLConnection) {
            log.debug("HttpsURLConnection");
        } else if (this.connection instanceof HttpURLConnection) {
            initHttpURLConnectionElementData((HttpURLConnection) connection);
        }
    }

    public void finishHttpURLConnect() {
        if (this.connection instanceof HttpsURLConnection) {
            log.debug("HttpsURLConnection");
        } else if (this.connection instanceof HttpURLConnection) {
            finishHttpURLConnect((HttpURLConnection) this.connection);
        }
    }

    public void finishHttpURLDisconnect() {
        if (this.connection instanceof HttpsURLConnection) {
            log.debug("HttpsURLConnection");
        } else if (this.connection instanceof HttpURLConnection) {
            finishHttpURLDisconnect((HttpURLConnection) this.connection);
        }
    }

    public void initHttpClientElementData() {
        if (this.request instanceof HttpGet) {
            HttpGet httpGet = (HttpGet) request;
            this.elementData.setUrl(httpGet.getURI().toString());
            this.elementData.setRequestMethod(httpGet.getMethod());
//            Header[] headers = httpGet.getAllHeaders();
//            Map< String, List< String>> requestHeader = new HashMap<>();
//            List<String> headerValue = new ArrayList<>();
//            if (headers != null) {
//                for (int i = 0; i < headers.length; i++) {
//                    headerValue.add(headers[i].getValue());
//                    requestHeader.put(headers[i].getName(), headerValue);
//                }
//            }
//            this.elementData.setRequestHeader(requestHeader);
        } else if (this.request instanceof HttpPost) {
            HttpPost httpPost = (HttpPost) request;
            this.elementData.setUrl(httpPost.getURI().toString());
            this.elementData.setRequestMethod(httpPost.getMethod());
//            Header[] headers = httpPost.getAllHeaders();
//            Map< String, List< String>> requestHeader = new HashMap<>();
//            List<String> headerValue = new ArrayList<>();
//            if (headers != null) {
//                for (int i = 0; i < headers.length; i++) {
//                    headerValue.add(headers[i].getValue());
//                    requestHeader.put(headers[i].getName(), headerValue);
//                }
//            }
//            this.elementData.setRequestHeader(requestHeader);
            this.elementData.setRequestMimeType(httpPost.getEntity().getContentType().getValue());
        }

    }

    public void finishHttpClientExecute(HttpResponse response) {
        this.response = response;
        if (response != null) {
            this.elementData.setResponseCode(response.getStatusLine().getStatusCode());
        }
//        Header[] headers = response.getAllHeaders();
//        Map< String, List< String>> requestHeader = new HashMap<>();
//        List<String> headerValue = new ArrayList<>();
//        if (headers != null) {
//            for (int i = 0; i < headers.length; i++) {
//                headerValue.add(headers[i].getValue());
//                requestHeader.put(headers[i].getName(), headerValue);
//            }
//        }
//        this.elementData.setResponseHeader(requestHeader);
    }

    public void finishHttpClient() {
        // TODO
        if (this.DNSStartTime != Long.MAX_VALUE) {
            this.elementData.setDNSTime(this.DNSEndTime - this.DNSStartTime);
            this.elementData.setIsValidDNS(true);
        }
        if (this.TCPStartTime != Long.MAX_VALUE) {
            this.elementData.setTCPTime(this.TCPEndTime - this.TCPStartTime);
        }
        if (this.SSLStartTime != Long.MAX_VALUE) {
            this.elementData.setSSLTime(this.SSLEndTime - this.SSLStartTime);
        }
        if (this.downloadStartTime != Long.MAX_VALUE) {
            this.elementData.setDownloadTime(this.downloadEndTime - this.downloadStartTime);
        }
        this.setTransactionState(TransactonState.finish);
        log.debug(HttpTransactions.getInstance().toString());
    }



    public Object getConnection() {
        return this.connection;
    }

    public ElementData getElementData() {
        return this.elementData;
    }

    public long getTimeZero() {
        return timeZero;
    }

    private void initHttpURLConnectionElementData(HttpURLConnection connection) {
        this.elementData.setUrl(connection.getURL().toString());
        this.elementData.setRequestMethod(connection.getRequestMethod());
        this.elementData.setRequestHeader(connection.getRequestProperties());
        this.elementData.setRequestMimeType(connection.getRequestProperty("Content-Type"));
    }

    public void finishHttpURLConnect(HttpURLConnection connection) {
        this.elementData.setResponseHeader(connection.getHeaderFields());
        this.elementData.setResponseMimeType(connection.getContentType());
        try {
            this.elementData.setResponseCode(connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishHttpURLDisconnect(HttpURLConnection connection) {
        // TODO
        if (this.DNSStartTime != Long.MAX_VALUE) {
            this.elementData.setDNSTime(this.DNSEndTime - this.DNSStartTime);
            this.elementData.setIsValidDNS(true);
        }
        if (this.TCPStartTime != Long.MAX_VALUE) {
            this.elementData.setTCPTime(this.TCPEndTime - this.TCPStartTime);
        }
        if (this.SSLStartTime != Long.MAX_VALUE) {
            this.elementData.setSSLTime(this.SSLEndTime - this.SSLStartTime);
        }
        if (this.downloadStartTime != Long.MAX_VALUE) {
            this.elementData.setDownloadTime(this.downloadEndTime - this.downloadStartTime);
        }
        this.setTransactionState(TransactonState.finish);
        log.debug(HttpTransactions.getInstance().toString());
    }

    public String toString() {
        return (new StringBuilder())
                .append("HttpTransaction{")
                .append("threadId=").append(threadId)
                .append(", networkAPI=").append(networkAPI)
                .append(", transactonState=").append(transactionState)
                .append(", ").append(elementData.toString())
                .append('}').toString();
    }
}
