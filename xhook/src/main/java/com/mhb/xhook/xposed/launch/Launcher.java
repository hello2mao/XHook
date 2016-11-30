package com.mhb.xhook.xposed.launch;

import android.content.pm.ApplicationInfo;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.xposed.collecter.ModuleContext;
import com.mhb.xhook.xposed.util.XposedConfig;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * This class will be called by Xposed
 */
public class Launcher implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final BasicLog LOG = XHookLogManager.getInstance();
    private static final String XPOSED_INSTALLER = "de.robv.android.xposed.installer";


    /**
     * Called when zygote initialize
     */
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        HookTarget.hookWhenZygoteInit();
    }

    /**
     * Called when package loaded
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        XposedBridge.log("Loaded package: " + lpparam.packageName);
        ApplicationInfo appInfo = lpparam.appInfo;
        if (appInfo == null) {
            return;
        }
        // No system app
        // No updated system app
        if ((appInfo.flags
                & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) !=0) {
            return;
        }
        // No Xposed self
        // No app self
        if (appInfo.packageName.equals(XPOSED_INSTALLER) ||
                appInfo.packageName.equals(XposedConfig.getInstance().getSelfPackageName())) {
            return;
        }
        // make sure the main thread of target app
        if ((appInfo.packageName.equals(XposedConfig.getInstance().getHookTargetApp())) &&
                (lpparam.isFirstApplication)) {
            LOG.info("Hook " + XposedConfig.getInstance().getHookTargetApp() + " start!");
            LOG.info("The package = "+lpparam.packageName +" has hook");
            LOG.info("The app target id = "+android.os.Process.myPid());
            PackageMetaInfo pmInfo = PackageMetaInfo.fromXposed(lpparam);
            ModuleContext.getInstance().initModuleContext(pmInfo);
            HookTarget.hookWhenPackageLoaded();

        }
    }
}
