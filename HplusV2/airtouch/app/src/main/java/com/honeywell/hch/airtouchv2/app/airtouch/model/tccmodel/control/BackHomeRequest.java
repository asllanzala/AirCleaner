package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 3/13/2015.
 */
public class BackHomeRequest implements IRequestParams, Serializable {
    @SerializedName("enableCleanBeforeHome")
    private Boolean isEnableCleanBeforeHome;

    @SerializedName("timeToHome")
    private String mTimeToHome;

    @SerializedName("deviceString")
    private String mDeviceString;

    public Boolean getIsEnableCleanBeforeHome() {
        return isEnableCleanBeforeHome;
    }

    public void setIsEnableCleanBeforeHome(Boolean isEnableCleanBeforeHome) {
        this.isEnableCleanBeforeHome = isEnableCleanBeforeHome;
    }

    public String getTimeToHome() {
        return mTimeToHome;
    }

    public void setTimeToHome(String timeToHome) {
        mTimeToHome = timeToHome;
    }

    public String getDeviceString() {
        return mDeviceString;
    }

    public void setDeviceString(String deviceString) {
        mDeviceString = deviceString;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

}
