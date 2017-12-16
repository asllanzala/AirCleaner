package com.honeywell.hch.airtouchv2.lib.util;

import android.content.Context;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qian Jin on 9/10/15.
 */
public class UmengUtil {
    public enum EventType {
        ENROLL_START,
        ENROLL_END,
        ENROLL_SUCCESS,
        ENROLL_FAIL,
        ENROLL_CANCEL;

        @Override
        public String toString() {
            switch (this) {
                case ENROLL_START: return "start_enroll_event";
                case ENROLL_END: return "end_enroll_event";
                case ENROLL_SUCCESS: return "add_device_success";
                case ENROLL_FAIL: return "add_device_fail";
                case ENROLL_CANCEL: return "cancel_enroll_event";
            }
            return null;
        }
    }

    public static void onActivityCreate(Context context) {
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(context);
    }

    public static void onActivityResume(Context context, String tag) {
        MobclickAgent.onPageStart(tag);
        MobclickAgent.onResume(context);
    }

    public static void onActivityPause(Context context, String tag) {
        MobclickAgent.onPageEnd(tag);
        MobclickAgent.onPause(context);
    }

    public static void onEvent(Context context, String event) {
        Map<String, String> map = new HashMap<>();
        String macId = "";

        // get current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);

        // get phone's mac id
        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
            macId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
        }

        if (macId.equals("")) {
            map.put("userId", AuthorizeApp.shareInstance().getUserID() + "_" + time);
        } else {
            map.put("userId", AuthorizeApp.shareInstance().getUserID()
                + "_macId_" + macId + "_" + time);
        }

        MobclickAgent.onEvent(context, event, map);
    }

    public static void onEvent(Context context, String event, String msg) {
        Map<String, String> map = new HashMap<>();
        String macId = "";

        // get current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);

        // get phone's mac id
        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
            macId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
        }

        if (macId.equals("")) {
            map.put("userId", AuthorizeApp.shareInstance().getUserID() + "_" + msg + "_" + time);
        } else {
            map.put("userId", AuthorizeApp.shareInstance().getUserID()
                    + "_macId_" + macId + "_" + msg + "_" + time);
        }

        MobclickAgent.onEvent(context, event, map);
    }

}
