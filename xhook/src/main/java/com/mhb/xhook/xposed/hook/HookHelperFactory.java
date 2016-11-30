package com.mhb.xhook.xposed.hook;

public class HookHelperFactory {

    private static HookHelperInterface hookHelper = null;

    public static HookHelperInterface getHookHelper(){
        if(hookHelper == null) {
            hookHelper = new XposeHookHelperImpl();
        }
        return hookHelper;
    }

}
