package permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import zhangtianyagn.com.cn.permissionutils.BuildConfig;

/**
 * description:
 * <p>
 */
public class PermissionHelper {
    // 1. 传什么参数
    // 1.1. Object Fragment or Activity  1.2. int 请求码   1.3.需要请求的权限  string[]
    private Object mObject;
    //请求权限成功或失败执行对象
    private static Object executeObj = null;
    //参数
    private static HashMap<String,Object> params = new HashMap<>();
    private int mRequestCode;
    private String[] mRequestPermission;

    private PermissionHelper(Object object){

        //每次初始化的时候清空参数以及成功或者失败执行的对象
        params.clear();
        executeObj = null;

        this.mObject = object;
    }

    // 2.已什么的方式传参数
    // 2.1 直接传参数
    public static void  requestPermission(Activity activity, int requestCode, String[] permissions){
        PermissionHelper.with(activity).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    public static void  requestPermission(Fragment fragment, int requestCode, String[] permissions){
        PermissionHelper.with(fragment).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    // 2.2 链式的方式传
    // 传Activity
    public static PermissionHelper with(Activity activity){
        return new PermissionHelper(activity);
    }

    // 传Fragment
    public static PermissionHelper with(Fragment fragment){
        return new PermissionHelper(fragment);
    }

    /**
     * 设置权限申请成功的方法回调
     * @param mObject
     * @return
     */
    public PermissionHelper executeObj(Object mObject)
    {
        executeObj = mObject;
        return this;
    }


    // 添加一个请求码
    public PermissionHelper requestCode(int requestCode){
        this.mRequestCode = requestCode;
        return this;
    }

    // 添加请求的权限数组
    public PermissionHelper requestPermission(String... permissions){
        this.mRequestPermission = permissions;
        return this;
    }

    //添加请求的权限对象，只支持字符串和字符串数组
    public PermissionHelper requestPermission(Object mObject){

        if(mObject instanceof String)
        {
            this.mRequestPermission = new String[]{(String)mObject};
        }else if(mObject instanceof String[])
        {
            this.mRequestPermission = (String[])mObject;
        }else {

            throw new UnsupportedOperationException("请求权限只支持String 或者 String[] 类型");
        }
        return this;
    }


    //设置请求参数
    public PermissionHelper setParam(HashMap<String,Object> cparam)
    {
        params = cparam;
        return this;
    }
    /**
     * 3.1 真正判断和发起请求权限
     */
    public void request() {
        // 3.2 首先判断当前的版本是不是6.0 及以上
        if(!PermissionUtils.isOverMarshmallow()){
            // 3.3 如果不是6.0以上  那么直接执行方法   反射获取执行方法
            // 执行什么方法并不确定 那么我们只能采用注解的方式给方法打一个标记，
            // 然后通过反射去执行。  注解 + 反射  执行Activity里面的callPhone
            if(null == executeObj)
            {
                PermissionUtils.executeSucceedMethod(mObject,mRequestCode,params);
            }else {

                PermissionUtils.executeSucceedMethod(executeObj,mRequestCode,params);
            }
            return;
        }

        // 3.3 如果是6.0以上  那么首先需要判断权限是否授予
        // 需要申请的权限中 获取没有授予过得权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mObject,mRequestPermission);

        // 3.3.1 如果授予了 那么我们直接执行方法   反射获取执行方法
        if(deniedPermissions.size() == 0){
            // 全部都是授予过的
//          PermissionUtils.executeSucceedMethod(mObject,mRequestCode);
            if(null == executeObj)
            {
                PermissionUtils.executeSucceedMethod(mObject,mRequestCode,params);
            }else {

                PermissionUtils.executeSucceedMethod(executeObj,mRequestCode,params);
            }
        }else {

            if(mObject instanceof Fragment)
            {//Fragment请求权限

                ((Fragment)mObject).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]),mRequestCode);
//               requestPermission(((Fragment)mObject), mRequestCode,deniedPermissions.toArray(new String[deniedPermissions.size()]));
            }else {
                // 3.3.2 如果没有授予 那么我们就申请权限  申请权限
                ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject), deniedPermissions.toArray(new String[deniedPermissions.size()]), mRequestCode);
            }

        }
    }


    /**
     * 处理申请权限的回调
     * @param object Context
     * @param requestCode 请求码
     * @param permissions 权限申请列表
     */
    public static void requestPermissionsResult(Object object, int requestCode, String[] permissions) {
        // 再次获取没有授予的权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(object,permissions);
        if(deniedPermissions.size() == 0){
            // 权限用户都同意授予了
            PermissionUtils.executeSucceedMethod(object,requestCode,null);
        }else{
            // 你申请的权限中 有用户不同意的
            PermissionUtils.executeFailMethod(object,requestCode,null);
        }
    }

    /**
     * 一般用于Activity或者Fragment的回调
     * 处理申请权限的回调
     * @param context Context
     * @param requestCode 请求码
     * @param permissions 权限申请列表
     * @param reflectObj 请求成功或失败的对象
     */
    public static void requestPermissionsResult(Object context, int requestCode, String[] permissions, Object reflectObj) {

        // 再次获取没有授予的权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(context,permissions);

        if(deniedPermissions.size() == 0){
            // 权限用户都同意授予了
            PermissionUtils.executeSucceedMethod(reflectObj,requestCode,params);
        }else{
            // 你申请的权限中 有用户不同意的
            PermissionUtils.executeFailMethod(reflectObj,requestCode,params);
        }
    }

    /**
     * 一般用于Activity或者Fragment的回调
     * 处理申请权限的回调
     * @param context Context
     * @param requestCode 请求码
     * @param permissions 权限申请列表
     * @param cparam 请求参数
     * @param reflectObj 请求成功或失败的对象
     */
    public static void requestPermissionsResult(Object context, int requestCode, String[] permissions, HashMap<String,Object> cparam, Object reflectObj) {

        // 再次获取没有授予的权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(context,permissions);

        if(deniedPermissions.size() == 0){
            // 权限用户都同意授予了
            PermissionUtils.executeSucceedMethod(reflectObj,requestCode,cparam);
        }else{
            // 你申请的权限中 有用户不同意的
            PermissionUtils.executeFailMethod(reflectObj,requestCode,cparam);
        }
    }

    /***
     * 判断是否具有权限组
     * @param context
     * @param mObject
     * @return
     */
    public static boolean hasAppointAuthority(Context context, Object mObject)
    {

        boolean hasAuthority = true;
        PackageManager pm = context.getPackageManager();

        if(null == mObject)
        {
            return false;
        }

        if(mObject instanceof String)
        {
            String authority = (String) mObject;
            if (PackageManager.PERMISSION_GRANTED != pm.checkPermission(authority, context.getApplicationContext().getPackageName()))
            {
                return false;
            }else
            {
                return true;
            }
        }

        if(mObject instanceof String[])
        {
            String[] authoritys = (String[]) mObject;
            for(String authority: authoritys)
            {
                if (PackageManager.PERMISSION_GRANTED != pm.checkPermission(authority, context.getApplicationContext().getPackageName()))
                {
                    hasAuthority = false;
                    break;
                }
            }
        }

        return hasAuthority;
    }

    /**
     * 判断是否是MIUI
     */
    public static boolean isMIUI() {

        String device = Build.MANUFACTURER;

        if (device.equals("Xiaomi")) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    /**
     * 跳转到MIUI应用权限设置页面
     * @param context context
     */
    public static void jumpToPermissionsEditorActivity(Context context) {
        if (isMIUI()) {
            try {
                // MIUI 8
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e) {
                try {
                    // MIUI 5/6/7
                    Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    localIntent.putExtra("extra_pkgname", context.getPackageName());
                    context.startActivity(localIntent);
                } catch (Exception e1) {
                    // 否则跳转到应用详情
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            }
        }else if("HUAWEI".equals(getDeviceName())) {//华为

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        }else if("Sony".equals(getDeviceName())) {//索尼

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);

        }else if("OPPO".equals(getDeviceName())) {//OPPO

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        }else if("LG".equals(getDeviceName())) {//LG

            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        }else if("vivo".equals(getDeviceName())) {//vivo

        }else if("samsung".equals(getDeviceName())) {//samsung

        }else if("Letv".equals(getDeviceName())) {//Letv

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            context.startActivity(intent);
        }else if("ZTE".equals(getDeviceName())) {//ZTE

            getAppDetailSettingIntent(context);
        }else if("YuLong".equals(getDeviceName())) {//酷派

            getAppDetailSettingIntent(context);
        }else if("LENOVO".equals(getDeviceName())) {//联想

            getAppDetailSettingIntent(context);
        }else if("Meizu".equals(getDeviceName())){//魅族

            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            context.startActivity(intent);
        }else {
            getAppDetailSettingIntent(context);
        }
//        华为——Huawei
//        魅族——Meizu
//        小米——Xiaomi
//        索尼——Sony
//        oppo——OPPO
//        LG——LG
//        vivo——vivo
//        三星——samsung
//        乐视——Letv
//        中兴——ZTE
//        酷派——YuLong
//        联想——LENOVO
    }

    /**
     * 跳转到权限设置界面
     */
    public static void getAppDetailSettingIntent(Context context){

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 9){

            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if(Build.VERSION.SDK_INT <= 8){

            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }

        context.startActivity(intent);
    }

    public static String getDeviceName()
    {
        String device = Build.MANUFACTURER;
        return device;
    }


    /**
     * 有声音和视频
     * @param context
     * @return
     */
    public static boolean hasVoiceAndVideoPermission(Context context)
    {

        if (PackageManager.PERMISSION_GRANTED ==  ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) && PackageManager.PERMISSION_GRANTED ==  ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO))
        {
            return true;
        }else
        {
            return false;
        }
    }

    public static Object getExecuteObj() {
        return executeObj;
    }

    public static void setExecuteObj(Object executeObj) {
        PermissionHelper.executeObj = executeObj;
    }
}

