package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class UpdatePasswordRequest implements IRequestParams, Serializable {
    @SerializedName("username")
    private String mUsername;

    @SerializedName("newPassword")
    private String mNewPassword;


    public UpdatePasswordRequest(String username, String newPassword) {
        mUsername = username;
        mNewPassword = newPassword;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
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
