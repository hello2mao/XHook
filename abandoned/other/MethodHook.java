package com.mhb.xhook.xposed.hook;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.xposed.util.MethodParser;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;


/**
 * Abstract base class of hooked method
 */
public abstract class MethodHook {

    private String className;
    private String methodName;
    private static final BasicLog LOG = XHookLogManager.getInstance();

    protected MethodHook(String className, String methodName){
        this.className = className;
        this.methodName = methodName;
    }

    public abstract void before(MethodHookParam param) throws Throwable;

    public abstract void after(MethodHookParam param) throws Throwable;

    public String getClassName(){
        return className;
    }

    public String getMethodName(){
        return methodName;
    }

    /**
     * For Debug
     */
    protected void methodLogLong(MethodHookParam param, String argNames, String pos) {
        String[] argNamesArray = null;
        if (argNames != null)
            argNamesArray = argNames.split("\\|");
        String formattedArgs = MethodParser.parseMethodArgs(param, argNamesArray);
        if (formattedArgs == null)
            formattedArgs = "";
        String returnValue = MethodParser.parseReturnValue(param);
        /**
         * logMsg in Json as below
         * {
         *     "<pos>":{
         *         "<ClassName>-><MethodName>":{
         *             formattedArgs
         *         },
         *         "return":{
         *             returnValue
         *         }
         *     }
         * }
         */
        LOG.debug("{\"" + pos + "\":{\"" + className + "->" + methodName + "\":{" + formattedArgs
                + "},\"return\":{" + returnValue + "}}}");
    }

    /**
     * For Debug
     */
    protected void methodLogShort(MethodHookParam param, String argNames, String pos) {
        String[] argNamesArray = null;
        if (argNames != null)
            argNamesArray = argNames.split("\\|");
        String formattedArgs = MethodParser.parseMethodArgs(param, argNamesArray);
        if (formattedArgs == null)
            formattedArgs = "";
        String returnValue = MethodParser.parseReturnValue(param);
        /**
         * logMsg in Json as below
         * {
         *     "<pos>":{
         *         "<ClassName>-><MethodName>":{
         *         },
         *         "return":{
         *             returnValue
         *         }
         *     }
         * }
         */
        LOG.debug("{\"" + pos + "\":{\"" + className + "->" + methodName + "\":{ignore},\"return\":{"
                + returnValue + "}}}");
    }
}
