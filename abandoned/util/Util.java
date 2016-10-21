package com.mhb.xhook.xposed.util;

import android.os.Build;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import de.robv.android.xposed.XposedHelpers;

/**
 * main util
 * @author maohongbin01
 * @time 16/7/22 下午8:05
 */
public class Util {
    public static final String SELF_PACKAGE_NAME = "com.mhb.xhook";
    public static final String DEBUG_TAG = "XHook-java";

    public static final String NATIVE_LIB = "xhooknative";
    public static final String NATIVE_LIB_PATH = String.format("/data/data/%s/lib/lib%s.so",
            Util.SELF_PACKAGE_NAME, Util.NATIVE_LIB);

    public static final int FRAMEWORK_HOOK_SYSTEM_API = 0x00;
    public static final int FRAMEWORK_HOOK_APP_API = 0x01;

    // TODO:
    // public static final String TARGET_APP = "com.sankuai.meituan";
    public static final String TARGET_APP = "com.mhb.xhook_test";
    // public static final String TARGET_APP = "com.mhb.test";



    public static String toHex(byte[] buf) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String h = Integer.toHexString(0xFF & buf[i]);
            while (h.length() < 2)
                h = "0" + h;

            hexString.append(h);
        }
        return  hexString.toString();
    }

    // Hook android.os.Build's fields
    public static void hookBuildFields(){
        // Init the map
        Random randomGenerator = new Random();
        HashMap<String, String> fieldsMap = new HashMap<String, String>();
        String[] fieldNames = {"PRODUCT", "DEVICE", "BOARD", "MANUFACTURER", "BRAND",
                "MODEL", "HARDWARE", "TAGS", "HOST", "SERIAL"};
        for(String fieldName : fieldNames)
            fieldsMap.put(fieldName, Util.generateRandomStrs(randomGenerator.nextInt(5) + 5));

        Iterator<Map.Entry<String, String>> iter = fieldsMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
            XposedHelpers.setStaticObjectField(Build.class, entry.getKey(), entry.getValue());
        }

    }

    public static String generateRandomStrs(int strCount){
        StringBuilder randomStrs = new StringBuilder();
        Random randomGenerator = new Random();
        for (int i = 1; i < strCount; i++){
            int randomInt = randomGenerator.nextInt(26);
            randomStrs.append((char)(randomInt + 'a'));
        }

        return randomStrs.toString();
    }

}
