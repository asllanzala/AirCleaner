package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestResponse;

/**
 * Created by E573227 on 2/8/2015.
 */
public class CommTaskResponse implements IRequestResponse {
    @SerializedName("state")
    private String mState;

    @SerializedName("faultReason")
    private String mFaultReason;

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getFaultReason() {
        return mFaultReason;
    }

    public void setFaultReason(String faultReason) {
        mFaultReason = faultReason;
    }
}
