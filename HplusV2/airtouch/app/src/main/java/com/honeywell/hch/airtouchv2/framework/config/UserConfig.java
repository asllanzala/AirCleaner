package com.honeywell.hch.airtouchv2.framework.config;

import android.content.Context;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.database.UserDBService;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Save and load current user info from shared preference
 * Created by nan.liu on 1/15/15.
 */
public class UserConfig extends BaseConfig {

    public UserConfig(Context context) {
        super(context);
        mFileName = "user_config";
    }

    // load sharedPreference data to AuthorizeApp
    public void loadUserInfo() {
        AuthorizeApp.shareInstance().setNickname(getSharedPreferences().getString("userNickname", ""));
//        AuthorizeApp.shareInstance().setEmail(getSharedPreferences().getString("userEmail", ""));
        AuthorizeApp.shareInstance().setMobilePhone(getSharedPreferences().getString("mobilePhone", ""));
        AuthorizeApp.shareInstance().setPassword(getSharedPreferences().getString("userPassword", ""));
        AuthorizeApp.shareInstance().setIsRemember(getSharedPreferences().getBoolean("isRemember", true));
        AuthorizeApp.shareInstance().setIsAutoLogin(getSharedPreferences().getBoolean("isAutoLogin", false));

        // decrypt and set password to AuthorizeApp
//        try {
//            TripleDES tripleDES = new TripleDES("ECB");
//            String pass = new String(tripleDES.decrypt(tripleDES.loadPassword()));
//            AuthorizeApp.shareInstance().setPassword(pass);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String mobileNumber = AuthorizeApp.shareInstance().getMobilePhone();
        int defaultHomeNumber = getSharedPreferences().getInt(mobileNumber + "/defaultHome", 0);
        AuthorizeApp.shareInstance().setDefaultHomeNumber(defaultHomeNumber);

//        try {
//            AuthorizeApp.shareInstance().setPassword(CryptoUtil.decryptAES(getSharedPreferences()
//                    .getString("password", "")));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void saveUserInfo(String nickname, String mobilePhone, String password,
                             String userId, String sessionId, Boolean isLoginSuccess) {
        getSharedPreferencesEditor().putString("userNickname", nickname);
        getSharedPreferencesEditor().putString("mobilePhone", mobilePhone);
        getSharedPreferencesEditor().putString("userPassword", password);
        getSharedPreferencesEditor().putBoolean("isAutoLogin", isLoginSuccess);
//        try {
//            getSharedPreferencesEditor().putString("userPassword", CryptoUtil.encryptAES(password));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        getSharedPreferencesEditor().commit();

        // encrypt password
//        try {
//            TripleDES tripleDES = new TripleDES("ECB");
//            tripleDES.savePassword(tripleDES.encrypt(password));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        loadUserInfo();

        // do not save below fields to sharedPreference
        AuthorizeApp.shareInstance().setUserID(userId);
        AuthorizeApp.shareInstance().setSessionId(sessionId);
        AuthorizeApp.shareInstance().setIsLoginSuccess(isLoginSuccess);

        UserDBService userDBService = new UserDBService(mContext);

        // Save user data to database
        userDBService.insertUser(new User(userId, mobilePhone,
                password, nickname, 1));

        // change default user in database
        List<User> userList = new ArrayList<>();
        userList.addAll(userDBService.findAllUsers());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).IsDefault() == 1) {
                userList.get(i).setDefault(0);
                break;
            }
        }
        userDBService.insertUsers(userList);

        // delete user database if reach limit
        if (userList != null && userList.size() >= 5) {
            userDBService.deleteUserByID(userList.get(0).getUserID());
        }
    }

    public void saveRemember(Boolean isRemember) {
        getSharedPreferencesEditor().putBoolean("isRemember", isRemember);
        getSharedPreferencesEditor().commit();
        AuthorizeApp.shareInstance().setIsRemember(isRemember);
    }

    public void saveAutoLogin(Boolean isAuto) {
        getSharedPreferencesEditor().putBoolean("isAutoLogin", isAuto);
        getSharedPreferencesEditor().commit();
        AuthorizeApp.shareInstance().setIsAutoLogin(isAuto);
    }

    public void saveDefaultHomeNumber(String mobilePhone, int number) {
        getSharedPreferencesEditor().putInt(mobilePhone + "/defaultHome", number);
        getSharedPreferencesEditor().commit();
        AuthorizeApp.shareInstance().setDefaultHomeNumber(number);
    }

    public void saveNewPassword(String password) {
        getSharedPreferencesEditor().putString("userPassword", "");
//        try {
//            getSharedPreferencesEditor().putString("userPassword", CryptoUtil.encryptAES(password));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        getSharedPreferencesEditor().commit();

        // encrypt password
//        try {
//            TripleDES tripleDES = new TripleDES("ECB");
//            tripleDES.savePassword(tripleDES.encrypt(password));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        AuthorizeApp.shareInstance().setPassword(password);
    }

}
