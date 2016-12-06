package com.mhb.xhook.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtils {

    /**
     * 通过反射来执行指定的方法（该方法可能是父类中的，也可能当前类私有的，但不能得到父类中私有的）
     *
     * @param obj
     *            传入要执行方法对应的对象
     * @param methodName
     *            要执行的方法名
     * @param args
     *            执行方法的参数
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object invoke(Object obj, String methodName, Object... args){
        // Class类型的数组存储参数对应的.class类型
        Class[] parametersType = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parametersType[i] = args[i].getClass();
        }

        Method method = getMethod(obj.getClass(), methodName, parametersType);

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            System.out.println("父类中的私有方法，你不可访问!!!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取指定的方法（可能在父类中） 从当前类找指定要的方法，若没有则循环向继承的父类查找，一旦找到便返回
     * 只有传入的当前类才可看见私有的，父类中的则不可以
     *
     * @param cla
     *            类
     * @param methodName
     *            方法名
     * @param parametersType
     *            参数类型
     * @return
     */
    public static Method getMethod(Class<?> cla, String methodName,
                                   Class<?>... parametersType) {
        boolean isFirstClass = true;
        for (; cla != Object.class; cla = cla.getSuperclass()) {
            Method method = null;
            try {
                method = cla.getDeclaredMethod(methodName, parametersType);
                if(isFirstClass){
                    //父类的私有方法是不能获取的
                    method.setAccessible(true);
                    isFirstClass = false;
                }
                return method;
            } catch (Exception e) {
                // 当没找到对应的方法时报的异常不做处理，让其进行下一次循环，查找继承的父类是否有该方法
            }finally{
                //当传进来的第一个类抛了异常，那么之后获取的都是父类，所有让前面的if条件不能执行
                isFirstClass = false;
            }
        }

        return null;
    }

    /**
     * 重载上面的invoke方法，通过传入的类名执行指定的方法
     *
     * @param className
     *            通过传入的类名
     * @param methodname
     *            方法名
     * @param args
     *            方法入口参数
     * @return
     */
    public static Object invoke(String className, String methodName,
                                Object... args) {

        Object obj = null;
        try {
            obj = Class.forName(className).newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(null == obj){
            return null;
        }

        return invoke(obj, methodName, args);
    }







    /**
     * 获取某对象中指定字段的值（该字段有可能是从父类中继承的）
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Class<?> cla = obj.getClass();
        Field field = getField(cla, fieldName);

        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("父类中的私有属性，你不可访问!!!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取指定字段 可能在父类,父类中的私有属性不可以被方法访问出来
     *
     * @param cla
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> cla, String fieldName) {
        boolean isFirstClass = true;
        for (; cla != Object.class; cla = cla.getSuperclass()) {
            Field field = null;
            try {
                field = cla.getDeclaredField(fieldName);
                if(isFirstClass){
                    //父类的私有属性是不能获取的
                    field.setAccessible(true);
                    isFirstClass = false;
                }
                return field;
            } catch (Exception e) {

            }finally{
                //当传进来的第一个类抛了异常，那么之后获取的都是父类，所有让前面的if条件不能执行
                isFirstClass = false;
            }
        }

        return null;
    }


    /**
     * 为某个对象设置指定的值
     *
     * @param obj
     * @param fieldName
     * @param val
     */
    public static void setFieldValue(Object obj ,String fieldName,Object val) {
        Field field = getField(obj.getClass(), fieldName);
        try {
            field.set(obj, val);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("无法设置父类中的私有属性!!!");
            e.printStackTrace();
        }
    }

    //重载方法setFieldValue
    @SuppressWarnings("unchecked")
    public static <T> T setFieldValue(String className,String fieldName,Object val){
        T obj = null;
        try {
            obj =  (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            System.out.println("类名错误！！！");
            e.printStackTrace();
        }
        setFieldValue(obj, fieldName, val);

        return obj;
    }





    /**
     * 获取泛型类的参数类型
     *
     * @param className 泛型类的Class类型
     * @param index 参数类型的索引，即泛型类中的泛型参数是第一几个
     * @return
     */
    @SuppressWarnings("unused")
    public static Class<?> getGenericSuperClass(Class<?> className, int index ){
        //Tyep 是个空接口
        Type type = className.getGenericSuperclass();
        if(!(type instanceof ParameterizedType)){
            return null;
        }

        //该接口ParameterizedType继承了Type 而该接口中的
        //getActualTypeArguments（）方法便可获得泛型中的实际参数类型
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] args = parameterizedType.getActualTypeArguments();

        if(index < 0 || index > args.length){
            return null;
        }

        if(null != args){
            Class<?> cla = (Class<?>) args[index];
            return cla;
        }

        return null;
    }
}
