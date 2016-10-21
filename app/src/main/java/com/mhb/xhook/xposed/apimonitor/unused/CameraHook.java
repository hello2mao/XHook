package com.mhb.xhook.xposed.apimonitor.unused;

import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class CameraHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        // TODO Auto-generated method stub
        Method takePictureMethod = RefInvoke.findMethodExact(
                "android.hardware.Camera", ClassLoader.getSystemClassLoader(),
                "takePicture",ShutterCallback.class,PictureCallback.class,PictureCallback.class,PictureCallback.class);
        hookHelper.hookMethod(takePictureMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Camera take a picture->");
            }
        });

        Method setPreviewCallbackMethod = RefInvoke.findMethodExact(
                "android.hardware.Camera", ClassLoader.getSystemClassLoader(),
                "setPreviewCallback",PreviewCallback.class);
        hookHelper.hookMethod(setPreviewCallbackMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Camera setPreview ->");
            }
        });

        Method setPreviewCallbackWithBufferMethod = RefInvoke.findMethodExact(
                "android.hardware.Camera", ClassLoader.getSystemClassLoader(),
                "setPreviewCallbackWithBuffer",PreviewCallback.class);
        hookHelper.hookMethod(setPreviewCallbackWithBufferMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Camera setPreview ->");
            }
        });

        Method setOneShotPreviewCallbackMethod = RefInvoke.findMethodExact(
                "android.hardware.Camera", ClassLoader.getSystemClassLoader(),
                "setOneShotPreviewCallback",PreviewCallback.class);
        hookHelper.hookMethod(setOneShotPreviewCallbackMethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Camera setPreview ->");
            }
        });
    }

}
