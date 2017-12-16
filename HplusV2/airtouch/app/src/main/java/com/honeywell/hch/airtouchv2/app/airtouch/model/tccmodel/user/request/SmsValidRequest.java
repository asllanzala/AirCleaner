package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class SmsValidRequest implements IRequestParams, Serializable {
    @SerializedName("userId")
    private int mUserId;

    @SerializedName("phoneNum")
    private String mPhoneNum;

    @SerializedName("smsContent")
    private String mSmsContent;


    public SmsValidRequest(int userId, String phoneNum, String smsContent) {
        mUserId = userId;
        mPhoneNum = phoneNum;
        mSmsContent = smsContent;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        mPhoneNum = phoneNum;
    }

    public String getSmsContent() {
        return mSmsContent;
    }

    public void setSmsContent(String smsContent) {
        mSmsContent = smsContent;
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
