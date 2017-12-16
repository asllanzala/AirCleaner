package com.honeywell.hch.airtouchv3.framework.config;

import android.content.Context;

import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;


/**
 * Save and load current user info from shared preference
 * Created by nan.liu on 1/15/15.
 */
public class UserConfig extends BaseConfig {

    private AuthorizeApp mAuthorizeApp;

    public UserConfig(Context context) {
        super(context);
        mFileName = "user_config";
        mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
    }

    // load default home id to AuthorizeApp
    public void loadDefaultHome() {
        String mobileNumber = mAuthorizeApp.getMobilePhone();
        int defaultHomeNumber = getSharedPreferences().getInt(mobileNumber + "/defaultHome", 0);
        mAuthorizeApp.setDefaultHomeNumber(defaultHomeNumber);
    }

    public void loadDefaultHomeId() {
        String mobileNumber = mAuthorizeApp.getMobilePhone();
        int defaultHomeId = getSharedPreferences().getInt(mobileNumber + "/defaultHomeId", 0);
        mAuthorizeApp.setDefaultHomeLocalId(defaultHomeId);
    }

    public void saveDefaultHomeNumber(String mobilePhone, int number) {
        getSharedPreferencesEditor().putInt(mobilePhone + "/defaultHome", number);
        getSharedPreferencesEditor().commit();
        mAuthorizeApp.setDefaultHomeNumber(number);
    }

    public void saveDefaultHomeId(String mobilePhone, int id) {
        getSharedPreferencesEditor().putInt(mobilePhone + "/defaultHomeId", id);
        getSharedPreferencesEditor().commit();
        mAuthorizeApp.setDefaultHomeLocalId(id);
    }

    public Boolean loadAutoLogin() {
        return getSharedPreferences().getBoolean("isAutoLogin", false);
    }

    public void saveAutoLogin(Boolean isAutoLogin) {
        getSharedPreferencesEditor().putBoolean("isAutoLogin", isAutoLogin);
        getSharedPreferencesEditor().commit();
    }


}
