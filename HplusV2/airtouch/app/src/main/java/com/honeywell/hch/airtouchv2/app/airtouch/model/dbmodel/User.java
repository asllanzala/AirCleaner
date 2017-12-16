package com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel;

import com.honeywell.hch.airtouchv2.framework.database.IDBModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nan.liu on 3/25/15.
 */
public class User implements Serializable, IDBModel {
    private String mUserID;
    private String mPhoneNumber;
    private String mPassword;
    private String mNickName;
    private int isDefault = 0;

    public User() {
    }

    public User(String userID, String phoneNumber, String password, String nickName, int isDefault) {
        setUserID(userID);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setNickName(nickName);
        setDefault(isDefault);
    }

    public User(HashMap<String, String> userMap) {
        setUserID(userMap.get("userID"));
        setPhoneNumber(userMap.get("phoneNumber"));
        setPassword(userMap.get("password"));
        setNickName(userMap.get("nickName"));
        setDefault(Integer.valueOf(userMap.get("isDefault")));
    }

    @Override
    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("userID", mUserID);
        userMap.put("phoneNumber", mPhoneNumber);
        userMap.put("password", mPassword);
        userMap.put("nickName", mNickName);
        userMap.put("isDefault", isDefault);
        return userMap;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public int IsDefault() {
        return isDefault;
    }

    public void setDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
