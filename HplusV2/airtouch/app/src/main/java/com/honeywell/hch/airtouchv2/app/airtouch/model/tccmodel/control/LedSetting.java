package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control;

/**
 * Created by Jin Qian on 2/12/2015.
 */
public class LedSetting {
    private int mSettingLed; // decide led rising and falling when onMove
    private int mLastSettingLed; // decide led rising and falling when onMove
    private int mSettingSpeed; // decide weather to speed control
    private int mLastSettingSpeed; // decide weather to speed control

    public LedSetting() {

    }

    public int getSettingLed() {
        return mSettingLed;
    }

    public void setSettingLed(int settingLed) {
        mSettingLed = settingLed;
    }

    public int getSettingSpeed() {
        return mSettingSpeed;
    }

    public void setSettingSpeed(int settingSpeed) {
        mSettingSpeed = settingSpeed;
    }

    public int getLastSettingSpeed() {
        return mLastSettingSpeed;
    }

    public void setLastSettingSpeed(int lastSettingSpeed) {
        mLastSettingSpeed = lastSettingSpeed;
    }

    public int getLastSettingLed() {
        return mLastSettingLed;
    }

    public void setLastSettingLed(int lastSettingLed) {
        mLastSettingLed = lastSettingLed;
    }

}
