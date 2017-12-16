package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;

/**
 * Created by Qian Jin on 10/14/15.
 * get group by group id
 */
public class DevicesForGroupResponse implements IRequestResponse, Serializable {
    @SerializedName("deviceId")
    private int mDeviceId;

    @SerializedName("deviceName")
    private String mDeviceName;

    @SerializedName("deviceType")
    private double mDeviceType;

    @SerializedName("isAlive")
    private Boolean mIsAlive;

    @SerializedName("macId")
    private String mMacId;

    @SerializedName("isUpgrading")
    private Boolean mIsUpgrading;

    @SerializedName("isMasterDevice")
    private int mIsMasterDevice;

    public int getDeviceId() {
        return mDeviceId;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public double getDeviceType() {
        return mDeviceType;
    }

    public Boolean getIsAlive() {
        return mIsAlive;
    }

    public String getMacId() {
        return mMacId;
    }

    public Boolean getIsUpgrading() {
        return mIsUpgrading;
    }

    public int getIsMasterDevice() {
        return mIsMasterDevice;
    }

}
