package com.mhb.xhook.xposed.collecter;


import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import com.mhb.xhook.xposed.hook.HookHelperFactory;
import com.mhb.xhook.xposed.hook.HookHelperInterface;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;
import com.mhb.xhook.xposed.launch.CommandBroadcastReceiver;
import com.mhb.xhook.xposed.launch.PackageMetaInfo;
import com.mhb.xhook.xposed.util.Utility;
import java.lang.reflect.Method;

public class ModuleContext {

    private PackageMetaInfo metaInfo;
    private int apiLevel;
    private boolean HAS_REGISTER_LISENER = false;
    private Application fristApplication;
    private static ModuleContext moduleContext;
    private HookHelperInterface hookHelper = HookHelperFactory.getHookHelper();


    private ModuleContext() {
        this.apiLevel = Utility.getApiLevel();
    }

    public static ModuleContext getInstance() {
        if (moduleContext == null) {
            moduleContext = new ModuleContext();
        }
        return moduleContext;
    }

    public void initModuleContext(PackageMetaInfo info) {
        this.metaInfo = info;
        String appClassName = this.getAppInfo().className;
        if (appClassName == null) {
            Method hookOncreateMethod = null;
            try {
                hookOncreateMethod = Application.class.getDeclaredMethod("onCreate");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            hookHelper.hookMethod(hookOncreateMethod, new ApplicationOnCreateHook());
        } else {
            Class<?> hookApplicationClass;
            try {
                hookApplicationClass = this.getBaseClassLoader().loadClass(appClassName);
                if (hookApplicationClass != null) {
                    Method hookOncreateMethod = hookApplicationClass.getDeclaredMethod("onCreate");
                    if (hookOncreateMethod != null) {
                        hookHelper.hookMethod(hookOncreateMethod, new ApplicationOnCreateHook());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Method hookOncreateMethod;
                try {
                    hookOncreateMethod = Application.class.getDeclaredMethod("onCreate");
                    if (hookOncreateMethod != null) {
                        hookHelper.hookMethod(hookOncreateMethod, new ApplicationOnCreateHook());
                    }
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public String getPackageName() {
        return metaInfo.getPackageName();
    }

    public String getProcssName() {
        return metaInfo.getProcessName();
    }

    public ApplicationInfo getAppInfo() {
        return metaInfo.getAppInfo();
    }

    public Application getAppContext() {
        return this.fristApplication;
    }

    public int getApiLevel() {
        return this.apiLevel;
    }

    public String getLibPath(){
        return this.metaInfo.getAppInfo().nativeLibraryDir;
    }

    public ClassLoader getBaseClassLoader(){
        return this.metaInfo.getClassLoader();
    }

    private class ApplicationOnCreateHook extends MethodHookCallBack {

        @Override
        public void beforeHookedMethod(HookParam param) {

        }

        @Override
        public void afterHookedMethod(HookParam param) {
            if (!HAS_REGISTER_LISENER) {
                fristApplication = (Application) param.thisObject;
                IntentFilter filter = new IntentFilter(CommandBroadcastReceiver.INTENT_ACTION);
                fristApplication.registerReceiver(new CommandBroadcastReceiver(), filter);
                HAS_REGISTER_LISENER = true;
            }
        }
    }

}
