package com.mhb.xhook.xposed.apimonitor.unused;

import android.app.PendingIntent;
import android.util.Base64;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class SmsManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method sendTextMessagemethod = RefInvoke.findMethodExact(
                "android.telephony.SmsManager", ClassLoader.getSystemClassLoader(),
                "sendTextMessage", String.class,String.class,String.class,PendingIntent.class,PendingIntent.class);
        hookHelper.hookMethod(sendTextMessagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Send SMS ->");
                String dstNumber = (String)param.args[0];
                String content = (String)param.args[2];
                LOG.debug("SMS DestNumber:"+dstNumber);
                LOG.debug("SMS Content:"+content);
            }
        });

        Method getAllMessagesFromIccmethod = RefInvoke.findMethodExact(
                "android.telephony.SmsManager", ClassLoader.getSystemClassLoader(),
                "getAllMessagesFromIcc");
        hookHelper.hookMethod(getAllMessagesFromIccmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Read SMS From Icc ->");
            }
        });

        Method sendDataMessagemethod = RefInvoke.findMethodExact(
                "android.telephony.SmsManager", ClassLoader.getSystemClassLoader(),
                "sendDataMessage",String.class,String.class,short.class,byte[].class,PendingIntent.class,PendingIntent.class);
        hookHelper.hookMethod(sendDataMessagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Send Data SMS ->");
                String dstNumber = (String)param.args[0];
                short port = (Short)param.args[2];
                String content = Base64.encodeToString((byte[]) param.args[3],0);
                LOG.debug("SMS DestNumber:"+dstNumber);
                LOG.debug("SMS destinationPort:"+port);
                LOG.debug("SMS Base64 Content:"+content);
            }
        });

        Method sendMultipartTextMessagemethod = RefInvoke.findMethodExact(
                "android.telephony.SmsManager", ClassLoader.getSystemClassLoader(),
                "sendMultipartTextMessage",String.class,String.class,ArrayList.class,ArrayList.class,ArrayList.class);
        hookHelper.hookMethod(sendMultipartTextMessagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Send Multipart SMS ->");
                String dstNumber = (String)param.args[0];
                ArrayList<String> sms = (ArrayList<String>) param.args[2];
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<sms.size(); i++){
                    sb.append(sms.get(i));
                }
                LOG.debug("SMS DestNumber:"+dstNumber);
                LOG.debug("SMS Content:"+sb.toString());
            }
        });


    }

}
