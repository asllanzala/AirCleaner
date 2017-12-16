package com.honeywell.hch.airtouchv2.framework.enrollment.models.http;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

public class CreateDeviceRequest implements IRequestParams, Serializable {

    private static final long serialVersionUID = 1323955823881625022L;

    @SerializedName("mac")
    private String mMacID;

    @SerializedName("crc")
    private String mCrcID;

    @SerializedName("deviceTypes")
    private String mDeviceType = "AirTouch";

    @SerializedName("name")
    private String mName;

    public CreateDeviceRequest() {
    }

    public CreateDeviceRequest(WAPIDeviceResponse wapiDeviceResponse) {
        mMacID = wapiDeviceResponse.getMacID();
        mCrcID = wapiDeviceResponse.getCrcID();
    }

    public String getMacID() {
        return mMacID;
    }

    public void setMacID(String macID) {
        mMacID = macID;
    }

    public String getCrcID() {
        return mCrcID;
    }

    public void setCrcID(String crcID) {
        mCrcID = crcID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
