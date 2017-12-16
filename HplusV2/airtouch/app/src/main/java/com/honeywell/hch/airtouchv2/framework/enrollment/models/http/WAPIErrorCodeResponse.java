package com.honeywell.hch.airtouchv2.framework.enrollment.models.http;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestResponse;

public class WAPIErrorCodeResponse implements IRequestResponse {

    @SerializedName("error")
    private int mError;

    public int getError() {
        return mError;
    }

    public void setError(int error) {
        mError = error;
    }
}
