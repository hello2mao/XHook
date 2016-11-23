package com.mhb.xhook.xposed.apimonitor.nbs;

import android.content.Context;

import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class NBSAppAgentHook extends ApiMonitorHook {

    @Override
    public void startHook() {
        try {
            Class nbsClass = Class.forName("com.networkbench.agent.impl.NBSAppAgent");
            if (nbsClass != null) {
                Method mStartMethod = RefInvoke.findMethodExact(
                        "com.networkbench.agent.impl.NBSAppAgent",
                        ClassLoader.getSystemClassLoader(), "start", Context.class);
                if (null == mStartMethod) {
                    LOG.error("findMethodExact failed");
                    return;
                }
                hookHelper.hookMethod(mStartMethod, new NBSAppAgentHook.StartHook());
            }
            LOG.debug("class not exist!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private class StartHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {


            Class<?> clazz = null;
            try {
                clazz = XposedHelpers.findClass("com.networkbench.agent.impl.NBSAppAgent", null);
            } catch (XposedHelpers.ClassNotFoundError e) {
                e.printStackTrace();
            }
            if (clazz == null) {
                LOG.error("Hook-Class not found");
            }
            Field field = null;
            try {
                field = clazz.getDeclaredField("LOG_LEVEL_FLAG");
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                // treeLock是一个final成员，所以记得要修改其修饰，去掉final
                modifiersField.setInt(field, field.getModifiers() & 0xffffffef);
                field.setInt(null, 31);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void afterHookedMethod(HookParam param) {

        }
    }

}