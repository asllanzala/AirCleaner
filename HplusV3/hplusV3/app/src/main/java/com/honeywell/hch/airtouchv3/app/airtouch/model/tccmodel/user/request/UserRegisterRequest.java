package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;


public class UserRegisterRequest implements IRequestParams, Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("username")
    private String mTelephone;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("countryPhoneNum")
    private String mCountryCode;

    @SerializedName("name")
    private String mNickname;

    // deprecated
    @SerializedName("email")
    private String mEmail;



    public UserRegisterRequest(String nickname, String telephone, String password, String countryCode) {
        mNickname = nickname;
        mTelephone = telephone;
        mPassword = password;
        mCountryCode = countryCode;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        mTelephone = telephone;
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