package permission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 张天洋 on 2017/5/12.
 */

public class PermissionConstants {

    //拍照
    public static final int CAMERA = 1001;
    //写存储卡
    public static final int STORATE_WRITE = 1002;
    //群录制视频
    public static final int RECORD_VIDEO_CODE = 1003;
    //记录的类型 0表示群视频 1表示群拍照 2照明红包-图片  3表示进群视频认证 4个人资料的相册上传  5普通的视频认证 6照明红包-视频
    public static int AUTHENTIC_REQUEST_TYPE = 0;
    //录制音频自由说话模式
    public static final int RECORD_VOICE_CODE = 1004;
    //录制音频按住说话模式
    public static final int RECORD__PRESS_VOICE_CODE = 1005;
    public static final int RECORD__PRESS_VOICE_CODE2 = 10086;
    //获取位置的权限
    public static final int LOCATION_CODE = 1006;
    //连麦权限
    public static final int LINK_MIC_CODE = 10015;
    //录制视频的权限代码
    public static final int VIDEO_CODE = 1007;
    //音频录制权限
    public static final int RECORD_AUDIO = 1008;
    //平常需要的权限申请
    public static final int COMMOND_NEED_PERMISSION_CODE = 1007;
    //读取电话本
    public static final int READ_PHONE_STATE = 1009;
    //上麦
    public static final int RECORD_UP_MIC = 1011;
    //底部上麦
    public static final int BOTTOM_UP_MIC = 1012;

    public static final int WINDOW_ALERT_CODE = 1013;

    public static HashMap<Integer,Object> authoritys = new HashMap<>();

    static {
        authoritys.put(CAMERA,"android.permission.CAMERA");
        authoritys.put(RECORD_AUDIO,"android.permission.RECORD_AUDIO");
        authoritys.put(LINK_MIC_CODE,"android.permission.RECORD_AUDIO");
        authoritys.put(RECORD_UP_MIC,"android.permission.RECORD_AUDIO");
        authoritys.put(BOTTOM_UP_MIC,"android.permission.RECORD_AUDIO");
        authoritys.put(VIDEO_CODE,new String[]{"android.permission.CAMERA","android.permission.RECORD_AUDIO"});
        authoritys.put(LOCATION_CODE,"");
        /**声音权限**/
        authoritys.put(RECORD__PRESS_VOICE_CODE2,"android.permission.RECORD_AUDIO");
        authoritys.put(RECORD__PRESS_VOICE_CODE,"android.permission.RECORD_AUDIO");
        authoritys.put(RECORD_VOICE_CODE,"android.permission.RECORD_AUDIO");
        /***读取SD卡的权限***/
        authoritys.put(STORATE_WRITE,new String[]{"android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION"});
    }

    public static Object getAuthorityByCode(int requstCode)
    {
        Iterator iterator = authoritys.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer key = (Integer) entry.getKey();
            Object val = entry.getValue();
            if(requstCode == key)
            {
                return val;
            }
        }

        return null;
    }

    /*********************************权限列表***********************************/
//    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//    <uses-permission android:name="android.permission.MANAGE_ACCOUNTS" />
//    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
//    <uses-permission android:name="android.permission.MEDIA_CONTENT_CONTROL" />
//    <uses-permission android:name="android.permission.READ_CONTACTS" />
//    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
//    <uses-permission android:name="android.permission.BLUETOOTH" />
//    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.GET_TASKS" />
//    <uses-permission android:name="android.permission.READ_LOGS" />
//    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
//    <uses-permission android:name="android.permission.CALL_PHONE" />
//    <uses-feature android:name="android.hardware.camera.front" />
//    <uses-feature android:name="android.hardware.microphone" />
//    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
//    <uses-permission android:name="android.permission.CAMERA" />
//    <uses-permission android:name="android.permission.RECORD_AUDIO" />
//    <uses-permission android:name="android.Manifest.permission.DEVICE_POWER" />
//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//    <uses-permission android:name="android.permission.VIBRATE" />
//    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
//    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
//    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="com.huawei.pushagent.permission.RICHMEDIA_PROVIDER" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
//    <uses-permission android:name="android.permission.READ_LOGS" />
//    <uses-permission android:name="android.permission.VIBRATE" />
//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
//    <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
//    <uses-permission android:name="android.permission.READ_LOGS" />
//    <uses-permission android:name="android.permission.VIBRATE" />
//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    /*********************************end权限列表***********************************/
}
