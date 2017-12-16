package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

/**
 * Created by Qian Jin on 10/17/15.
 */
public class GroupCommTaskResponse implements IRequestResponse {
    @SerializedName("deviceID")
    private int mDeviceId;

    @SerializedName("commonTaskID")
    private int mCommTaskId;

    @SerializedName("message")
    private String mMessage;

    public int getDeviceId() {
        return mDeviceId;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getCommTaskId() {
        return mCommTaskId;
    }

}
