package com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel;

import com.honeywell.hch.airtouchv3.framework.database.IDBModel;
import com.honeywell.hch.airtouchv3.framework.database.UserDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

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
    private String mCountryCode = AirTouchConstants.CHINA_CODE;
    private int mIsDefault = 0;
    private int mIsEncrypted = 0;

    public User() {
    }

    public User(String userID, String phoneNumber, String password, String nickName, int
            isDefault, int isEncrypted, String countryCode) {
        setUserID(userID);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setNickName(nickName);
        setIsDefault(isDefault);
        setIsEncrypted(isEncrypted);
        setCountryCode(countryCode);
    }

    public User(HashMap<String, String> userMap) {
        setUserID(userMap.get(UserDBService.USER_ID));
        setPhoneNumber(userMap.get(UserDBService.PHONE_NUMBER));
        setPassword(userMap.get(UserDBService.PASSWORD));
        setNickName(userMap.get(UserDBService.NICKNAME));
        setIsDefault(Integer.valueOf(userMap.get(UserDBService.IS_DEFAULT)));
        setIsEncrypted(Integer.valueOf(userMap.get(UserDBService.IS_ENCRYPTED)));
        setCountryCode(userMap.get(UserDBService.COUNTRY_CODE));
    }

    @Override
    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(UserDBService.USER_ID, mUserID);
        userMap.put(UserDBService.PHONE_NUMBER, mPhoneNumber);
        userMap.put(UserDBService.PASSWORD, mPassword);
        userMap.put(UserDBService.NICKNAME, mNickName);
        userMap.put(UserDBService.IS_DEFAULT, mIsDefault);
        userMap.put(UserDBService.IS_ENCRYPTED, mIsEncrypted);
        userMap.put(UserDBService.COUNTRY_CODE, mCountryCode);
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

    public int getIsDefault() {
        return mIsDefault;
    }

    public void setIsDefault(int isDefault) {
        this.mIsDefault = isDefault;
    }

    public int getIsEncrypted() {
        return mIsEncrypted;
    }

    public void setIsEncrypted(int isEncrypted) {
        this.mIsEncrypted = isEncrypted;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }
}
