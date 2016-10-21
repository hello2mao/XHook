package com.mhb.xhook.xposed.apimonitor;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class ViewHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        // android.view.View#dispatchTouchEvent
        // public boolean dispatchTouchEvent(MotionEvent event)
        Method mViewDispatchTouchEventMethod = RefInvoke.findMethodExact("android.view.View",
                ClassLoader.getSystemClassLoader(), "dispatchTouchEvent", MotionEvent.class);
        if (null == mViewDispatchTouchEventMethod) {
            LOG.error("findMethodExact android.view.View#dispatchTouchEvent failed");
            return;
        }
        hookHelper.hookMethod(mViewDispatchTouchEventMethod, new ViewHook.DispatchTouchEventHook());

        // android.view.View#draw
        // boolean draw(Canvas canvas, ViewGroup parent, long drawingTime)
        Method mViewDrawMethod = RefInvoke.findMethodExact("android.view.View",
                ClassLoader.getSystemClassLoader(), "draw", Canvas.class, ViewGroup.class, long.class);
        if (null == mViewDrawMethod) {
            LOG.error("findMethodExact android.view.View#draw");
            return;
        }
        hookHelper.hookMethod(mViewDrawMethod, new ViewHook.ViewDrawHook());


        // android.view.HardwareRenderer$GLRender.draw
        // draw(View view, View.AttachInfo attachInfo, HardwareDrawCallbacks callbacks,Rect dirty)
        Class attachInfoClass = null;
        Class hardwareDrawCallbacksClass = null;
        try {
            attachInfoClass = Class.forName("android.view.View$AttachInfo");
            hardwareDrawCallbacksClass = Class.forName("android.view.HardwareRenderer$HardwareDrawCallbacks");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (attachInfoClass == null | hardwareDrawCallbacksClass == null) {
            LOG.error("find class error: attachInfoClass&hardwareDrawCallbacksClass");
        }
        Method mGLRenderDrawMethod = RefInvoke.findMethodExact("android.view.HardwareRenderer$GlRenderer",
                ClassLoader.getSystemClassLoader(), "draw", View.class, attachInfoClass,
                hardwareDrawCallbacksClass, Rect.class);
        hookHelper.hookMethod(mGLRenderDrawMethod, new ViewHook.GLRenderDrawHook());
    }

    private class DispatchTouchEventHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {
            View v = (View) param.thisObject;
            MotionEvent me = (MotionEvent) param.args[0];
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                int viewId = v.getId();
                if (viewId != -1) {
                    LOG.debug("DispatchTouchEvent viewId=" + viewId + "toString=" + v.toString());
                }
            }
            LOG.debug(me.toString());
        }

        @Override
        public void afterHookedMethod(HookParam param) {

        }
    }

    private class ViewDrawHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {
            View v = (View) param.thisObject;
            int viewId = v.getId();
            if (viewId != -1) {
                LOG.debug("viewId = " + viewId + ", toString = " + v.toString());
            }
        }

        @Override
        public void afterHookedMethod(HookParam param) {

        }
    }

    private  class GLRenderDrawHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {

        }

        @Override
        public void afterHookedMethod(HookParam param) {

        }
    }
}
