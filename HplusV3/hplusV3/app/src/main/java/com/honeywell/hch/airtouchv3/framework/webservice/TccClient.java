package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.BackHomeRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.DeviceControlRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.AddLocationRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.ChangePasswordRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.DeviceRegisterRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.SmsValidRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UpdatePasswordRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 1/22/15
 */
public class TccClient extends HTTPClient implements TCCWebService {
    private static final String TAG = "AirTouchTccClient";
    private static TccClient mTccClient;
    private static final String REQUEST_SESSION = "session";
    private static final String LOG_OUT = "session";
    private static final String REQUEST_REGISTER_ACCOUNT = "userAccounts";
    private static final String UPDATE_PASSWORD = "passwordUpdate";
    private static final String CHANGE_PASSWORD = "userAccounts/%1$s/passwordChange";
    private static final String REQUEST_SMS_VALID = "userAccounts/smsValid";
    private static final String VERIFY_SMS_VALID = "userAccounts/smsValid?phoneNum=%1$s&validNo=%2$s";
    private static final String GET_LOCATION = "locations?userId=%1$s&allData=true";
    private static final String ADD_LOCATION = "locations?userId=%1$s";
    private static final String ADD_DEVICE = "devices?locationId=%1$d";
    private static final String CHECK_MAC = "gateways/AliveStatus?macId=%1$s";
    private static final String CONTROL_DEVICE = "devices/%1$d/AirCleaner/Control";
    private static final String GET_DEVICE_STATUS = "devices/%1$d/AirCleaner/RunStatus";
    private static final String GET_CAPABILITY = "devices/%1$d/AirCleaner/Capability";
    private static final String GET_HOME_PM25 = "locations/%1$d/AirCleaner/PM25";
    private static final String COMM_TASK = "commTasks?commTaskId=%1$d";
    private static final String DELETE_DEVICE = "devices/%1$d";
    private static final String CLEAN_TIME = "AirCleaner/%1$d/BackHome";
    private String mBaseLocalUrl;

    public static TccClient sharedInstance() {
        if (null == mTccClient || AppConfig.isChangeEnv) {
            mTccClient = new TccClient();
        }
        return mTccClient;
    }

    public TccClient() {
//        mBaseLocalUrl = AppConfig.isDebugMode ? "https://qaweb.chinacloudapp.cn/WebAPI/api/"
//                : "https://mservice.honeywell.com.cn/WebAPI/api/";
        mBaseLocalUrl = AppConfig.isDebugMode ? "https://stweb.chinacloudapp.cn/WebAPI/api/"
                : "https://mservice.honeywell.com.cn/WebAPI/api/";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    @Override
    public int userRegister(UserRegisterRequest request, IReceiveResponse
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_REGISTER_ACCOUNT), null, RequestID
                .USER_REGISTER, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse, 500, 500);
    }

    @Override
    public int updatePassword(UpdatePasswordRequest request, IReceiveResponse
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(UPDATE_PASSWORD), null, RequestID.UPDATE_PASSWORD, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int changePassword(String userId, String sessionId, ChangePasswordRequest request,
                              IReceiveResponse
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(CHANGE_PASSWORD, userId), sessionId, RequestID
                .CHANGE_PASSWORD, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getSmsCode(SmsValidRequest request, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_SMS_VALID), null, RequestID.GET_SMS_CODE, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int verifySmsCode(String phoneNum, String smsCode, SmsValidRequest request,
                             IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(VERIFY_SMS_VALID, phoneNum, smsCode), null,
                RequestID.VERIFY_SMS_VALID, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int userLogin(UserLoginRequest request, IReceiveResponse
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_SESSION), null, RequestID.USER_LOGIN, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int userLogout(String sessionId, UserLoginRequest request, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.DELETE, getLocalUrl(LOG_OUT), sessionId, RequestID.USER_LOGOUT, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getHomePm25(int locationId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(GET_HOME_PM25, locationId), sessionId, RequestID
                .GET_HOME_PM25, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getLocation(String userId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(GET_LOCATION, userId), sessionId, RequestID
                .GET_LOCATION, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int addLocation(String userId, String sessionId, AddLocationRequest request,
                           IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(ADD_LOCATION, userId), sessionId, RequestID
                .ADD_LOCATION, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int addDevice(int locationId, String sessionId, DeviceRegisterRequest request,
                         IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(ADD_DEVICE, locationId), sessionId, RequestID
                .ADD_DEVICE, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int controlDevice(int deviceId, String sessionId, DeviceControlRequest request,
                             IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(CONTROL_DEVICE, deviceId), sessionId, RequestID
                .CONTROL_DEVICE, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getDeviceStatus(int deviceId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(GET_DEVICE_STATUS, deviceId), sessionId, RequestID
                .GET_DEVICE_STATUS, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getDeviceCapability(int deviceId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(GET_CAPABILITY, deviceId), sessionId, RequestID
                .GET_DEVICE_CAPABILITY, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int checkMac(String macId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(CHECK_MAC, macId), sessionId, RequestID.CHECK_MAC, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int updateSession(String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(REQUEST_SESSION), sessionId, RequestID.UPDATE_SESSION, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int getCommTask(int taskId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(COMM_TASK, taskId), sessionId, RequestID.COMM_TASK, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int deleteDevice(int deviceId, String sessionId, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.DELETE, getLocalUrl(DELETE_DEVICE, deviceId), sessionId, RequestID.DELETE_DEVICE, null);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    @Override
    public int cleanTime(int locationId, String sessionId, BackHomeRequest request,
                         IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(CLEAN_TIME, locationId), sessionId, RequestID.CLEAN_TIME, request);
        return executeHTTPRequest(httpRequestParams, receiveResponse);
    }

}