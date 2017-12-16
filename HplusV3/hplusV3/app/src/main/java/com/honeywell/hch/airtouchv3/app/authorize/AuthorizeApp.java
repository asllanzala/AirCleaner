package com.honeywell.hch.airtouchv3.app.authorize;

import android.content.Context;
import android.content.Intent;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.User;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.framework.app.BaseApp;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.UserDBService;
import com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification.BaiduPushConfig;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.framework.webservice.task.UserLoginTask;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.SharePreferenceUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.honeywell.hch.airtouchv3.lib.util.TripleDES;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jin Qian on 1/27/15.
 * After login succeed, need to save all data to AuthorizeApp.
 * Including all homes, all devices and set default home.
 */
public class AuthorizeApp extends BaseApp{

    private static String TAG = "AirTouchCurrentUser";

    // private String mEmail;
    private String mNickname;
    private String mMobilePhone = "";
    private String mPassword;
    private Boolean mIsRemember = true;
    private int mDefaultHomeNumber;
    private int mDefaultHomeLocalId;

    private UserDBService mUserDBService;

    // do not save below properties to sharedPreference
    private String mUserId;
    private String mSessionId;
    private Boolean mIsLoginSuccess = false;
    private Boolean isGetAllData =false;
    private String mCountryCode; // user account comes from
    private String mGPSCountry; // country located by GPS

    private Boolean mIsUserWantToEnroll = false;
    private Boolean mIsAutoLoginOngoing = false;
    private Long mSessionLastUpdated = 0L;

    private Context mContext;


    public AuthorizeApp(Context context) {
        mContext = context;
        mUserDBService = new UserDBService(context);
    }

    public void currentUserLogin() {
        if (!isLoginSuccess()) {
            setIsAutoLoginOngoing(true);
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "=================setIsAutoLoginOngoing(true)!");
        }
        UserLoginRequest userLoginRequest
                = new UserLoginRequest(mMobilePhone, mPassword, AppConfig.APPLICATION_ID);
        UserLoginTask userLoginTask
                = new UserLoginTask(loginReceiver,userLoginRequest);
        AsyncTaskExecutorUtil.executeAsyncTask(userLoginTask);
    }


    // load sharedPreference data to AuthorizeApp
    public void loadUserInfo() {
        User defaultUser = mUserDBService.getDefaultUser();
        if (defaultUser == null && mUserDBService.findAllUsers().size() > 0) {
            defaultUser = mUserDBService.findAllUsers().get(0);
        }
        if (defaultUser != null) {
            setNickname(defaultUser.getNickName());
            setMobilePhone(defaultUser.getPhoneNumber());
            setIsRemember(!StringUtil.isEmpty(defaultUser.getPassword()));
            setCountryCode(defaultUser.getCountryCode());
            if (!mIsRemember) {
                return;
            }
            if (defaultUser.getIsEncrypted() == 0) {
                setPassword(defaultUser.getPassword());
            } else {
                try {
                    TripleDES tripleDES = new TripleDES("ECB");
                    setPassword(new String(tripleDES.decrypt(defaultUser.getPassword().getBytes
                            ("ISO-8859-1"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveUserInfo(String nickname, String mobilePhone, String password,
                             String userId, String sessionId, String countryCode){
        // do not save below fields to sharedPreference
        setUserID(userId);
        setSessionId(sessionId);
        setIsLoginSuccess(true);

        User oldDefaultUser = null;
        List<User> userList = new ArrayList<>();
        userList.addAll(mUserDBService.findAllUsers());

        try {
            // change default user in database
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getIsDefault() == 1) {
                    userList.get(i).setIsDefault(0);
                    oldDefaultUser = userList.get(i);
                    break;
                }
            }

            if (oldDefaultUser != null) {
                mUserDBService.insertUser(oldDefaultUser);
            }

            // Save user data to database
            TripleDES tripleDES = new TripleDES("ECB");
            mUserDBService.insertUser(new User(userId, mobilePhone,
                    mIsRemember ? new String(tripleDES.encrypt(password), "ISO-8859-1") : "",
                    nickname, 1, 1, countryCode));

            // delete user database if reach limit
            if (userList != null && userList.size() >= 5) {
                mUserDBService.deleteUserByID(userList.get(0).getUserID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadUserInfo();
    }

    public void saveNewPassword(String password) {
        setPassword(password);
        User currentUser = mUserDBService.getUserByID(mUserId);
        try {
            TripleDES tripleDES = new TripleDES("ECB");
            currentUser.setPassword(mIsRemember ? new String(tripleDES.encrypt(password), "ISO-8859-1") : "");
            mUserDBService.insertUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    IActivityReceive loginReceiver = new IActivityReceive() {
        @Override
        public void onReceive(ResponseResult responseResult) {
            if (responseResult.isResult()) {
                // India version
                if(!AppConfig.shareInstance().isIndiaAccount()) {
                    SharePreferenceUtil.clearMyPreference(mContext, SharePreferenceUtil.getSharedPreferencesInstance());
                    PushManager.startWork(mContext,
                            PushConstants.LOGIN_TYPE_API_KEY,
                            BaiduPushConfig.getMetaValue(mContext, BaiduPushConfig.BAIDUPUSHAPIKEY));
                }
            }
            Intent intent = new Intent("loginChanged");
            mContext.sendBroadcast(intent);
        }
    };


    public String getUserID() {
        return mUserId;
    }

    public void setUserID(String userID) {
        mUserId = userID;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getMobilePhone() {
        return mMobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        mMobilePhone = mobilePhone;
    }

    //    public String getEmail() {
//        return mEmail;
//    }
//
//    public void setEmail(String email) {
//        mEmail = email;
//    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public Boolean isRemember() {
        return mIsRemember;
    }

    public void setIsRemember(Boolean isRemember) {
        mIsRemember = isRemember;
    }

    public Boolean isLoginSuccess() {
        return mIsLoginSuccess;
    }

    public void setIsLoginSuccess(Boolean isLoginSuccess) {
        this.mIsLoginSuccess = isLoginSuccess;
    }

    public Boolean isGetAllData(){
        return isGetAllData;
    }

    public void setIsGetAllData(Boolean isGetAllData1){
        this.isGetAllData = isGetAllData1;
    }

    public Boolean isUserWantToEnroll() {
        return mIsUserWantToEnroll;
    }

    public void setIsUserWantToEnroll(Boolean isUserWantToEnroll) {
        mIsUserWantToEnroll = isUserWantToEnroll;
    }

    public int getDefaultHomeNumber() {
        return mDefaultHomeNumber;
    }

    public void setDefaultHomeNumber(int defaultHomeNumber) {
        mDefaultHomeNumber = defaultHomeNumber;
    }

    public Long getSessionLastUpdated() {
        return mSessionLastUpdated;
    }

    public void setSessionLastUpdated(Long sessionLastUpdated) {
        mSessionLastUpdated = sessionLastUpdated;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getGPSCountry() {
        return mGPSCountry;
    }

    public void setGPSCountry(String GPSCountry) {
        mGPSCountry = GPSCountry;
    }

    //    public ArrayList<UserLocation> getUserLocations() {
//        return mUserLocations;
//    }
//
//    public void setUserLocations(ArrayList<UserLocation> userLocations) {
//        mUserLocations.clear();
//        mUserLocations.addAll(userLocations);
//    }
//
//    public UserLocation getCurrentHome() {
//        return mCurrentHome;
//    }
//
//    public void setCurrentHome(UserLocation currentHome,int currentHomeIndex) {
//        mCurrentHome = currentHome;
//        mCurrentHomeIndex = currentHomeIndex;
//    }
//
//    public void resetCurrentHome()
//    {
//        if (mCurrentHomeIndex < getUserLocations().size())
//        {
//            mCurrentHome =  getUserLocations().get(mCurrentHomeIndex);
//        }
//    }
//
//    public int getCurrentDeviceId() {
//        return mCurrentDeviceId;
//    }
//
//    public void setCurrentDeviceId(int currentDeviceId) {
//        mCurrentDeviceId = currentDeviceId;
//    }

    public Boolean isAutoLoginOngoing() {
        return mIsAutoLoginOngoing;
    }

    public void setIsAutoLoginOngoing(Boolean isAutoLoginOngoing) {
        mIsAutoLoginOngoing = isAutoLoginOngoing;
    }


    public void dealResultAfterRelogin(String userId,String sessionId,String nickname,boolean reloginResult, String
            countryCode)
    {
        if (reloginResult)
        {
            mUserId = userId;
            mSessionId = sessionId;
            setSessionId(mSessionId);
            setSessionLastUpdated(System.currentTimeMillis());
            setIsLoginSuccess(true);
            mNickname = nickname;
            mCountryCode = countryCode;
        }
        else
        {
            setIsLoginSuccess(false);
        }
    }

    public int getDefaultHomeLocalId() {
        return mDefaultHomeLocalId;
    }

    public void setDefaultHomeLocalId(int mDefaultHomeLocalId) {
        this.mDefaultHomeLocalId = mDefaultHomeLocalId;
    }
}
