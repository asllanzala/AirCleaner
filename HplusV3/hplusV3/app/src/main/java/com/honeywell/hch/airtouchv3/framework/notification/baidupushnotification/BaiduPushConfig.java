package com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent on 23/10/15.
 */
public class BaiduPushConfig {

    //test environment
//    public static String NotificationHubName = "baidunh";
//    public static String NotificationHubConnectionString = "Endpoint=sb://baidunotification-ns.servicebus.chinacloudapi.cn/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=TKao5xPc7HoUZwci7iZTWQpdNt67fRq97drmCL33GpQ=";
    //product environment
    public static String NotificationHubName = "tccnotificationpro";
    public static String NotificationHubConnectionString = "Endpoint=sb://tccnotification-ns.servicebus.chinacloudapi.cn/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=wiHEzF8JtKUqqaECqE7pk+ZQ8SDjJ3ZKN7+98ZNPQf4=";
    private static BaiduPushConfig instance;

    private static final int TOUCHTYPE = 100;
    private static final int PREMIUMTYPE = 101;
    private static final int SENSOREOR = 001;
    private static final int REMOTEENABLE = 002;
    private static final int REMOTEDISABLE = 003;
    private static final int TVOCSENSOR = 004;

    public static final String BAIDUPUSHAPIKEY = "com.baidu.push.API_KEY";



    private BaiduPushConfig() {
    }

    public static BaiduPushConfig getInstance() {
        if (instance == null) {
            instance = new BaiduPushConfig();
        }
        return instance;
    }

    public boolean isAppOnForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(ctx.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, String> parseMessage(String message) {
        try {
            JSONObject object = new JSONObject(message);
            String alert = object.getString("alert");
            JSONObject alertObject = new JSONObject(alert);
            String result = alertObject.getString("loc-key");
            HashMap<String, String> map = new HashMap<String, String>();
            int deviceType = Integer.valueOf(result.substring(0, 3));
            int messageType = Integer.valueOf(result.substring(3));
            switch (deviceType) {
                case TOUCHTYPE:
                    map.put("deviceType", "Air Touch");
                    break;
                case PREMIUMTYPE:
                    map.put("deviceType", "Air Premium");
                    break;
            }
            switch (messageType) {
                case SENSOREOR:
                    map.put("messageType", "Sensor Error");
                    break;
                case REMOTEENABLE:
                    map.put("messageType", "Remote Enable");

                    break;
                case REMOTEDISABLE:
                    map.put("messageType", "Remote Disable");

                    break;
                case TVOCSENSOR:
                    map.put("messageType", "TVOC Sensor Error");
                    break;
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // get ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

}
