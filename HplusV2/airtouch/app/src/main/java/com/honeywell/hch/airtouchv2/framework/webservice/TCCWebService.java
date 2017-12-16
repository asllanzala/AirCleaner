package com.honeywell.hch.airtouchv2.framework.webservice;

import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.BackHomeRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.DeviceControlRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.AddLocationRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.ChangePasswordRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.DeviceRegisterRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.SmsValidRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UpdatePasswordRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;

/**
 * Created by lynnliu on 9/15/15.
 */
public interface TCCWebService {

    public int userRegister(UserRegisterRequest request, IReceiveResponse receiveResponse);

    public int updatePassword(UpdatePasswordRequest request, IReceiveResponse receiveResponse);

    public int changePassword(String userId, String sessionId, ChangePasswordRequest request,
                              IReceiveResponse receiveResponse);

    public int getSmsCode(SmsValidRequest request, IReceiveResponse receiveResponse);

    public int verifySmsCode(String phoneNum, String smsCode, SmsValidRequest request,
                             IReceiveResponse receiveResponse);

    public int userLogin(UserLoginRequest request, IReceiveResponse receiveResponse);

    public int userLogout(String sessionId, UserLoginRequest request,
                          IReceiveResponse receiveResponse);

    public int getHomePm25(int locationId, String sessionId, IReceiveResponse receiveResponse);

    public int getLocation(String userId, String sessionId, IReceiveResponse receiveResponse);

    public int addLocation(String userId, String sessionId, AddLocationRequest request,
                           IReceiveResponse receiveResponse);

    public int addDevice(int locationId, String sessionId, DeviceRegisterRequest request,
                         IReceiveResponse receiveResponse);

    public int controlDevice(int deviceId, String sessionId, DeviceControlRequest request,
                             IReceiveResponse receiveResponse);

    public int getDeviceStatus(int deviceId, String sessionId, IReceiveResponse receiveResponse);

    public int getDeviceCapability(int deviceId, String sessionId, IReceiveResponse
            receiveResponse);

    public int checkMac(String macId, String sessionId, IReceiveResponse receiveResponse);

    public int updateSession(String sessionId, IReceiveResponse receiveResponse);

    public int getCommTask(int taskId, String sessionId, IReceiveResponse receiveResponse);

    public int deleteDevice(int deviceId, String sessionId, IReceiveResponse receiveResponse);

    public int cleanTime(int locationId, String sessionId, BackHomeRequest request,
                         IReceiveResponse receiveResponse);
}
