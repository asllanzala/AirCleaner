package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestResponse;

/**
 * Created by Jin Qian on 1/23/2015.
 */
public class ErrorResponse implements IRequestResponse {
    @SerializedName("code")
    private String mCode;

    @SerializedName("message")
    private String mMessage;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}