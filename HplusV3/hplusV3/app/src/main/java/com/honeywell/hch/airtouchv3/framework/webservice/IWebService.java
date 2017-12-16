package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

/**
 * Created by lynnliu on 9/15/15.
 */
public interface IWebService {

    public ResponseResult userRegister(UserRegisterRequest request, IActivityReceive
            receiveResponse);

    public ResponseResult userLogin(UserLoginRequest request, IActivityReceive
            receiveResponse);

    public ResponseResult checkMac(String macId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse);

    public ResponseResult emotionBottle(int locationId, int periodType, String sessionId,
            UserRegisterRequest request, IActivityReceive receiveResponse);

    public ResponseResult swapLocationName(int locationId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse);

    public ResponseResult deleteLocation(int locationId, String sessionId, IActivityReceive
            receiveResponse);

    public ResponseResult addLocation(String userId, String sessionId, IRequestParams
            request, IActivityReceive receiveResponse);

    public ResponseResult addDevice(int locationId, String sessionId, IRequestParams request,
                                    IActivityReceive receiveResponse);

    public ResponseResult deleteDevice(int deviceId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse);

    public ResponseResult getCommTask(int taskId, String sessionId, IRequestParams request,
                                      IActivityReceive receiveResponse);

    public ResponseResult getLocation(String userId, String sessionId, IRequestParams request,
                                      IActivityReceive receiveResponse);

    public ResponseResult getDeviceCapability(int deviceId, String sessionId);

    public ResponseResult getDeviceRunStatus(int deviceId, String sessionId);

    public ResponseResult createGroup(String sessionId, String groupName, int masterDeviceId, int locationId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult deleteGroup(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult addDeviceToGroup(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult deleteDeviceFromGroup(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult updateGroupName(String sessionId, String groupName, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult getGroupByGroupId(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult isDeviceMaster(String sessionId, int deviceId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult getGroupByLocationId(String sessionId, int locationId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult sendScenarioToGroup(String sessionId, int groupId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult multiCommTask(String sessionId,
            IRequestParams request, IActivityReceive receiveResponse);

    public ResponseResult turnOnDevice(int deviceId, String sessionId, IRequestParams request,
            IActivityReceive receiveResponse);

    public ResponseResult cleanTime(int locationId, String sessionId,IRequestParams request,
                                    IActivityReceive receiveResponse);
    public ResponseResult checkEnrollmentStyle(String deviceType, String sessionId, IRequestParams request,
                                               IActivityReceive receiveResponse);
}