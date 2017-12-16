package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.CapabilityResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.CreateGroupResponse;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.webservice.MockHTTPClient;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.util.HPlusFileUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by lynnliu on 24/9/15.
 */
@RunWith(RobolectricTestRunner.class)
public class HttpWebServiceTest {

    // result value
    private static final String TEST_REGISTER_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("register_result");
    private static final String TEST_LOGIN_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("login_result");

    private static final String TEST_GET_DEVICE_CAPABILITY_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("device_capability_result");

    private static final String TEST_GET_DEVICE_RUNSTATUS_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("device_runstatus_result");
    private static final String TEST_GET_LOCATION_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("get_location_result");

    private static final String TEST_CREATE_GROUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("create_group");

    private static final String TEST_DELETE_GROUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("delete_group");

    private static final String TEST_ADD_DEVICE_TO_GROUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("add_device_to_group");

    private static final String TEST_DELETE_DEVICE_FROM_GROUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("delete_device_from_group");

    private static final String TEST_UPDATE_GROUP_NAME_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("update_group_name");

    private static final String TEST_DELETE_DEVICE_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("delete_device");

    private static final String TEST_GET_GROUP_BY_GTOUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("get_group_by_groupId");

    private static final String TEST_SEND_SCENARIO_TO_GROUP_RESULT = HPlusFileUtils
            .readFileFromWebTestsAsString("send_scenario_goup");


    // url constant
    private static final String REQUEST_REGISTER_ACCOUNT = "userAccounts";

    public MockHTTPClient mMockHTTPClient;
    private String mBaseLocalUrl = "https://qaweb.chinacloudapp.cn/WebAPI/api/";


    protected HPlusApplication application = Mockito.mock(HPlusApplication.class, Mockito.RETURNS_DEEP_STUBS);

    @Before
    public void setup() {
        mMockHTTPClient = new MockHTTPClient();
        Mockito.when(application.getApplicationContext()).thenReturn(application);
        HPlusApplication.setHPlusApplication(application);

    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    @Test
    public void testUserRegister() {
        mMockHTTPClient.reset();

        UserRegisterRequest request = new UserRegisterRequest("nickname", "password", "telephone", "+86");
        request.setEmail("email");

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.POST, getLocalUrl(REQUEST_REGISTER_ACCOUNT), null, RequestID
                .USER_REGISTER, request);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_REGISTER_RESULT);
        HTTPRequestResponse response = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult userRegisterResponse = ResponseParseManager.getRegsterResponse(response);
        //verify url, status code, result value, balabalabala...
    }

    @Test
    public void testGetDeviceCapability(){
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .GET_DEVICE_CAPABILITY, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_GET_DEVICE_CAPABILITY_RESULT);

        HTTPRequestResponse response = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult deviceCapabilityResponse = ResponseParseManager.parseGetDeviceCapabilityResponse(response, RequestID
                .GET_DEVICE_CAPABILITY);
        Assert.assertEquals(true, deviceCapabilityResponse.isResult());

        CapabilityResponse capabilityResponse = (CapabilityResponse)deviceCapabilityResponse.getResponseData().getSerializable(AirTouchConstants.DEVICE_CAPABILITY_KEY);
        Assert.assertEquals(800, capabilityResponse.getFilter1ExpiredTime());
        Assert.assertEquals(5000, capabilityResponse.getFilter2ExpiredTime());
        Assert.assertEquals(1400, capabilityResponse.getFilter3ExpiredTime());
    }

    @Test
    public void testGetDeviceRunStatus(){
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .GET_DEVICE_STATUS, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_GET_DEVICE_RUNSTATUS_RESULT);

        HTTPRequestResponse response = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult deviceRunstatusResponse = ResponseParseManager.parseGetDeviceRunStatusResponse(response, RequestID
                .GET_DEVICE_STATUS);

        Assert.assertEquals(true, deviceRunstatusResponse.isResult());

        RunStatus runstatusResponse = (RunStatus)deviceRunstatusResponse.getResponseData().getSerializable(AirTouchConstants.DEVICE_RUNSTATUS_KEY);

        Assert.assertEquals(39, runstatusResponse.getmPM25Value());
        Assert.assertEquals("NotRunning", runstatusResponse.getFanSpeedStatus());
        Assert.assertEquals("Off", runstatusResponse.getScenarioMode());
    }

    @Test
    public void testGetLocation(){
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .GET_LOCATION, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_GET_LOCATION_RESULT);

        HTTPRequestResponse response = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult locationResponse = ResponseParseManager.parseGetLocationResponse(response, RequestID
                .GET_LOCATION);

        Assert.assertEquals(true, locationResponse.isResult());
        Assert.assertEquals(4, AppManager.shareInstance().getUserLocationDataList().size());
    }

    @Test
    public void testUserLogin(){
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .USER_LOGIN, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_LOGIN_RESULT);

        HTTPRequestResponse response = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult loginResponse = ResponseParseManager.parseUserLoginResponse(response, RequestID
                .GET_LOCATION);

        Assert.assertEquals(true, loginResponse.isResult());
        Assert.assertEquals("F39AF8EE-4B38-4FB6-B20A-9ECAA2322FCE", AppManager.shareInstance().getAuthorizeApp().getSessionId());
        Assert.assertEquals("7897", AppManager.shareInstance().getAuthorizeApp().getUserID());
        Assert.assertEquals("Jin", AppManager.shareInstance().getAuthorizeApp().getNickname());

    }

    @Test
    public void testCreateGroup(){
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .CREATE_GROUP, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_CREATE_GROUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseCreateGroupResponse(successResponse, RequestID
                .CREATE_GROUP);

        Assert.assertEquals(200, groupResult.getResponseCode());
        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseCreateGroupResponse(failResponse, RequestID
                .CREATE_GROUP);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testDeleteGroup() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .DELETE_GROUP, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_DELETE_GROUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseGroupResponse(successResponse, RequestID
                .DELETE_GROUP);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseGroupResponse(failResponse, RequestID
                .DELETE_GROUP);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testAddDeviceToGroup() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .ADD_DEVICE, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_ADD_DEVICE_TO_GROUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseGroupResponse(successResponse, RequestID
                .ADD_DEVICE);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseGroupResponse(failResponse, RequestID
                .ADD_DEVICE);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testDeleteDeviceFromGroup() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .DELETE_DEVICE_FROM_GROUP, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_DELETE_DEVICE_FROM_GROUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseGroupResponse(successResponse, RequestID
                .DELETE_DEVICE_FROM_GROUP);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseGroupResponse(failResponse, RequestID
                .DELETE_DEVICE_FROM_GROUP);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testUpdateGroupName() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .UPDATE_GROUP_NAME, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_UPDATE_GROUP_NAME_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseGroupResponse(successResponse, RequestID
                .UPDATE_GROUP_NAME);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseGroupResponse(failResponse, RequestID
                .UPDATE_GROUP_NAME);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testIsMasterDevice() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .IS_DEVICE_MASTER, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_DELETE_DEVICE_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseIsMasterResponse(successResponse, RequestID
                .IS_DEVICE_MASTER);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseIsMasterResponse(failResponse, RequestID
                .DELETE_DEVICE);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testDeleteDevice() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .DELETE_DEVICE, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_DELETE_DEVICE_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseAddDeviceResponse(successResponse, RequestID
                .DELETE_DEVICE);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));


        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseAddDeviceResponse(failResponse, RequestID
                .DELETE_DEVICE);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void testSendScenarioToGroup() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .SEND_SCENARIO_TO_GROUP, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_SEND_SCENARIO_TO_GROUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseScenarioResponse(successResponse, RequestID
                .GET_GROUP_BY_GROUP_ID);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));

        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseScenarioResponse(failResponse, RequestID
                .SEND_SCENARIO_TO_GROUP);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }

    @Test
    public void getGroupByGroupId() throws Exception {
        mMockHTTPClient.reset();

        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(mBaseLocalUrl), null, RequestID
                .GET_GROUP_BY_GROUP_ID, null);
        mMockHTTPClient.setHTTPRequestResponseData(TEST_GET_GROUP_BY_GTOUP_RESULT);

        HTTPRequestResponse successResponse = mMockHTTPClient.getHTTPRequestSuccessResponse(httpRequestParams);
        ResponseResult groupResult = ResponseParseManager.parseCommonResponse(successResponse, RequestID
                .GET_GROUP_BY_GROUP_ID);

        Assert.assertEquals(200, groupResult.getResponseCode());
//        Assert.assertEquals(2, groupResult.getResponseData().getInt(CreateGroupResponse.GROUP_ID));

        MockHTTPClient.Error error = new MockHTTPClient.Error(404,"error");
        mMockHTTPClient.setError(error);
        HTTPRequestResponse failResponse = mMockHTTPClient.getHTTPRequestFailureResponse(httpRequestParams);
        ResponseResult failGroupResult = ResponseParseManager.parseCommonResponse(failResponse, RequestID
                .GET_GROUP_BY_GROUP_ID);
        Assert.assertEquals(404, failGroupResult.getResponseCode());
    }
}

