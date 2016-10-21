package com.mhb.xhook.xposed.apimonitor;

import com.mhb.xhook.AppConfig;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;

import org.apache.log4j.Logger;


public abstract class AbstractBehaviorHookCallBack extends MethodHookCallBack {

    private final Logger log = Logger.getLogger(AppConfig.CONF_TAG);

    @Override
    public void beforeHookedMethod(HookParam param) {
        log.debug("Invoke "+ param.method.getDeclaringClass().getName()+"->"+param.method.getName());
        this.descParam(param);
        //this.printStackInfo();
    }

    @Override
    public void afterHookedMethod(HookParam param) {
        //LOG.debug("End Invoke "+ param.method.toString());
    }

    private void printStackInfo(){
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if(stackElements != null){
            StackTraceElement st;
            for(int i=0; i<stackElements.length; i++){
                st = stackElements[i];
                if(st.getClassName().startsWith("com.android.reverse")||st.getClassName().startsWith("de.robv.android.xposed.XposedBridge"))
                    continue;
                log.debug(st.getClassName()+":"+st.getMethodName()+":"+st.getFileName()+":"+st.getLineNumber());
            }
        }
    }

    public abstract void descParam(HookParam param);

}
