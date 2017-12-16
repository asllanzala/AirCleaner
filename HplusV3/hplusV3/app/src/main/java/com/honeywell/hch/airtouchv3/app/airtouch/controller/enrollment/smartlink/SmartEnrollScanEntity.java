package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import com.honeywell.hch.airtouchv3.R;

import java.util.Arrays;

/**
 * Created by Vincent on 24/11/15.
 */
public class SmartEnrollScanEntity {


    private static final String AIR_TOUCH_S_MODEL = "PAC35M2101S";

    private static final String AIR_TOUCH_X_1G_MODEL = "KJ700G-PAC2127W";

    private static final String AIR_TOUCH_X_2G_MODEL = "KJ700F-PAC2127W";

    private static final String AIR_TOUCH_S_INDIA = "HAC35M2101S";

    private static final String AIR_TOUCH_P_WIFI = "KJ450F-PAC2022S";

    private static final String AIR_TOUCH_S_JD = "KSN95Y";

    private static final String AIR_TOUCH_P_JD = "KHN6YM";
    

    private static SmartEnrollScanEntity mSmartEnrollScanEntity;

    private String mProductUUID;

    private String mMacID;

    private String mModel = "KJ700G-PAC2127W";

    private String[] mEnrollType;

    private String mCountry;

    private boolean isFromTimeout = false;

    private boolean isRegisteredByThisUser = false;

    private String smartEntrance;

    public String getSmartEntrance() {return smartEntrance;}

    public void setSmartEntranch(String smartEntranch) {
        this.smartEntrance = smartEntranch;
    }

    public String getmProductUUID() {
        return mProductUUID;
    }

    public void setmProductUUID(String mProductUUID) {
        this.mProductUUID = mProductUUID;
    }

    public String getmMacID() {
        return mMacID;
    }

    public void setmMacID(String mMacID) {
        this.mMacID = mMacID;
    }

    public String getmModel() {
        return mModel;
    }

    public void setmModel(String mModel) {
        this.mModel = mModel;
    }

    public String[] getmEnrollType() {
        return mEnrollType;
    }

    public void setmEnrollType(String[] mEnrollType) {
        this.mEnrollType = mEnrollType;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public boolean isFromTimeout() {
        return isFromTimeout;
    }

    public void setFromTimeout(boolean isTimeOut) {
        this.isFromTimeout = isTimeOut;
    }


    public void setData(String mProductUUID, String mMacID, String mModel, String[] mEnrollType, String mCountry, boolean isFromTimeout) {
        this.mProductUUID = mProductUUID;
        this.mMacID = mMacID;
        this.mModel = mModel;
        this.mEnrollType = mEnrollType;
        this.mCountry = mCountry;
        this.isFromTimeout = isFromTimeout;
    }
    public void clearData() {
        this.mProductUUID = "";
        this.mMacID = "";
        this.mModel = "";
        this.mEnrollType = null;
        this.mCountry = "";
        this.isFromTimeout = false;
    }

    public static SmartEnrollScanEntity getEntityInstance() {
        if (mSmartEnrollScanEntity == null) {
            mSmartEnrollScanEntity = new SmartEnrollScanEntity();
        }
        return mSmartEnrollScanEntity;
    }

    public boolean isRegisteredByThisUser() {
        return isRegisteredByThisUser;
    }

    public void setIsRegisteredByThisUser(boolean isRegisteredByThisUser) {
        this.isRegisteredByThisUser = isRegisteredByThisUser;
    }

    @Override
    public String toString() {
        return "SmartEnrollScanEntity{" +
                "mProductUUID='" + mProductUUID + '\'' +
                ", mMacID='" + mMacID + '\'' +
                ", mModel='" + mModel + '\'' +
                ", mEnrollType=" + Arrays.toString(mEnrollType) +
                ", mCountry='" + mCountry + '\'' +
                ", isFromTimeout=" + isFromTimeout +
                '}';
    }

    public int getDeviceImage() {
        if (AIR_TOUCH_S_MODEL.equals(mModel)) {
            return R.drawable.machine_black;
        }
        if (AIR_TOUCH_X_1G_MODEL.equals(mModel)) {
            return R.drawable.air_touch_x;
        }
        if (AIR_TOUCH_X_2G_MODEL.equals(mModel)) {
            return R.drawable.air_touch_x;
        }
        if (AIR_TOUCH_S_INDIA.equals(mModel)) {
            return R.drawable.machine_black;
        }
        if (AIR_TOUCH_P_WIFI.equals(mModel)) {
            return R.drawable.p_wifi_device;
        }
        if (AIR_TOUCH_S_JD.equals(mModel)) {
            return R.drawable.airtouchs_forjd;
        }
        if (AIR_TOUCH_P_JD.equals(mModel)) {
            return R.drawable.airtouchs_forjd;
        }
        return R.drawable.machine_black;
    }

    public int getDeviceName() {
        if (AIR_TOUCH_S_MODEL.equals(mModel) || AIR_TOUCH_S_INDIA.equals(mModel) || AIR_TOUCH_S_JD.equals(mModel)) {
            return R.string.airtouch_s_str;
        }
        if (AIR_TOUCH_X_1G_MODEL.equals(mModel) || AIR_TOUCH_X_2G_MODEL.equals(mModel)) {
            return R.string.airtouch_x_str;
        }
        if (AIR_TOUCH_P_JD.equals(mModel) || AIR_TOUCH_P_WIFI.equals(mModel)) {
            return R.string.airtouch_p_str;
        }
        return R.string.airtouch_s_str;
    }

}
