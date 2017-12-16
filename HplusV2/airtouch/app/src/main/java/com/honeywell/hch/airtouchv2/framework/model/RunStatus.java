package com.honeywell.hch.airtouchv2.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class RunStatus implements IRequestParams, Serializable {
    @SerializedName("fanSpeedStatus")
    private String mFanSpeedStatus;

    @SerializedName("fanFaultCode")
    private String mFanFaultCode;

    @SerializedName("aqDisplayLevel")
    private String mAqDisplayLevel;

    @SerializedName("scenarioMode")
    private String mScenarioMode;

    @SerializedName("runTime1")
    private String mRunTime1;

    @SerializedName("runTime2")
    private String mRunTime2;

    @SerializedName("runTime3")
    private String mRunTime3;

    @SerializedName("filter1Runtime")
    private int mFilter1Runtime;

    @SerializedName("filter2Runtime")
    private int mFilter2Runtime;

    @SerializedName("filter3Runtime")
    private int mFilter3Runtime;

    @SerializedName("tiltSensorStatus")
    private String mTiltSensorStatus;

    @SerializedName("isAlive")
    private Boolean mIsAlive;

    @SerializedName("mobileCtrlFlags")
    private String mMobileCtrlFlags;

    @SerializedName("cleanTime")
    private int[] mCleanTime;

    @SerializedName("cleanBeforeHomeEnable")
    private boolean mCleanBeforeHomeEnable;

    @SerializedName("timeToHome")
    private String mTimeToHome;

    @SerializedName("pM25Value")
    private int mPM25Value;

    public String getFanSpeedStatus() {
        return mFanSpeedStatus;
    }

    public void setFanSpeedStatus(String fanSpeedStatus) {
        mFanSpeedStatus = fanSpeedStatus;
    }

    public String getFanFaultCode() {
        return mFanFaultCode;
    }

    public void setFanFaultCode(String fanFaultCode) {
        mFanFaultCode = fanFaultCode;
    }

    public String getScenarioMode() {
        return mScenarioMode;
    }

    public void setScenarioMode(String scenarioMode) {
        mScenarioMode = scenarioMode;
    }

    public int getFilter1Runtime() {
        return mFilter1Runtime;
    }

    public void setFilter1Runtime(int filter1Runtime) {
        mFilter1Runtime = filter1Runtime;
    }

    public int getFilter2Runtime() {
        return mFilter2Runtime;
    }

    public void setFilter2Runtime(int filter2Runtime) {
        mFilter2Runtime = filter2Runtime;
    }

    public int getFilter3Runtime() {
        return mFilter3Runtime;
    }

    public void setFilter3Runtime(int filter3Runtime) {
        mFilter3Runtime = filter3Runtime;
    }

    public String getAqDisplayLevel() {
        return mAqDisplayLevel;
    }

    public void setAqDisplayLevel(String aqDisplayLevel) {
        mAqDisplayLevel = aqDisplayLevel;
    }

    public String getRunTime1() {
        return mRunTime1;
    }

    public void setRunTime1(String runTime1) {
        mRunTime1 = runTime1;
    }

    public String getRunTime2() {
        return mRunTime2;
    }

    public void setRunTime2(String runTime2) {
        mRunTime2 = runTime2;
    }

    public String getRunTime3() {
        return mRunTime3;
    }

    public void setRunTime3(String runTime3) {
        mRunTime3 = runTime3;
    }

    public String getTiltSensorStatus() {
        return mTiltSensorStatus;
    }

    public void setTiltSensorStatus(String tiltSensorStatus) {
        mTiltSensorStatus = tiltSensorStatus;
    }

    public Boolean getIsAlive() {
        return mIsAlive;
    }

    public void setIsAlive(Boolean isAlive) {
        mIsAlive = isAlive;
    }

    public String getMobileCtrlFlags() {
        return mMobileCtrlFlags;
    }

    public void setMobileCtrlFlags(String mobileCtrlFlags) {
        mMobileCtrlFlags = mobileCtrlFlags;
    }

    public int[] getCleanTime() {
        return mCleanTime;
    }

    public void setCleanTime(int[] cleanTime) {
        mCleanTime = cleanTime;
    }

    public boolean isCleanBeforeHomeEnable() {
        return mCleanBeforeHomeEnable;
    }

    public void setCleanBeforeHomeEnable(boolean cleanBeforeHomeEnable) {
        mCleanBeforeHomeEnable = cleanBeforeHomeEnable;
    }

    public String getTimeToHome() {
        return mTimeToHome;
    }

    public void setTimeToHome(String timeToHome) {
        mTimeToHome = timeToHome;
    }

    public int getmPM25Value() {
        return mPM25Value;
    }

    public void setmPM25Value(int mPM25Value) {
        this.mPM25Value = mPM25Value;
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
