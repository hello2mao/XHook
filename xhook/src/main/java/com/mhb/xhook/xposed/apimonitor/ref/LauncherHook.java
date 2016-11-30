package com.mhb.xhook.xposed.apimonitor.ref;

import android.view.View;

import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

/**
 * Created by maohongbin01 on 16/9/28.
 */

public class LauncherHook extends ApiMonitorHook {
    @Override
    public void startHook() {
        // com.android.launcher.Launcher#onClick
        // public void onClick(View v)
        Method onClickMethod = RefInvoke.findMethodExact("com.android.launcher2.Launcher",
                ClassLoader.getSystemClassLoader(), "onClick", View.class);
        if (null == onClickMethod) {
            LOG.error("findMethodExact com.android.launcher2.Launcher#onClick failed");
            return;
        }
        hookHelper.hookMethod(onClickMethod, new OnClickMethodHook());


    }

    private class OnClickMethodHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {
            View v = (View) param.args[0];
            int viewId = v.getId();
            LOG.debug("OnClickMethodHook viewId=" + viewId + "toString=" + v.toString());
        }

        @Override
        public void afterHookedMethod(HookParam param) {

        }
    }
}
