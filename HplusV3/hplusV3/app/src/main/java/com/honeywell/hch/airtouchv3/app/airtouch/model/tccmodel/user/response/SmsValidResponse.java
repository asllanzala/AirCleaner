package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class SmsValidResponse implements IRequestResponse {
    @SerializedName("isValid")
    private Boolean mIsValid;

    public Boolean isValid() {
        return mIsValid;
    }

    public void setIsValid(Boolean isValid) {
        mIsValid = isValid;
    }
}