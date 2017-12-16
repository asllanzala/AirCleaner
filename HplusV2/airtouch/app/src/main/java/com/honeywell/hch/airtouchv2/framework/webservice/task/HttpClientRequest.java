package com.honeywell.hch.airtouchv2.framework.webservice.task;

import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.webservice.HTTPClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;

/**
 * Created by Jin Qian on 1/22/15
 */
public class HttpClientRequest extends HTTPClient {
    private static final String TAG = "AirTouchTccClient";
    private static HttpClientRequest mTccClient;
    private static final String REQUEST_SESSION = "session";
    private static final String REQUEST_REGISTER_ACCOUNT = "userAccounts";
    private static final String REQUEST_LOCATION_EMOTION = "locationEmotion?locationId=%1$d&periodType=%2$d";
    private static final String REQUEST_SWAP_LOCATION = "locations/editLocation?locationId=%1$d";
    private static final String REQUEST_DELETE_LOCATION = "locations?locationId=%1$d";
    private static final String REQUEST_ADD_LOCATION = "locations?userId=%1$s";
    private static final String REQUEST_GET_LOCATION = "locations?userId=%1$s&allData=true";
    private static final String LOG_OUT = "session";
    private static final String UPDATE_PASSWORD = "passwordUpdate";
    private static final String CHANGE_PASSWORD = "userAccounts/%1$s/passwordChange";
    private static final String REQUEST_SMS_VALID = "userAccounts/smsValid";
    private static final String VERIFY_SMS_VALID = "userAccounts/smsValid?phoneNum=%1$s&validNo=%2$s";
    private static final String GET_LOCATION = "locations?userId=%1$s&allData=true";
    private static final String ADD_LOCATION = "locations?userId=%1$s";
    private static final String ADD_DEVICE = "devices?locationId=%1$d";
    private static final String COMM_TASK = "commTasks?commTaskId=%1$d";
    private static final String CHECK_MAC = "gateways/AliveStatus?macId=%1$s";
    private static final String CONTROL_DEVICE = "devices/%1$d/AirCleaner/Control";
    private static final String GET_DEVICE_STATUS = "devices/%1$d/AirCleaner/RunStatus";
    private static final String GET_CAPABILITY = "devices/%1$d/AirCleaner/Capability";
    private static final String GET_HOME_PM25 = "locations/%1$d/AirCleaner/PM25";
    private static final String DELETE_DEVICE = "devices/%1$d";
    private static final String CLEAN_TIME = "AirCleaner/%1$d/BackHome";
    private String mBaseLocalUrl;

    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECT_TIMEOUT = 3000;

    public static HttpClientRequest sharedInstance() {
        if (null == mTccClient) {
            mTccClient = new HttpClientRequest();
        }
        return mTccClient;
    }

    public HttpClientRequest() {
        mBaseLocalUrl = AppConfig.isDebugMode ? "https://qaweb.chinacloudapp.cn/WebAPI/api/"
                : "https://mservice.honeywell.com.cn/WebAPI/api/";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    public ResponseResult userRegister(UserRegisterRequest request, IActivityReceive
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_REGISTER_ACCOUNT), null, RequestID.USER_REGISTER, request);
        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.getRegsterResponse(response);
    }

    public ResponseResult userLogin(UserLoginRequest request, IActivityReceive
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_SESSION), null, RequestID.USER_LOGIN, request);

        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.parseUserLoginResponse(response, request);
    }

    public ResponseResult checkMac(String macId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(CHECK_MAC, macId), sessionId, RequestID.CHECK_MAC, request);

        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.parseCheckMacResponse(response, RequestID.CHECK_MAC);
    }

    public ResponseResult emotionBottle(int locationId, int periodType, String sessionId, UserRegisterRequest request, IActivityReceive
            receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_LOCATION_EMOTION, locationId, periodType), sessionId, RequestID.EMOTION_BOTTLE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseBottleResponse(response, RequestID.EMOTION_BOTTLE);
    }

    public ResponseResult swapLocationName(int locationId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.PUT, getLocalUrl(REQUEST_SWAP_LOCATION, locationId), sessionId,
                    RequestID.SWAP_LOCATION, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCommonResponse(response, RequestID.SWAP_LOCATION);
    }

    public ResponseResult deleteLocation(int locationId, String sessionId, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.DELETE, getLocalUrl(REQUEST_DELETE_LOCATION, locationId), sessionId,
                    RequestID.DELETE_LOCATION, null);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCommonResponse(response, RequestID.DELETE_LOCATION);
    }

    public ResponseResult addLocation(String userId, String sessionId, IRequestParams
                                      request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_ADD_LOCATION, userId), sessionId,
                    RequestID.ADD_LOCATION, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseAddLocationResponse(response, RequestID.ADD_LOCATION);
    }

    public ResponseResult addDevice(int locationId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(ADD_DEVICE, locationId), sessionId,
                    RequestID.ADD_DEVICE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseAddDeviceResponse(response, RequestID.ADD_DEVICE);
    }

    public ResponseResult getCommTask(int taskId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(COMM_TASK, taskId), sessionId,
                    RequestID.COMM_TASK, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCommTaskResponse(response, RequestID.ADD_DEVICE);
    }

    public ResponseResult getLocation(String userId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_GET_LOCATION, userId), sessionId,
                    RequestID.GET_LOCATION, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGetLocationResponse(response, RequestID.GET_LOCATION);
    }

}