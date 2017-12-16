package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/19/2015.
 * GET api/locations/{locationId}/AirCleaner/PM25
 */
public class HomeDevicePM25 implements IRequestParams, Serializable {
    @SerializedName("deviceID")
    private int mDeviceID;

    @SerializedName("pM25Value")
    private int mPM25Value;

    @SerializedName("scenarioMode")
    private String mAirCleanerFanModeSwitch;

    @SerializedName("fanSpeedStatus")
    private String mFanSpeedStatus;

    @SerializedName("cleanBeforeHomeEnable")
    private Boolean mCleanBeforeHomeEnable;

    @SerializedName("timeToHome")
    private String mTimeToHome;

    private RunStatus mRunStatusResponse;

    public int getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(int deviceID) {
        mDeviceID = deviceID;
    }

    public int getPM25Value() {
        return mPM25Value;
    }

    public void setPM25Value(int PM25Value) {
        mPM25Value = PM25Value;
    }

    public Boolean getCleanBeforeHomeEnable() {
        return mCleanBeforeHomeEnable;
    }

    public void setCleanBeforeHomeEnable(Boolean cleanBeforeHomeEnable) {
        mCleanBeforeHomeEnable = cleanBeforeHomeEnable;
    }

    public String getTimeToHome() {
        return mTimeToHome;
    }

    public void setTimeToHome(String timeToHome) {
        mTimeToHome = timeToHome;
    }

    public String getAirCleanerFanModeSwitch() {
        return mAirCleanerFanModeSwitch;
    }

    public void setAirCleanerFanModeSwitch(String airCleanerFanModeSwitch) {
        mAirCleanerFanModeSwitch = airCleanerFanModeSwitch;
    }

    public RunStatus getRunStatusResponse() {
        return mRunStatusResponse;
    }

    public void setRunStatusResponse(RunStatus runStatusResponse) {
        mRunStatusResponse = runStatusResponse;
    }

    public String getFanSpeedStatus() {
        return mFanSpeedStatus;
    }

    public void setFanSpeedStatus(String fanSpeedStatus) {
        mFanSpeedStatus = fanSpeedStatus;
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


