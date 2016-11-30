package com.mhb.xhook.xposed.apimonitor.ref;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.io.FileDescriptor;
import java.lang.reflect.Method;

public class MediaRecorderHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method startmethod = RefInvoke.findMethodExact(
                "android.media.MediaRecorder", ClassLoader.getSystemClassLoader(),
                "start");
        hookHelper.hookMethod(startmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Media Record: Start ->");
                String mPath = (String)RefInvoke.getFieldOjbect("android.media.MediaRecorder", param.thisObject, "mPath");
                if(mPath != null)
                   LOG.debug("Save Path: ->" +mPath);
                else{
                    FileDescriptor mFd = (FileDescriptor) RefInvoke.getFieldOjbect("android.media.MediaRecorder", param.thisObject, "mFd");
                    LOG.debug("Save Path: ->" +mFd.toString());
                }
            }
        });

        Method stopmethod = RefInvoke.findMethodExact(
                "android.media.MediaRecorder", ClassLoader.getSystemClassLoader(),
                "stop");
        hookHelper.hookMethod(stopmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Media Record: Stop ->");
            }
        });

    }

}
