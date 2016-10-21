package com.mhb.xhook.hookclass.abandoned;

import android.os.Binder;

import com.mhb.xhook.xposed.hook.MethodHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class InstrumentationHook extends MethodHook {

    private Methods mMethod = null;

    private static final String mClassName = "android.app.Instrumentation";

    private InstrumentationHook(Methods method) {
        super( mClassName, method.name());
        mMethod = method;
    }



    // @formatter:off

    // public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,Intent intent, int requestCode, Bundle options, UserHandle user)
    // public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,Intent intent, int requestCode, Bundle options)
    // frameworks/base/core/java/android/app/Instrumentation.java

    // @formatter:on

    private enum Methods {
        execStartActivity
    }

    public static List<MethodHook> getMethodHookList() {
        List<MethodHook> methodHookList = new ArrayList<MethodHook>();
        methodHookList.add(new InstrumentationHook(Methods.execStartActivity));

        return methodHookList;
    }

    @Override
    public void before(MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    public void after(MethodHookParam param) throws Throwable {
        int uid = Binder.getCallingUid();
        String argNames = null;

        if (mMethod == Methods.execStartActivity) {
            if (param.args.length == 8) {
                argNames = "who|contextThread|token|target|intent|requestCode|options|user";
            } else if (param.args.length == 7) {
                argNames = "who|contextThread|token|target|intent|requestCode|options";
            }
        }

//      methodLog(uid, param, argNames);
    }
}
