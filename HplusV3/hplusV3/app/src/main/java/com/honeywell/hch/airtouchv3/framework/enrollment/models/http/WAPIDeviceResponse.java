package com.honeywell.hch.airtouchv3.framework.enrollment.models.http;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

public class WAPIDeviceResponse implements IRequestResponse {

    @SerializedName("MAC")
    private String mMacID;

    @SerializedName("CRC")
    private String mCrcID;


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

}
