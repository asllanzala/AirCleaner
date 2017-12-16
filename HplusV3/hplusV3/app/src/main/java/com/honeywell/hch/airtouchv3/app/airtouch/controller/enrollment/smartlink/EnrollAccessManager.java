package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.app.Activity;
import android.content.Intent;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollWelcomeActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.EnrollmentConstant;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

/**
 * Created by Vincent on 9/12/15.
 */
public class EnrollAccessManager {
    private final static int AIR_TOUCH_S = 1;

    public static void startIntent(Activity context, String activityName) {
        Intent i = new Intent();
        if (AppConfig.shareInstance().isIndiaAccount()) {
            SmartEnrollScanEntity.getEntityInstance().setData("", "", EnrollmentConstant.AIR_TOUCH_S_INDIA, new String[]{"0"}, "", false);
            i.setClass(context, EnrollWelcomeActivity.class);
        } else {
            i.putExtra(AirTouchConstants.SMART_ENROLL_ENRTRANCE, activityName);
            i.setClass(context, SmartLinkEnrollScanActivity.class);
        }
        context.startActivity(i);
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
