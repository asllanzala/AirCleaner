package com.honeywell.hch.airtouchv3.framework.enrollment.models.http;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

public class WAPIConnectResponse implements IRequestResponse {

    @SerializedName("Success")
    private boolean mSuccess;

    @SerializedName("Message")
    private String mMessage;

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
