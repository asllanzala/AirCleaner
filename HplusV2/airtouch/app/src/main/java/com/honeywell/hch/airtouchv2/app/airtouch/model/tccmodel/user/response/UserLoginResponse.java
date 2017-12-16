package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/19/2015.
 */
public class UserLoginResponse implements IRequestParams, Serializable {
    @SerializedName("sessionId")
    private String mSessionId;

    @SerializedName("userInfo")
    private UserInfo mUserInfo;

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionID) {
        mSessionId = sessionID;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
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
