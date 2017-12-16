package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice;

import android.os.Bundle;

import com.honeywell.hch.airtouchv3.app.dashboard.model.CreateGroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DeviceListRequest;
import com.honeywell.hch.airtouchv3.framework.webservice.MockWebService;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

import junit.framework.Assert;

/**
 * Created by wuyuan on 1/8/16.
 */
public class CreateGroupMethodTest extends BaseGroupManagerFunctionTest{

    private GroupManager mGroupManager;
    private MockWebService mWebService;


    private RequestID mRequestId;
    private boolean mResult;
    private int mResponseCode;

    private ResponseResult mResponseResult;
    private Bundle mBundle;
    private String mGroupName = "A";
    private int masterDeviceId = 2;
    private int locationId = 3;
    private int[] ids = new int[]{3,2,1};
    private DeviceListRequest request = new DeviceListRequest(ids);

    public CreateGroupMethodTest(GroupManager groupManager, MockWebService webService){
        mGroupManager = groupManager;
        mWebService = webService;
    }

    public void testSuccessCreateGroup(){

        mResult = true;
        mResponseCode = 200;
        mRequestId = RequestID.CREATE_GROUP;
        mResponseResult = new ResponseResult(mResult, mResponseCode, "", mRequestId);
        mBundle = new Bundle();
        mBundle.putInt(CreateGroupResponse.CODE_ID, mResponseCode);
        mResponseResult.setResponseData(mBundle);

        //test login success
        setReloginSuccess();
        mGroupManager.response = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                Assert.assertEquals(mRequestId, responseResult.getRequestId());
                Assert.assertEquals(mResult, responseResult.isResult());
                Assert.assertEquals(mResponseCode, responseResult.getResponseCode());
            }
        };
        mWebService.setResponseResult(mResponseResult);
        mGroupManager.createGroup(mGroupName, masterDeviceId, locationId, request);

    }


    public void testFailCreateGroup(){

        mResult = false;
        mResponseCode = 200;
        mRequestId = RequestID.CREATE_GROUP;
        ResponseResult responseResult = new ResponseResult(mResult, mResponseCode, "", mRequestId);

        //test login success
        setReloginSuccess();
        mGroupManager.response = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                Assert.assertEquals(false, responseResult.isResult());
            }
        };
        mWebService.setResponseResult(responseResult);
        mGroupManager.createGroup(mGroupName, masterDeviceId, locationId, request);

    }

    public void testLoginFailWhenCreateGroup(){

        ResponseResult responseResult = new ResponseResult(mResult, mResponseCode, "", mRequestId);

        //test login success
        setReloginFail();
        mGroupManager.response = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                Assert.assertEquals(false, responseResult.isResult());

            }
        };
        mWebService.setResponseResult(responseResult);
        mGroupManager.createGroup(mGroupName, masterDeviceId, locationId, request);

    }
}
