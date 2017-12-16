package com.honeywell.hch.airtouchv2.app.authorize;

import android.content.Intent;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLoginResponse;
import com.honeywell.hch.airtouchv2.framework.app.BaseApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Jin Qian on 1/27/15.
 * After login succeed, need to save all data to AuthorizeApp.
 * Including all homes, all devices and set default home.
 */
public class AuthorizeApp extends BaseApp{
    // private String mEmail;
    private String mNickname;
    private String mMobilePhone;
    private String mPassword;
    private Boolean mIsRemember;
    private Boolean mIsAutoLogin;
    private int mDefaultHomeNumber;

    // do not save below properties to sharedPreference
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private UserLocation mCurrentHome;
    private int mCurrentDeviceId;
    private String mUserId;
    private String mSessionId;
    private Boolean isLoginSuccess = false;
    private Boolean isGetAllData =false;

    private Boolean mIsUserWantToEnroll = false;
    private Boolean mIsAutoLoginOngoing = false;
    private Long mSessionLastUpdated = 0L;

    private int mUserHomeNumber;

    private int mHomeDeviceTotalNumber = 0;
    private int mHomeAirTouchSeriesDeviceNumber = 0;

    private static String TAG = "AirTouchCurrentUser";
    private static AuthorizeApp currentUser;

    private int mCurrentHomeIndex = 0;

    private IRefreshEnd notifyRefreshEnd;

    public static AuthorizeApp shareInstance() {
        if (currentUser == null) {
            currentUser = new AuthorizeApp();
        }
        return currentUser;
    }

    public void currentUserLogin() {
        if (!isLoginSuccess()) {
            setIsAutoLoginOngoing(true);
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "=================setIsAutoLoginOngoing(true)!");
        }
        UserLoginRequest userLoginRequest
                = new UserLoginRequest(mMobilePhone, mPassword, AppConfig.APPLICATION_ID);
        TccClient.sharedInstance().userLogin(userLoginRequest, mReceiveResponse);
    }

    public void updateSession() {
        TccClient.sharedInstance().updateSession(mSessionId, mReceiveResponse);
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case USER_LOGIN:
                    setIsAutoLoginOngoing(false);
                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, "=================setIsAutoLoginOngoing(false)!");
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            UserLoginResponse userLoginResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    UserLoginResponse.class);

                            if (userLoginResponse.getUserInfo() != null) {
                                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Login Success, userId："
                                        + userLoginResponse.getUserInfo().getUserID());

                                mUserId = userLoginResponse.getUserInfo().getUserID();
                                mSessionId = userLoginResponse.getSessionId();
                                setSessionId(mSessionId);
                                setSessionLastUpdated(System.currentTimeMillis());
                                setIsLoginSuccess(true);
                                setIsGetAllData(false);

                                TccClient.sharedInstance().getLocation(mUserId, mSessionId, mReceiveResponse);
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case GET_LOCATION:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            try {
                                mUserLocations.clear();
                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                                mUserHomeNumber = responseArray.length();

                                mHomeDeviceTotalNumber = 0;
                                mHomeAirTouchSeriesDeviceNumber = 0;

                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseJSON = responseArray.getJSONObject(i);
                                    UserLocation getLocationResponse = new Gson().fromJson(responseJSON.toString(), UserLocation.class);
                                    mUserLocations.add(getLocationResponse);
                                    mHomeDeviceTotalNumber += getLocationResponse.getAirTouchSDeviceNumber();

                                }
                                for (int i = 0; i < responseArray.length(); i++) {
                                    final UserLocation locationItem = mUserLocations.get(i);
                                    // get devices of each home
                                    locationItem.loadHomeDevicesData(new IRefreshEnd() {
                                        @Override
                                        public void notifyDataRefreshEnd() {
                                            mHomeAirTouchSeriesDeviceNumber++;

                                            if (mHomeAirTouchSeriesDeviceNumber == mHomeDeviceTotalNumber) {
                                                setIsGetAllData(true);

                                                Intent intent = new Intent("loginChanged");
                                                ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);

                                            }
                                        }
                                    });
                                }

//                                    // get devices of each home
//                                    TccClient.sharedInstance().getHomePm25(getLocationResponse.getLocationID(),
//                                            mSessionId, RequestID.GET_HOME_PM25, mReceiveResponse);

                                //old location has been cleared,so need to reset one as current home
                                resetCurrentHome();

                                if (mUserHomeNumber == 0 || mHomeDeviceTotalNumber == 0) {
                                    //send broadcast to MainActivity
                                    setIsGetAllData(true);

                                    Intent intent = new Intent("loginChanged");
                                    ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

//                case GET_HOME_PM25:
//                    ArrayList<HomeDevicePM25> homeDevices = new ArrayList<>();
//                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            try {
//                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                                for (int i = 0; i < responseArray.length(); i++) {
//                                    JSONObject responseJSON = responseArray.getJSONObject(i);
//                                    HomeDevicePM25 device = new Gson().fromJson(responseJSON.toString(),
//                                            HomeDevicePM25.class);
//                                    homeDevices.add(device);
//                                }
//                                // sometimes there is no device in home, fake one.
////                                if (responseArray.length() == 0) {
////                                    HomeDevicePM25 device = new HomeDevicePM25();
////                                    device.setDeviceID(0);
////                                    device.setPM25Value(0);
////                                    device.setAirCleanerFanModeSwitch("Off");
////                                    homeDevices.add(device);
////                                }
//                                homeDevicesList.add(homeDevices);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        /*
//                         * 1) GET_LOCATION - get all homes
//                         * 2) GET_HOME_PM25 - get all devices in each home
//                         * 3) save data of devices into each home
//                         */
//                        getHomePm25Count++;
//                        if (getHomePm25Count == mUserHomeNumber) {
//                            for (int i = 0; i < mUserHomeNumber; i++) {
//                                if (homeDevicesList.size() == 0)
//                                    break;
//                                getUserLocations().get(i).setHomeDevicesPM25(homeDevicesList.get(i));
//                            }
//                            setIsGetAllData(true);
//                            //send broadcast to MainActivity
//                            Intent intent = new Intent("loginChanged");
//                            ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);
//                        }
//
//                    } else {
//                        errorHandle(httpRequestResponse);
//                    }

//                    break;

                default:
                    setIsAutoLoginOngoing(false);
                    break;
            }
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

    public Boolean isAutoLogin() {
        return mIsAutoLogin;
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        mIsAutoLogin = isAutoLogin;
    }

    public Boolean isLoginSuccess() {
        return isLoginSuccess;
    }

    public void setIsLoginSuccess(Boolean isLoginSuccess) {
        this.isLoginSuccess = isLoginSuccess;
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

    public ArrayList<UserLocation> getUserLocations() {
        return mUserLocations;
    }

    public void setUserLocations(ArrayList<UserLocation> userLocations) {
        mUserLocations.clear();
        mUserLocations.addAll(userLocations);
    }

    public UserLocation getCurrentHome() {
        return mCurrentHome;
    }

    public void setCurrentHome(UserLocation currentHome,int currentHomeIndex) {
        mCurrentHome = currentHome;
        mCurrentHomeIndex = currentHomeIndex;
    }

    public void resetCurrentHome()
    {
        if (mCurrentHomeIndex < getUserLocations().size())
        {
            mCurrentHome =  getUserLocations().get(mCurrentHomeIndex);
        }
    }

    public int getCurrentDeviceId() {
        return mCurrentDeviceId;
    }

    public void setCurrentDeviceId(int currentDeviceId) {
        mCurrentDeviceId = currentDeviceId;
    }

    public Boolean isAutoLoginOngoing() {
        return mIsAutoLoginOngoing;
    }

    public void setIsAutoLoginOngoing(Boolean isAutoLoginOngoing) {
        mIsAutoLoginOngoing = isAutoLoginOngoing;
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        setIsLoginSuccess(false);
        //send broadcast to MainActivity
        Intent intent = new Intent("loginChanged");
        ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void dealResultAfterRelogin(String userId,String sessionId,boolean reloginResult)
    {
        if (reloginResult)
        {
            mUserId = userId;
            mSessionId = sessionId;
            setSessionId(mSessionId);
            setSessionLastUpdated(System.currentTimeMillis());
            setIsLoginSuccess(true);
        }
        else
        {
            setIsLoginSuccess(false);
            Intent intent = new Intent("loginChanged");
            ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);
        }
    }

    /**
     * get locaiton object using locaiton id
     * @param locationId
     * @return
     */
    public UserLocation getLocationWithId(int locationId) {
        for (UserLocation locationItem : mUserLocations) {
            if (locationItem.getLocationID() == locationId){
                return locationItem;
            }
        }
        LogUtil.log(LogUtil.LogLevel.ERROR,"AuthorizeApp","getLocationWithId location id = " + locationId + " ,is return null");
        return null;
    }
//
    public HomeDevice getCurrentDevice(){
        for ( UserLocation location : mUserLocations){
            for (HomeDevice homeDevice : location.getHomeDevices()) {
                if (homeDevice.getDeviceInfo().getDeviceID() == mCurrentDeviceId)
                {
                    return homeDevice;
                }
            }
        }
        return null;
    }


}
