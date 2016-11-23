package com.mhb.xhook.xposed.apimonitor.ref;


import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class AudioRecordHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        Method startRecordingMethod = RefInvoke.findMethodExact(
                "android.media.AudioRecord", ClassLoader.getSystemClassLoader(),
                "startRecording");
        hookHelper.hookMethod(startRecordingMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Audio Recording ->");
            }
        });

    }

}
