package com.honeywell.hch.airtouchv2.framework.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.honeywell.hch.airtouchv2.app.airtouch.controller.location.MainActivity;

/**
 * Created by Jin Qian on 3/12/2015.
 */
public class NightAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(MainActivity.TIME_CHANGE_ACTION);
        newIntent.setAction(MainActivity.TIME_CHANGE_ACTION);
        context.sendBroadcast(newIntent);
    }
}
