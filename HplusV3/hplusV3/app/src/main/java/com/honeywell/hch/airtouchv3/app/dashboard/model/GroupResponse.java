package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

/**
 * Created by Qian Jin on 10/14/15.
 * get group by group id
 */
public class GroupResponse implements IRequestResponse {
    public static final String CODE_ID = "code_id";

    @SerializedName("messgage")
    private String mMessage;

    @SerializedName("code")
    private int mCode;

    public String getMessage() {
        return mMessage;
    }

    public int getCode() {
        return mCode;
    }
}
