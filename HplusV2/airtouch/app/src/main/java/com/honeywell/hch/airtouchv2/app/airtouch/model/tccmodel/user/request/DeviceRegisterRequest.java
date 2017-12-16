package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class DeviceRegisterRequest implements IRequestParams, Serializable {
    @SerializedName("mac")
    private String mMac;

    @SerializedName("crc")
    private String mCrc;

    @SerializedName("userDefinedDeviceName")
    private String mUserDefinedDeviceName;

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    public String getCrc() {
        return mCrc;
    }

    public void setCrc(String crc) {
        mCrc = crc;
    }

    public String getUserDefinedDeviceName() {
        return mUserDefinedDeviceName;
    }

    public void setUserDefinedDeviceName(String userDefinedDeviceName) {
        mUserDefinedDeviceName = userDefinedDeviceName;
    }

    public DeviceRegisterRequest(String mac, String crc, String device) {
        mMac = mac;
        mCrc = crc;
        mUserDefinedDeviceName = device;
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
