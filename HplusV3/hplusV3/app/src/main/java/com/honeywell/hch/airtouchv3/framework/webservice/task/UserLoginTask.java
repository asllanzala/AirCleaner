package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.content.Intent;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

/**
 * Created by wuyuan on 15/5/19.
 */
public class UserLoginTask extends BaseRequestTask
{
    private IActivityReceive mIReceiveResponse;
    private IRequestParams requestParams;

    public UserLoginTask(IActivityReceive iReceiveResponse, IRequestParams requestParams)
    {
        this.mIReceiveResponse = iReceiveResponse;
        this.requestParams = requestParams;
    }
    @Override
    protected ResponseResult doInBackground(Object... params)
    {
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        UserLoginRequest userLoginRequest = new UserLoginRequest(((UserLoginRequest)requestParams).getUsername(), ((UserLoginRequest)requestParams).getPassword(),
                AppConfig.APPLICATION_ID);
       ResponseResult loginResult = HttpProxy.getInstance().getWebService().userLogin(userLoginRequest, null);
       if(!loginResult.isResult()){
           return loginResult;
       }
        //get location
        ResponseResult getLocationResult = HttpProxy.getInstance().getWebService().getLocation(authorizeApp.getUserID(), authorizeApp.getSessionId(), null, null);
        if (getLocationResult.isResult()){
            reloadDeviceInfo();
        }
        return getLocationResult;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        AppManager.shareInstance().getAuthorizeApp().setIsAutoLoginOngoing(false);
        Intent boradIntent = new Intent(AirTouchConstants.AFTER_USER_LOGIN);
        HPlusApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);

        if (mIReceiveResponse != null)
        {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
