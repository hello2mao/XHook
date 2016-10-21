package com.mhb.xhook.xposed.apimonitor.unused;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.mhb.xhook.xposed.apimonitor.AbstractBehaviorHookCallBack;
import com.mhb.xhook.xposed.apimonitor.ApiMonitorHook;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.util.RefInvoke;

import java.lang.reflect.Method;

public class PackageManagerHook extends ApiMonitorHook {

    @Override
    public void startHook() {

        Method setComponentEnableSettingmethod = RefInvoke.findMethodExact("android.app.ApplicationPackageManager",
                ClassLoader.getSystemClassLoader(), "setComponentEnabledSetting", ComponentName.class, int.class, int.class);
        hookHelper.hookMethod(setComponentEnableSettingmethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                ComponentName cn = (ComponentName) param.args[0];
                int newState = (Integer) param.args[1];
                LOG.debug("Set ComponentEnabled ->");
                LOG.debug("The Component ClassName: " + cn.getPackageName() + "/" + cn.getClassName());
                if (newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
                    LOG.debug("Component New State = " + "COMPONENT_ENABLED_STATE_DISABLED");
                if (newState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
                    LOG.debug("Component New State = " + "COMPONENT_ENABLED_STATE_ENABLED");
                if (newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER)
                    LOG.debug("Component New State = " + "COMPONENT_ENABLED_STATE_DISABLED_USER");
                if (newState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
                    LOG.debug("Component New State = " + "COMPONENT_ENABLED_STATE_DEFAULT");
            }
        });

        Method installPackagemethod = null;
        try {
            installPackagemethod = RefInvoke.findMethodExact("android.app.ApplicationPackageManager", ClassLoader.getSystemClassLoader(),
                    "installPackage", Uri.class, Class.forName("android.content.pm.IPackageInstallObserver"), int.class, String.class);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hookHelper.hookMethod(installPackagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                Uri uri = (Uri) param.args[0];
                LOG.debug("Slient Install APK ->");
                LOG.debug("The APK URL = " + uri.toString());
            }
        });

        Method deletePackagemethod = null;
        try {
            deletePackagemethod = RefInvoke.findMethodExact("android.app.ApplicationPackageManager", ClassLoader.getSystemClassLoader(),
                    "deletePackage", String.class, Class.forName("android.content.pm.IPackageDeleteObserver"), int.class);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hookHelper.hookMethod(deletePackagemethod, new AbstractBehaviorHookCallBack() {

            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                String packagename = (String) param.args[0];
                LOG.debug("Slient UnInstall APK ->");
                LOG.debug("The APK PackageName = " + packagename);
            }
        });

        Method getInstalledPackagesMethod = RefInvoke.findMethodExact("android.app.ApplicationPackageManager",
                ClassLoader.getSystemClassLoader(), "getInstalledPackages", int.class, int.class);
        hookHelper.hookMethod(getInstalledPackagesMethod, new AbstractBehaviorHookCallBack() {
            @Override
            public void descParam(HookParam param) {
                // TODO Auto-generated method stub
                LOG.debug("Query Installed Apps ->");
            }
        });
    }

}
