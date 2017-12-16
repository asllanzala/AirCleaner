package com.honeywell.hch.airtouchv3.framework.webservice;

import android.util.Log;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseParseManager;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

/**
 * Created by Jin Qian on 1/22/15
 */
public class HttpWebService extends HTTPClient implements IWebService {
    private static final String TAG = "AirTouchTccClient";
    private static final String REQUEST_SESSION = "session";
    private static final String REQUEST_REGISTER_ACCOUNT = "userAccounts";
    private static final String REQUEST_LOCATION_EMOTION = "locationEmotion?locationId=%1$d&periodType=%2$d";
    private static final String REQUEST_SWAP_LOCATION = "locations/editLocation?locationId=%1$d";
    private static final String REQUEST_DELETE_LOCATION = "locations?locationId=%1$d";
    private static final String REQUEST_ADD_LOCATION = "locations?userId=%1$s";
    private static final String REQUEST_GET_LOCATION = "locations?userId=%1$s&allData=true";
    private static final String REQUEST_CREATE_GROUP =
            "Grouping/CreateGroup?groupName=%1$s&masterDeviceId=%2$d&locationId=%3$d";
    private static final String REQUEST_DELETE_GROUP = "Grouping/DeleteGroup?groupId=%1$d";
    private static final String REQUEST_ADD_DEVICE_TO_GROUP = "Grouping/AddDeviceIntoGroup?groupId=%1$d";
    private static final String REQUEST_DELETE_DEVICE_FROM_GROUP = "Grouping/DeleteDeviceFromGroup?groupId=%1$d";
    private static final String REQUEST_UPDATE_GROUP_NAME =
            "Grouping/UpdateGroupName?groupNewName=%1$s&groupId=%2$d";
    private static final String REQUEST_GET_GROUP_BY_GROUP_ID = "Grouping/GetGroupByGroupId?groupId=%1$d";
    private static final String REQUEST_GET_GROUP_BY_LOCATION_ID = "Grouping/GetGroupByLocationId?locationId=%1$d";
    private static final String REQUEST_IS_DEVICE_MASTER = "Grouping/IsDeviceMasterDevice?deviceId=%1$d";
    private static final String REQUEST_SEND_SCENARIO_TO_GROUP = "Grouping/GroupControl?groupId=%1$d";
    private static final String REQUEST_MULTI_COMM_TASK = "commTasks";


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
    private static final String CHECK_ENROLL_TYPE = "GetEnrollMode?deviceType=%1s";

    private String mBaseLocalUrl;

    private static final int READ_TIMEOUT = 15;
    private static final int CONNECT_TIMEOUT = 15;


    public HttpWebService() {
//        mBaseLocalUrl = AppConfig.isDebugMode ? "https://qaweb.chinacloudapp.cn/WebAPI/api/"
//                : "https://mservice.honeywell.com.cn/WebAPI/api/";
//        mBaseLocalUrl = AppConfig.isDebugMode ? "https://stweb.chinacloudapp.cn/WebAPI/api/"
//                : "https://mservice.honeywell.com.cn/WebAPI/api/";
        mBaseLocalUrl = "https://mservice.honeywell.com.cn/WebAPI/api/";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    @Override
    public ResponseResult userRegister(UserRegisterRequest request, IActivityReceive
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_REGISTER_ACCOUNT), null, RequestID
                .USER_REGISTER, request);
        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.getRegsterResponse(response);
    }

    @Override
    public ResponseResult userLogin(UserLoginRequest request, IActivityReceive
            receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_SESSION), null, RequestID.USER_LOGIN, request);

        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.parseUserLoginResponse(response, RequestID.USER_LOGIN);
    }

    @Override
    public ResponseResult checkMac(String macId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(CHECK_MAC, macId), sessionId, RequestID.CHECK_MAC, request);

        HTTPRequestResponse response = executeMethodHTTPRequest(httpRequestParams,
                receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        return ResponseParseManager.parseCheckMacResponse(response, RequestID.CHECK_MAC);
    }

    @Override
    public ResponseResult emotionBottle(int locationId, int periodType, String sessionId,
            UserRegisterRequest request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_LOCATION_EMOTION, locationId,
                    periodType), sessionId, RequestID.EMOTION_BOTTLE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseBottleResponse(response, RequestID.EMOTION_BOTTLE);
    }

    @Override
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

    @Override
    public ResponseResult deleteLocation(int locationId, String sessionId, IActivityReceive
            receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.DELETE, getLocalUrl(REQUEST_DELETE_LOCATION, locationId),
                    sessionId, RequestID.DELETE_LOCATION, null);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCommonResponse(response, RequestID.DELETE_LOCATION);
    }

    @Override
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

    @Override
    public ResponseResult addDevice(int locationId, String sessionId, IRequestParams request,
                                    IActivityReceive receiveResponse) {
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

    @Override
    public ResponseResult deleteDevice(int deviceId, String sessionId, IRequestParams request,
                                    IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.DELETE, getLocalUrl(DELETE_DEVICE, deviceId), sessionId,
                    RequestID.DELETE_DEVICE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseAddDeviceResponse(response, RequestID.DELETE_DEVICE);
    }

    @Override
    public ResponseResult getCommTask(int taskId, String sessionId, IRequestParams request,
                                      IActivityReceive receiveResponse) {
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

        return ResponseParseManager.parseCommTaskResponse(response, RequestID.COMM_TASK);
    }

    @Override
    public ResponseResult getLocation(String userId, String sessionId, IRequestParams request,
                                      IActivityReceive receiveResponse) {
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

    @Override
    public ResponseResult getDeviceCapability(int deviceId, String sessionId){
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(GET_CAPABILITY, deviceId), sessionId, RequestID
                    .GET_DEVICE_CAPABILITY, null);
            response = executeMethodHTTPRequest(httpRequestParams,
                    null, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }
        return ResponseParseManager.parseGetDeviceCapabilityResponse(response, RequestID.GET_DEVICE_CAPABILITY);
    }

    @Override
    public ResponseResult getDeviceRunStatus(int deviceId, String sessionId){
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(GET_DEVICE_STATUS, deviceId), sessionId, RequestID
                    .GET_DEVICE_STATUS, null);
            response = executeMethodHTTPRequest(httpRequestParams,
                    null, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }
        return ResponseParseManager.parseGetDeviceRunStatusResponse(response, RequestID.GET_DEVICE_CAPABILITY);
    }

    @Override
    public ResponseResult createGroup(String sessionId, String groupName, int masterDeviceId, int locationId,
                                      IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_CREATE_GROUP, groupName, masterDeviceId, locationId),
                    sessionId, RequestID.CREATE_GROUP, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCreateGroupResponse(response, RequestID.CREATE_GROUP);
    }

    @Override
    public ResponseResult deleteGroup(String sessionId, int groupId,
                                      IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_DELETE_GROUP, groupId),
                    sessionId, RequestID.DELETE_GROUP, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGroupResponse(response, RequestID.DELETE_GROUP);
    }

    @Override
    public ResponseResult addDeviceToGroup(String sessionId, int groupId,
                                      IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_ADD_DEVICE_TO_GROUP, groupId),
                    sessionId, RequestID.ADD_DEVICE_TO_GROUP, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGroupResponse(response, RequestID.ADD_DEVICE_TO_GROUP);
    }

    @Override
    public ResponseResult deleteDeviceFromGroup(String sessionId, int groupId,
                                           IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_DELETE_DEVICE_FROM_GROUP, groupId),
                    sessionId, RequestID.DELETE_DEVICE_FROM_GROUP, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGroupResponse(response, RequestID.DELETE_DEVICE_FROM_GROUP);
    }

    @Override
    public ResponseResult updateGroupName(String sessionId, String groupName, int groupId,
                                                IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_UPDATE_GROUP_NAME, groupName, groupId),
                    sessionId, RequestID.UPDATE_GROUP_NAME, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGroupResponse(response, RequestID.UPDATE_GROUP_NAME);
    }

    @Override
    public ResponseResult getGroupByGroupId(String sessionId, int groupId,
                                          IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_GET_GROUP_BY_GROUP_ID, groupId),
                    sessionId, RequestID.GET_GROUP_BY_GROUP_ID, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCommonResponse(response, RequestID.GET_GROUP_BY_GROUP_ID);
    }

    @Override
    public ResponseResult isDeviceMaster(String sessionId, int deviceId,
                                      IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_IS_DEVICE_MASTER, deviceId),
                    sessionId, RequestID.IS_DEVICE_MASTER, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseIsMasterResponse(response, RequestID.IS_DEVICE_MASTER);
    }

    @Override
    public ResponseResult getGroupByLocationId(String sessionId, int locationId,
                                            IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(REQUEST_GET_GROUP_BY_LOCATION_ID, locationId),
                    sessionId, RequestID.GET_GROUP_BY_LOCATION_ID, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGetGroupByLocationIdResponse(response, RequestID.GET_GROUP_BY_LOCATION_ID);
    }

    @Override
    public ResponseResult sendScenarioToGroup(String sessionId, int groupId,
                                               IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_SEND_SCENARIO_TO_GROUP, groupId),
                    sessionId, RequestID.SEND_SCENARIO_TO_GROUP, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseScenarioResponse(response, RequestID.SEND_SCENARIO_TO_GROUP);
    }

    @Override
    public ResponseResult multiCommTask(String sessionId,
                                              IRequestParams request, IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.POST, getLocalUrl(REQUEST_MULTI_COMM_TASK),
                    sessionId, RequestID.MULTI_COMM_TASK, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseMultiCommTaskResponse(response, RequestID.MULTI_COMM_TASK);
    }

    @Override
    public ResponseResult cleanTime(int locationId, String sessionId, IRequestParams request,
                                    IActivityReceive receiveResponse){
        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.PUT, getLocalUrl(CLEAN_TIME, locationId), sessionId, RequestID.CLEAN_TIME, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    null, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseCleanTime(response, RequestID.CLEAN_TIME);
    }

    @Override
    public ResponseResult turnOnDevice(int deviceId, String sessionId, IRequestParams request,
                                       IActivityReceive receiveResponse){

        HTTPRequestResponse response = null;
        try {
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.PUT, getLocalUrl(CONTROL_DEVICE, deviceId), sessionId, RequestID
                    .CONTROL_DEVICE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    null, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }
        return ResponseParseManager.parseTurnOnDevie(response, RequestID.CLEAN_TIME);
    }

    @Override
    public ResponseResult checkEnrollmentStyle(String deviceType, String sessionId, IRequestParams request,
                                    IActivityReceive receiveResponse) {
        HTTPRequestResponse response = null;
        try {
            Log.i("SmartLinkEnroll","checkEnrollmentStyle");
            Log.i("SmartLinkEnroll","RUL: " +getLocalUrl(CHECK_ENROLL_TYPE, deviceType));
            HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                    .RequestType.GET, getLocalUrl(CHECK_ENROLL_TYPE, deviceType), sessionId,
                    RequestID.GET_ENROLL_TYPE, request);
            response = executeMethodHTTPRequest(httpRequestParams,
                    receiveResponse, CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Exception:" + e.toString());
        }

        return ResponseParseManager.parseGetEnrollTypeResponse(response, RequestID.GET_ENROLL_TYPE);
    }
}