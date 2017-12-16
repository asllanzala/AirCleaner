package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class ChangePasswordRequest implements IRequestParams, Serializable {
    @SerializedName("oldPassword")
    private String mOldPassword;

    @SerializedName("newPassword")
    private String mNewPassword;


    public ChangePasswordRequest(String oldPassword, String newPassword) {
        mOldPassword = oldPassword;
        mNewPassword = newPassword;
    }

    public String getOldPassword() {

        return mOldPassword;
    }

    public void setOldPassword(String oldPassword) {
        mOldPassword = oldPassword;
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
