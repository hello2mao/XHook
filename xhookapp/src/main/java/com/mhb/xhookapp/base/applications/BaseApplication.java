package com.mhb.xhookapp.base.applications;

import android.app.Application;

import com.mhb.xhook.dexposed.Dexposed;
import com.mhb.xhookapp.AppConfig;

/**
 * BaseApplication
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (AppConfig.ENABLE_DEXPOSED) {
            Dexposed.init(this);
        }
    }
}
