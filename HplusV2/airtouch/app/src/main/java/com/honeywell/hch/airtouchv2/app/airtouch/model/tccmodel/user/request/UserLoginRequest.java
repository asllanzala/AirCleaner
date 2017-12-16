package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class UserLoginRequest implements IRequestParams, Serializable {
    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("applicationID")
    private String mApplicationID;



    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getApplicationID() {
        return mApplicationID;
    }

    public void setApplicationID(String mApplicationID) {
        this.mApplicationID = mApplicationID;
    }

    public UserLoginRequest(String username, String password, String applicationID) {
        mUsername = username;
        mPassword = password;
        mApplicationID = applicationID;
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
