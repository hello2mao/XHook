package com.mhb.xhook.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeviceCheck {

    private static final String SELECT_RUNTIME_PROPERTY = "persist.sys.dalvik.vm.lib";
    private static final String LIB_DALVIK = "libdvm.so";
    private static final String LIB_ART = "libart.so";
    private static final String LIB_ART_D = "libartd.so";
    private static final BasicLog LOG = XhookLogManager.getInstance();


    private static boolean isCheckedDeviceSupport = false;
    private static boolean isDeviceSupportable = false;

    public static boolean isDalvikMode() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return true;
        }
        String vmMode = getCurrentRuntimeValue();
        if ("Dalvik".equals(vmMode)){
            return true;
        }        
        return false;
    }
    
    private static String getCurrentRuntimeValue() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            try {
                Method get = systemProperties.getMethod("get", String.class, String.class);
                if (get == null) {
                    return "WTF?!";
                }
                try {
                    // FIXME: has some problem
                    final String value = (String) get.invoke(systemProperties,
                            SELECT_RUNTIME_PROPERTY,"");
                    if (LIB_DALVIK.equals(value)) {
                        return "Dalvik";
                    } else if (LIB_ART.equals(value)) {
                        return "ART";
                    } else if (LIB_ART_D.equals(value)) {
                        return "ART debug build";
                    }

                    return value;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    // TODO:
    private static boolean isSupportSDKVersion() {
        if (android.os.Build.VERSION.SDK_INT >= 14 && android.os.Build.VERSION.SDK_INT < 20) {
            return true;
        } else if(android.os.Build.VERSION.SDK_INT == 10 || android.os.Build.VERSION.SDK_INT == 9){
            return true;
        }
        return false;
    }

    private static boolean isX86CPU() {
        Process process = null;
        String abi = null;
        InputStreamReader ir = null;
        BufferedReader input = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.product.cpu.abi");
            ir = new InputStreamReader(process.getInputStream());
            input = new BufferedReader(ir);
            abi = input.readLine();
            if (abi.contains("x86")) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
            if (ir != null) {
                try {
                    ir.close();
                } catch (Exception e) {
                }
            }
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                }
            }
        }

        return false;
    }

    public static synchronized boolean isDeviceSupport(Context context) {
        // return memory checked value.
        try {
            if (isCheckedDeviceSupport) {
                return isDeviceSupportable;
            }

            if (!isX86CPU() && !isYunOS()) {
                isDeviceSupportable = true;
            } else {
                isDeviceSupportable = false;
            }
        } finally {
            Log.d("hotpatch", "device support is " + isDeviceSupportable
                    + ", checked: " + isCheckedDeviceSupport);
            isCheckedDeviceSupport = true;
        }
        return isDeviceSupportable;
    }

    @SuppressLint("DefaultLocale")
    private static boolean isYunOS() {
        String s1 = null;
        String s2 = null;
        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            s1 = (String) m.invoke(null, "ro.yunos.version");
            s2 = (String) m.invoke(null, "java.vm.name");
        } catch (NoSuchMethodException a) {
        } catch (ClassNotFoundException b) {
        } catch (IllegalAccessException c) {
        } catch (InvocationTargetException d) {
        }
        if ((s2 != null && s2.toLowerCase().contains("lemur"))
                || (s1 != null && s1.trim().length() > 0)) {
            return true;
        } else {
            return false;
        }
    }
}
