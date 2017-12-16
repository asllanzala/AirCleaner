package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.content.Intent;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;

/**
 * Created by wuyuan on 13/10/2015.
 * this task is used for refresh the datas that need to refresh frequently
 * like location,device runstatus.
 */
public class ShortTimerRefreshTask extends BaseRequestTask {



    public ShortTimerRefreshTask() {
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {
          isRunning = true;
            ResponseResult reLoginResult = reloginSuccessOrNot();
            if (reLoginResult.isResult()) {
                AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();

                //get location
                ResponseResult getLocationResult = HttpProxy.getInstance().getWebService().getLocation(authorizeApp.getUserID(), authorizeApp.getSessionId(), null, null);
                if (getLocationResult.isResult()) {
                    reloadDeviceInfo();
                }

            }

        return null;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        isRunning = false;
        Intent boradIntent = new Intent(AirTouchConstants.SHORTTIME_REFRESH_END_ACTION);
        HPlusApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);
        super.onPostExecute(responseResult);
    }
}
