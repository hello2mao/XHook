package com.mhb.xhook.xposed.util;



import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;
import com.mhb.xhook.xposed.hook.MethodHookCallBack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


public class RefInvoke {

    private static final BasicLog LOG = XHookLogManager.getInstance();

    public static Method findMethodExact(String className,
                                         ClassLoader classLoader,
                                         String methodName,
                                         Class<?>... parameterTypes) {
        try {
//            Class clazz = classLoader.loadClass(className);
            // Find hook class
            Class<?> clazz = null;
            try {
                clazz = XposedHelpers.findClass(className, classLoader);
            } catch (XposedHelpers.ClassNotFoundError e) {
                e.printStackTrace();
            }
            if (clazz == null) {
                LOG.error("Hook-Class not found: " + className);
                return null;
            }
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void hookMethodByName(String className,
                                          ClassLoader classLoader,
                                          String methodName,
                                          MethodHookCallBack callback) {
        // Find hook class
        Class<?> hookClass = null;
        try {
            hookClass = XposedHelpers.findClass(className, classLoader);
        } catch (XposedHelpers.ClassNotFoundError e) {
            e.printStackTrace();
        }
        if (hookClass == null) {
            LOG.error("Hook-Class not found: " + className);
            return;
        }

        // Add hook
        // Only methods and constructors can be hooked
        // Cannot hook interfaces
        // Cannot hook abstract methods
        if (methodName.equals(hookClass.getSimpleName())) {
            for (Constructor<?> constructor : hookClass.getDeclaredConstructors()) {
                LOG.debug("hook constructor: " + constructor.toString());
                XposedBridge.hookMethod(constructor, callback);
            }
        } else {
            for (Method method : hookClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    if (Modifier.isAbstract(method.getModifiers())) {
                        LOG.error("can not hook abstract method: " + method.toString());
                    } else {
                        LOG.debug("hook method: " + method.toString());
                        XposedBridge.hookMethod(method, callback);
                    }
                }
            }
        }
    }


    public static  Object invokeStaticMethod(String class_name,
                                             String method_name,
                                             Class[] pareTyple,
                                             Object[] pareVaules){
        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        }  catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeMethod(String class_name,
                                       String method_name,
                                       Object obj ,
                                       Class[] pareTyple,
                                       Object[] pareVaules){
        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        }  catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static  Object invokeDeclaredMethod(String class_name,
                                               String method_name,
                                               Object obj ,
                                               Class[] pareTyple,
                                               Object[] pareVaules){
        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        }  catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int getFieldInt(String class_name,Object obj, String filedName){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.getInt(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;

    }


    public static Object getFieldOjbect(String class_name,Object obj, String filedName){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Object getStaticFieldOjbect(String class_name, String filedName){

        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(null);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void setFieldOjbect(String classname, String filedName, Object obj, Object filedVaule){
        try {
            Class obj_class = Class.forName(classname);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setFieldInt(String className, String fieldName, Object obj, int value) {
        try {
            Class obj_class = Class.forName(className);
            Field field = obj_class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(obj, value);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setStaticOjbect(String class_name, String filedName, Object filedVaule){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(null, filedVaule);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
