package com.mhb.xhook.dexposed;

import android.content.Context;

import com.taobao.android.dexposed.DexposedBridge;

public class Dexposed {

    public static void init(Context context) {
        // Check whether current device is supported (also initialize Dexposed framework if not yet)
        if (DexposedBridge.canDexposed(context)) {
            // Use Dexposed to kick off AOP stuffs.

        }
    }
}
