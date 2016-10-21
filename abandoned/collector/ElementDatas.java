package com.mhb.xhook.xposed.collecter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by maohongbin01 on 16/7/26.
 */
public class ElementDatas {

    private final Collection elementDatas = new ArrayList();

    public synchronized void add(ElementData elementData) {
        elementDatas.add(elementData);
    }

    public synchronized void remove(ElementData elementData) {
        elementDatas.remove(elementData);
    }

    public void clear() {
        elementDatas.clear();
    }

    public Collection getHttpTransactions() {
        return elementDatas;
    }

    public int count() {
        return elementDatas.size();
    }

    public String toString() {
        return (new StringBuilder()).append("ElementDatas{elementDatas=")
                .append(elementDatas).append('}').toString();
    }

}
