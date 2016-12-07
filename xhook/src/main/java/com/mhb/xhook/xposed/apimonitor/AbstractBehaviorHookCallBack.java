package com.mhb.xhook.xposed.apimonitor;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;
import com.mhb.xhook.xposed.hook.HookParam;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;


public abstract class AbstractBehaviorHookCallBack extends MethodHookCallBack {

    private static final BasicLog LOG = XhookLogManager.getInstance();

    @Override
    public void beforeHookedMethod(HookParam param) {
        LOG.debug("Invoke "+ param.method.getDeclaringClass().getName()+"->"+param.method.getName());
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
                LOG.debug(st.getClassName()+":"+st.getMethodName()+":"+st.getFileName()+":"+st.getLineNumber());
            }
        }
    }

    public abstract void descParam(HookParam param);

}
