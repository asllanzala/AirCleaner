package com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Vincent on 28/10/15.
 */

public class BaiduPushMessageReceiver extends PushMessageReceiver {

    public static final String TAG = BaiduPushMessageReceiver.class
            .getSimpleName();
    public static NotificationHub hub = null;
    public static String mChannelId, mUserId;
    public static Notification notification = new Notification();
    public static NotificationManager manager;
    public static final int NOTIFICATIONFLAG = 0;
    private static Context mContext = null;
    private String mTagUserId;


    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);
        mChannelId = channelId;
        mUserId = userId;
        manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mTagUserId = AppManager.shareInstance().getAuthorizeApp().getMobilePhone();
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (errorCode == 0) {
            try {
                if (hub == null) {
                    hub = new NotificationHub(
                            BaiduPushConfig.NotificationHubName,
                            BaiduPushConfig.NotificationHubConnectionString,
                            context);
                    Log.i(TAG, "Notification hub initialized");
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            registerWithNotificationHubs(mTagUserId);
        }

    }

    private void registerWithNotificationHubs(final String tagUserId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (hub != null){
                        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "Registered baidu 2222===== - '"
                               + mTagUserId);
                        hub.registerBaidu(mUserId, mChannelId, tagUserId);
                    }
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "Registered with Notification Hub - '"
                            + BaiduPushConfig.NotificationHubName + "'"
                            + " with UserId - '"
                            + mUserId + "' and Channel Id - '"
                            + mChannelId + "userName: " + mTagUserId);

                } catch (Exception e) {
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, e.getMessage());
                }
            }
        }).start();

    }

    /**
     * get message method
     */
    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, messageString);
        boolean flag = BaiduPushConfig.getInstance().isApplicationBroughtToBackground(mContext);
        HashMap<String, String> map = BaiduPushConfig.getInstance().parseMessage(message);
//        if (flag == true) {
//            Intent intent = new Intent(mContext, BaiduPushRemindActivity.class);
//            intent.putExtra("touchuan_msg", map);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//        } else {
//            setupNotifi(mContext, map);
//        }
    }

    /**
     * self notification
     */
//    public void setupNotifi(Context context, Map map) {
//        notification.defaults = Notification.DEFAULT_SOUND;
//        notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
//        notification.icon = R.drawable.ic_launcher;
//        notification.when = System.currentTimeMillis();
//        notification.flags = Notification.FLAG_NO_CLEAR;// can't clear auto
//
//        RemoteViews rv = new RemoteViews(context.getPackageName(),
//                R.layout.bpush_lapp_notification_layout);
//        rv.setTextViewText(R.id.bpush_lapp_notification_title_textview, context.getString(R.string.app_name));
//        rv.setImageViewResource(R.id.bpush_lapp_notification_big_icon_imageview, R.drawable.ic_launcher);
//        rv.setTextViewText(R.id.bpush_lapp_notification_content_textview, map.get("deviceType") + " " + map.get("messageType"));
//        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
//        String time = sdf1.format(new Date());
//        rv.setTextViewText(R.id.bpush_lapp_notification_time_textview, time);
//
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.contentView = rv;
//        Intent intent = new Intent(context, BaiduPushRemindActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
//                intent, 0);
//        notification.contentIntent = contentIntent;
//        manager.notify(NOTIFICATIONFLAG, notification);
//    }

    /**
     * get notifiation method
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "notificationclicked title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, notifyString);
        Intent intent = new Intent(context, BaiduPushRemindActivity.class);
        intent.putExtra("tongzhi_msg", description);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * notification arrived
     */

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, notifyString);

    }

    /**
     * setTags() 。
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, responseString);
    }

    /**
     * delTags() 。
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, responseString);
    }

    /**
     * listTags() 。
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, responseString);

    }

    /**
     * PushManager.stopWork() 。
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, responseString);
        registerWithNotificationHubs("");

    }


}
