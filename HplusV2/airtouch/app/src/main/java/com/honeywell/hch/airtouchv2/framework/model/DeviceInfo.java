package com.honeywell.hch.airtouchv2.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/19/2015.
 *
 * 2015-7-31 changed by Stephen(H127856)
 * data model reconstruction
 */
public class DeviceInfo implements IRequestParams, Serializable {
    @SerializedName("deviceID")
    private int mDeviceID;

    @SerializedName("name")
    private String mName;

    @SerializedName("isUpgrading")
    private Boolean mIsUpgrading;

    @SerializedName("isAlive")
    private Boolean mIsAlive;

    @SerializedName("firmwareVersion")
    private String mFirmwareVersion;

    @SerializedName("macID")
    private String mMacID;

    @SerializedName("deviceType")
    private int mDeviceType;

    public int getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(int deviceID) {
        mDeviceID = deviceID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Boolean getIsUpgrading() {
        return mIsUpgrading;
    }

    public void setIsUpgrading(Boolean isUpgrading) {
        mIsUpgrading = isUpgrading;
    }

    public Boolean getIsAlive() {
        return mIsAlive;
    }

    public void setIsAlive(Boolean isAlive) {
        mIsAlive = isAlive;
    }

    public String getFirmwareVersion() {
        return mFirmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        mFirmwareVersion = firmwareVersion;
    }

    public String getMacID() {
        return mMacID;
    }

    public void setMacID(String macID) {
        mMacID = macID;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

    public int getmDeviceType() {
        return mDeviceType;
    }

    public void setmDeviceType(int mDeviceType) {
        this.mDeviceType = mDeviceType;
    }
}


