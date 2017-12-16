package com.honeywell.hch.airtouchv3.framework.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;

/**
 * Created by Jin Qian on 3/12/2015.
 */
public class MorningAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(MainActivity.TIME_CHANGE_ACTION);
        newIntent.setAction(MainActivity.TIME_CHANGE_ACTION);
        context.sendBroadcast(newIntent);
    }
}
