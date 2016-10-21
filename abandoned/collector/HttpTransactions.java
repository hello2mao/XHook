package com.mhb.xhook.xposed.collecter;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by maohongbin01 on 16/7/28.
 */
public class HttpTransactions {

    private static BasicLog log = XHookLogManager.getInstance();

    private final Collection httpTransactions = new ArrayList();
    private static HttpTransactions instance = null;

    private HttpTransactions() {}

    public static HttpTransactions getInstance() {
        if (null == instance) {
            synchronized (HttpTransactions.class) {
                if (null == instance) {
                    instance = new HttpTransactions();
                }
            }
        }
        return instance;
    }

    public synchronized void add(HttpTransaction httpTransaction) {
        httpTransactions.add(httpTransaction);
    }

    public synchronized void remove(HttpTransaction httpTransaction) {
        httpTransactions.remove(httpTransaction);
    }

    public void clear() {
        httpTransactions.clear();
    }

    public Collection getHttpTransactions() {
        return httpTransactions;
    }

    public int count() {
        return httpTransactions.size();
    }

    public HttpTransaction getRunningHttp(long threadId) {
        HttpTransaction transaction;
        HttpTransaction transactionFound = null;
        Iterator iter = httpTransactions.iterator();
        while (iter.hasNext()) {
            transaction = (HttpTransaction) iter.next();
            if ((transaction.getThreadId() == threadId)
                    && (transaction.getTransactionState() == HttpTransaction.TransactionState.running)) {
                // TODO:currently, set older one state to finish and return the newer one
                if (transactionFound != null) {
                    transactionFound.setTransactionState(HttpTransaction.TransactionState.finish);
                    log.error("find another running transaction");
                }
                transactionFound = transaction;
            }
        }
        return transactionFound;
    }



    public String toString() {
        StringBuilder sb = new StringBuilder().append("HttpTransactions{\n");
        Iterator iter = httpTransactions.iterator();
        HttpTransaction transaction;
        while (iter.hasNext()) {
            transaction = (HttpTransaction) iter.next();
            if (transaction != null) {
                sb.append(transaction.toString()).append(" \n");
            }
        }

        return sb.append('}').toString();
    }

}
