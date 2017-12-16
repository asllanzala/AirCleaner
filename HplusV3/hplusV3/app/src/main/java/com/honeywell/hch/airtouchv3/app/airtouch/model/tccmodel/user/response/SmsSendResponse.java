package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class SmsSendResponse implements IRequestResponse {
    @SerializedName("isSend")
    private Boolean mIsSend;

    public Boolean isSend() {
        return mIsSend;
    }

    public void setIsSend(Boolean isSend) {
        mIsSend = isSend;
    }
}