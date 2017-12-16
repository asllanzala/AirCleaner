package com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.baidu.android.pushservice.PushServiceReceiver;
import com.baidu.android.pushservice.message.PublicMsg;
import com.baidu.android.pushservice.util.i;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Vincent on 28/10/15.
 * intercept notification
 */
public class BaiduPushServiceReceiver extends PushServiceReceiver {
    private static final String TAG = "BaiduPushService";
    public static Notification notification = new Notification();
    //    public static NotificationManager manager;
    public static final int NOTIFICATIONFLAG = 0;
    private static Context mContext = null;
    private static final String NOTIFICATIONMSG = "tongzhi_msg";

    private final ReentrantLock lock = new ReentrantLock();

    public BaiduPushServiceReceiver() {
    }

    public void onReceive(Context var1, Intent var2) {
        String var3 = var2.getAction();
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "action: " + var3);
        if (mContext == null) {

            mContext = var1.getApplicationContext();
        }
        if (!"android.intent.action.BOOT_COMPLETED".equals(var3) && !"android.net.conn.CONNECTIVITY_CHANGE".equals(var3) && !"android.intent.action.USER_PRESENT".equals(var3) && !"android.intent.action.MEDIA_CHECKING".equals(var3) && !"android.intent.action.ACTION_POWER_CONNECTED".equals(var3) && !"android.intent.action.ACTION_POWER_DISCONNECTED".equals(var3) && !"android.bluetooth.adapter.action.STATE_CHANGED".equals(var3)) {
            String var4;
            String var5;
            String var8;
            if ("com.baidu.android.pushservice.action.notification.SHOW".equals(var3)) {
                var4 = var2.getStringExtra("pushService_package_name");
                var5 = var2.getStringExtra("service_name");
                Parcelable var6 = var2.getParcelableExtra("public_msg");
                PublicMsg var7 = null;
                if (var6 != null && var6 instanceof PublicMsg) {
                    var7 = (PublicMsg) var6;
                }
                if (TextUtils.isEmpty(var4) || TextUtils.isEmpty(var5) || var7 == null) {
                    com.baidu.frontia.a.b.a.a.c("PushServiceReceiver", "Extra not valid, servicePkgName=" + var4 + " serviceName=" + var5 + " pMsg==null - " + (var7 == null));
                    return;
                }
                var8 = var2.getStringExtra("notify_type");
                String var9;
                if ("private".equals(var8)) {
                    var9 = var2.getStringExtra("message_id");
                    String var10 = var2.getStringExtra("app_id");
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var1: " + var1);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var4: " + var4);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var5: " + var5);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var7: " + var7);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var9: " + var9);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "var10: " + var10);
                    boolean flag = BaiduPushConfig.getInstance().isApplicationBroughtToBackground(var1);
                    if (flag == true) {
                        Intent intent = new Intent(mContext, BaiduPushRemindActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "description: " + var7.mDescription);
                        intent.putExtra(NOTIFICATIONMSG, var7.mDescription);
                        mContext.startActivity(intent);
                    } else {
                        showPrivateNotification(var1, var4, var5, var7, var9, var10);
                    }
                }
            }
        }

    }

    //showNotification when notification style come
    private static void showPrivateNotification(Context var0, String var1, String var2, PublicMsg var3, String var4, String var5) {
        NotificationManager var6 = (NotificationManager) var0.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent var7 = new Intent();
        var7.setClassName(var1, var2);
        var7.setAction("com.baidu.android.pushservice.action.privatenotification.CLICK");
        var7.setData(Uri.parse("content://" + var4));
        var7.putExtra("public_msg", var3);
        var7.putExtra("app_id", var5);
        var7.putExtra("msg_id", var4);
        PendingIntent var8 = PendingIntent.getService(var0, 0, var7, 0);
        Intent var9 = new Intent();
        var9.setClassName(var1, var2);
        var9.setAction("com.baidu.android.pushservice.action.privatenotification.DELETE");
        var9.setData(Uri.parse("content://" + var4));
        var9.putExtra("public_msg", var3);
        var9.putExtra("app_id", var5);
        var9.putExtra("msg_id", var4);
        PendingIntent var10 = PendingIntent.getService(var0, 0, var9, 0);
        Notification var11 = null;
        boolean var12 = i.p(var0, var3.mPkgName);
        if (var3.mNotificationBuilder == 0) {
            var11 = com.baidu.android.pushservice.g.a(var0, var3.mNotificationBuilder, var3.mNotificationBasicStyle, var3.mTitle, var3.mDescription, var12);
        } else {
            var11 = com.baidu.android.pushservice.g.a(var0, var3.mNotificationBuilder, var3.mTitle, var3.mDescription, var12);
        }

        var11.contentIntent = var8;
        var11.deleteIntent = var10;
//        var6.notify(var4, 0, var11);
        setupNotifi(var0, var3.mTitle, var3.mDescription, var6);
        sendNotificationArrivedReceiver(var0, var1, var3);
    }

    private static void setupNotifi(Context context, String title, String description, NotificationManager manager) {
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
        notification.icon = R.drawable.ic_launcher;
        notification.when = System.currentTimeMillis();
        notification.flags = Notification.FLAG_NO_CLEAR;// can't clear auto

        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.bpush_lapp_notification_layout);
        rv.setTextViewText(R.id.bpush_lapp_notification_title_textview, title);
        rv.setImageViewResource(R.id.bpush_lapp_notification_big_icon_imageview, R.drawable.ic_launcher);
        rv.setTextViewText(R.id.bpush_lapp_notification_content_textview, description);
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        String time = sdf1.format(new Date());
        rv.setTextViewText(R.id.bpush_lapp_notification_time_textview, time);

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentView = rv;
        Intent intent = new Intent(context, BaiduPushRemindActivity.class);
        intent.putExtra("tongzhi_msg", description);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
                intent, 0);
        notification.contentIntent = contentIntent;
        manager.notify(NOTIFICATIONFLAG, notification);
    }

    //send notification receiver
    private static void sendNotificationArrivedReceiver(Context var0, String var1, PublicMsg var2) {
        Intent var3 = new Intent();
        var3.setPackage(var1);
        var3.putExtra("method", "com.baidu.android.pushservice.action.notification.ARRIVED");
        var3.putExtra("notification_title", var2.mTitle);
        var3.putExtra("notification_content", var2.mCustomContent);
        var3.putExtra("extra_extra_custom_content", var2.mDescription);
        i.a(var0, var3, "com.baidu.android.pushservice.action.RECEIVE", var2.mPkgName);
    }

}
