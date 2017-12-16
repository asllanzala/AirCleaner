package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

/**
 * The MockWebService is used to replace HttpWebService when the unit tests are running.
 */
public class MockWebService implements IWebService {

    private ResponseResult mResponseResult;

    public void setResponseResult(ResponseResult result) {
        mResponseResult = result;
    }

    @Override
    public ResponseResult userRegister(UserRegisterRequest request, IActivityReceive
            receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult userLogin(UserLoginRequest request, IActivityReceive
            receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult checkMac(String macId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult emotionBottle(int locationId, int periodType, String sessionId,
            UserRegisterRequest request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult swapLocationName(int locationId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult deleteLocation(int locationId, String sessionId, IActivityReceive
            receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult addLocation(String userId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult addDevice(int locationId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult deleteDevice(int deviceId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult getCommTask(int taskId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult getLocation(String userId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult getDeviceCapability(int deviceId, String sessionId){
        return mResponseResult;
    }

    @Override
    public ResponseResult getDeviceRunStatus(int deviceId, String sessionId){
        return mResponseResult;
    }

    @Override
    public ResponseResult createGroup(String sessionId, String groupName, int masterDeviceId,
            int locationId, IRequestParams request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult deleteGroup(String sessionId, int groupId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult addDeviceToGroup(String sessionId, int groupId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult deleteDeviceFromGroup(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult updateGroupName(String sessionId, String groupName, int groupId,
            IRequestParams request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult getGroupByGroupId(String sessionId, int groupId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult isDeviceMaster(String sessionId, int deviceId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult getGroupByLocationId(String sessionId, int locationId,
            IRequestParams request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult sendScenarioToGroup(String sessionId, int groupId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult multiCommTask(String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult turnOnDevice(int deviceId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse) {
        return mResponseResult;
    }

    @Override
    public ResponseResult cleanTime(int locationId, String sessionId,IRequestParams request,
            IActivityReceive receiveResponse){
        return mResponseResult;
    }

    @Override
    public ResponseResult checkEnrollmentStyle(String deviceType, String sessionId, IRequestParams request, IActivityReceive receiveResponse) {
        return mResponseResult;
    }
}