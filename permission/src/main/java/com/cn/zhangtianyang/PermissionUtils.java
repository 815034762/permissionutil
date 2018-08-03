package com.cn.zhangtianyang;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * description:
 * 处理权限请求的工具类
 * Version：1.0
 */
public class PermissionUtils {

    /**
     *  这个类里面所有的都是静态方法  所有不能让别人去new对象
     */
    private PermissionUtils(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断其是不是6.0以上的版本
     * Marshmallow 棉花糖  6.0
     * @return
     */
    public static boolean isOverMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 执行成功的方法
     */
    public static void executeSucceedMethod(Object reflectObject, int requestCode, HashMap<String,Object> params) {

        // 获取class中多有的方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();

        // 遍历找我们打了标记的方法
        for (Method method:methods){
            Log.e("TAG","<--方法名称--> " + method);
            // 获取该方法上面有没有打这个成功的标记
            PermissionSucceed succeedMethod =  method.getAnnotation(PermissionSucceed.class);
            if(succeedMethod != null){
                // 代表该方法打了标记
                // 并且我们的请求码必须 requestCode 一样
                int methodCode = succeedMethod.requestCode();
                if(methodCode == requestCode){
                    // 这个就是我们要找的成功方法
                    // 反射执行该方法
                    Log.e("TAG","找到了该方法 ："+method);
                    executeMethod(reflectObject,method,params);
                }
            }
        }
    }


    /**
     * 反射执行该方法
     */
    private static void executeMethod(Object reflectObject, Method method, HashMap<String,Object> params) {
        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是传参数
        try {
            // 允许执行私有方法
            method.setAccessible(true);
            //没参数执行
//            if(null == params || params.isEmpty())
//            {
//                method.invoke(reflectObject);
//            }else
//            {
                //new Object[]{}
            /**
             * 不论param为不为null都要附HashMap<>带参数
             */
            method.invoke(reflectObject,params);
//            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取没有授予的权限
     * @param object  Activity or Fragment
     * @return 没有授予过得权限
     */
    public static List<String> getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String requestPermission:requestPermissions){
            // 把没有授予过的权限加入到集合
            if(ContextCompat.checkSelfPermission(getActivity(object), requestPermission)
                    == PackageManager.PERMISSION_DENIED){

                deniedPermissions.add(requestPermission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取Context
     * @param object
     * @return
     */
    public static Activity getActivity(Object object) {
        if(object instanceof Activity){
            return (Activity)object;
        }
        if(object instanceof Fragment){
            return ((Fragment)object).getActivity();
        }
        return null;
    }


    /**
     * 执行失败的方法
     */
    public static void executeFailMethod(Object reflectObject, int requestCode, HashMap<String,Object> params) {
        // 获取class中多有的方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();

        // 遍历找我们打了标记的方法
        for (Method method:methods){
            Log.e("TAG",method+"");
            // 获取该方法上面有没有打这个失败的标记
            PermissionFail failMethod =  method.getAnnotation(PermissionFail.class);
            if(failMethod != null){
                // 代表该方法打了标记
                // 并且我们的请求码必须 requestCode 一样
                int methodCode = failMethod.requestCode();
                if(methodCode == requestCode){
                    // 这个就是我们要找的成功方法
                    // 反射执行该方法
                    Log.e("TAG","找到了失败的方法 ："+method);
                    //请求失败不执行
                    executeMethod(reflectObject,method,params);
                }
            }
        }
    }
}
