package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/19/2015.
 */
public class UserInfo implements IRequestParams, Serializable {
    @SerializedName("userID")
    private String mUserID;

    @SerializedName("username")
    private String mEmail;

    @SerializedName("firstname")
    private String mFirstName;

    @SerializedName("lastname")
    private String mLastName;

    @SerializedName("streetAddress")
    private String mStreetAddress;

    @SerializedName("city")
    private String mCity;

    @SerializedName("state")
    private String mState;

    @SerializedName("zipcode")
    private String mZipcode;

    @SerializedName("country")
    private String mCountry;

//    @SerializedName("telephone")
//    private String mTelephone;

    @SerializedName("countryPhoneNum")
    private String mCountryPhoneNum;

    @SerializedName("userLanguage")
    private String mUserLanguage;

    @SerializedName("isActivated")
    private String mIsActivated;

    @SerializedName("deviceCount")
    private String mDeviceCount;

    @SerializedName("tenantID")
    private String mTenantID;

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getStreetAddress() {
        return mStreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        mStreetAddress = streetAddress;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        mZipcode = zipcode;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getCountryPhoneNum() {
        return mCountryPhoneNum;
    }

    public void setCountryPhoneNum(String countryPhoneNum) {
        mCountryPhoneNum = countryPhoneNum;
    }

    public String getUserLanguage() {
        return mUserLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        mUserLanguage = userLanguage;
    }

    public String getIsActivated() {
        return mIsActivated;
    }

    public void setIsActivated(String isActivated) {
        mIsActivated = isActivated;
    }

    public String getDeviceCount() {
        return mDeviceCount;
    }

    public void setDeviceCount(String deviceCount) {
        mDeviceCount = deviceCount;
    }

    public String getTenantID() {
        return mTenantID;
    }

    public void setTenantID(String tenantID) {
        mTenantID = tenantID;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

//    {
//        "sessionId": "3C6D4907-1E2B-416F-B411-83E92540D7E7",
//            "userInfo": {
//                "userID": 7010,
//                "username": "15800349135",
//                "firstname": "Jin",
//                "lastname": "",
//                "streetAddress": "StreetAddress",
//                "city": "New York",
//                "state": "New York",
//                "zipcode": "30030",
//                "country": "US",
//                "telephone": "",
//                "userLanguage": "en-US",
//                "isActivated": true,
//                "deviceCount": 0,
//                "tenantID": 0
//            },
//        "latestEulaAccepted": true
//    }

}


