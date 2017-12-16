package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.control;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupRequest;

import android.graphics.Color;

/**
 * Created by allanhwmac on 15/10/14.
 */
public class DeviceMode {

    public static final int MODE_UNDEFINE = -1;

    public static final boolean IS_REFLASHING = false;
    /**
     * HOME == AUTO == ON
     */
    public static final int MODE_HOME = ScenarioGroupRequest.SCENARIO_HOME_AUTO;

    /**
     * AWAY == OFF
     */
    public static final int MODE_AWAY = ScenarioGroupRequest.SCENARIO_AWAY_OFF;

    public static final int MODE_SLEEP = ScenarioGroupRequest.SCENARIO_SLEEP;

    public static final int MODE_ON_OFF = 3;

    public static final int ANGLE_DEVIDE_15 = 15;  // 07:00

    public static final int ANGLE_DEVIDE_180 = 180; // 18:00

    public static final int ANGLE_DEVIDE_255 = 255; // 23:00

    public static final int COLOR_HOME_NORMAL = Color.argb(76, 87, 134, 132); // 23:00 - 07:00

    public static final int COLOR_HOME_CLICKED = Color.argb(255, 87, 134, 132); // 23:00 - 07:00

    public static final int COLOR_SLEEP_NORMAL = Color.argb(76, 50, 81, 80); // 07:00 - 18:00

    public static final int COLOR_SLEEP_CLICKED = Color.argb(255, 50, 81, 80); // 07:00 - 18:00

    public static final int COLOR_AWAY_NORMAL = Color.argb(76, 106, 173, 185); // 18:00 - 23:00

    public static final int COLOR_AWAY_CLICKED = Color.argb(255, 106, 173, 185); // 18:00 - 23:00

    private int mModeType;

    private int mStartAngle;

    private int mEndAngle;

    private int mSweepAngle;

    private int mCircleColorNormal;

    private int mCircleColorClicked;

    /**
     * @param modeType * @param startAngle Starting angle (in degrees) where the arc begins
     * @param endAngle Sweep angle (in degrees) measured clockwise
     * @
     */
    public DeviceMode(int modeType, int startAngle, int endAngle, int colorNormal,
            int colorClicked) {
        mModeType = modeType;
        mStartAngle = startAngle;
        mEndAngle = endAngle;

        if (endAngle <= startAngle) {
            endAngle = endAngle + 360;
        }
        mSweepAngle = endAngle - startAngle;

        mCircleColorNormal = colorNormal;
        mCircleColorClicked = colorClicked;
    }

    public int getModeType() {
        return mModeType;
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public int getEndAngle() {
        return mEndAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public int getColorNormal() {
        return mCircleColorNormal;
    }

    public int getColorClicked() {
        return mCircleColorClicked;
    }

    public String getModeName() {
        return getModeName(mModeType);
    }

    public static String getModeName(int groupModeType) {
        switch (groupModeType) {
            case MODE_HOME:
                return HPlusApplication.getInstance()
                        .getString(R.string.group_control_device_mode_home);

            case MODE_AWAY:
                return HPlusApplication.getInstance()
                        .getString(R.string.group_control_device_mode_away);

            case MODE_SLEEP:
                return HPlusApplication.getInstance()
                        .getString(R.string.group_control_device_mode_sleep);

            default:
                return "Unknow";
        }
    }

    public static String getDeviceRunningStatusName(int groupModeType) {
        switch (groupModeType) {
            case MODE_HOME:
                return HPlusApplication.getInstance()
                        .getString(R.string.control_auto);

            case MODE_AWAY:
                return HPlusApplication.getInstance()
                        .getString(R.string.off);

            case MODE_SLEEP:
                return HPlusApplication.getInstance()
                        .getString(R.string.control_sleep);

            default:
                return "Unknow";
        }
    }

}
