package com.honeywell.hch.airtouchv2.framework.webservice.task;

import android.content.Intent;
import android.os.AsyncTask;

import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

import java.util.List;

/**
 * Created by wuyuan on 15/5/19.
 */
public  class BaseRequestTask  extends AsyncTask<Object, Object, ResponseResult>
{

    private Long timeDeviation = 0L;

    private static final int SESSION_TIMEOUT = 15 * 60;

     int mHomeAirTouchSeriesDeviceNumber = 0;
     int  mHomeDeviceTotalNumber = 0;


    public BaseRequestTask(){}

    @Override
    protected ResponseResult doInBackground(Object... params) {
        return null;
    }

    @Override
    protected void onPostExecute(ResponseResult result) {
        super.onPostExecute(result);
    }

    public ResponseResult reloginSuccessOrNot()
    {
        if (AuthorizeApp.shareInstance().isLoginSuccess()) {
            timeDeviation = System.currentTimeMillis()
                    - AuthorizeApp.shareInstance().getSessionLastUpdated();
            timeDeviation /= 1000;
            if ((timeDeviation > SESSION_TIMEOUT)
                    && (AuthorizeApp.shareInstance().getSessionLastUpdated() != 0)) {
                AuthorizeApp.shareInstance().setSessionLastUpdated(System.currentTimeMillis());

                //relogin
                AuthorizeApp user = AuthorizeApp.shareInstance();
                if (!user.isLoginSuccess()) {
                    user.setIsAutoLoginOngoing(true);
                }
                UserLoginRequest userLoginRequest = new UserLoginRequest(user.getMobilePhone(), user.getPassword(),
                        AppConfig.APPLICATION_ID);
                AuthorizeApp.shareInstance().setSessionLastUpdated(System.currentTimeMillis());

                return HttpClientRequest.sharedInstance().userLogin(userLoginRequest,null);
            }

        }
        ResponseResult result =  new ResponseResult(true, RequestID.USER_LOGIN);
        return result;
    }

    /*
     * Combine RunStatus into device and send broadcast to main page
     */
    public boolean reloadDeviceInfo(){
         mHomeAirTouchSeriesDeviceNumber = 0;
         mHomeDeviceTotalNumber = 0;

        List<UserLocation> locations = AuthorizeApp.shareInstance().getUserLocations();
        for (int i = 0; i < locations.size(); i++) {
            mHomeDeviceTotalNumber += locations.get(i).getAirTouchSDeviceNumber();

        }
        if (mHomeDeviceTotalNumber == 0){
            sendUpdateBroadcastAfterRefresh();
            return true;
        }
        for (int i = 0; i < locations.size(); i++) {
            final UserLocation locationItem = locations.get(i);
            // get devices of each home
            locationItem.loadHomeDevicesData(new IRefreshEnd() {
                @Override
                public void notifyDataRefreshEnd() {
                    mHomeAirTouchSeriesDeviceNumber++;

                    if (mHomeAirTouchSeriesDeviceNumber == mHomeDeviceTotalNumber) {
                        sendUpdateBroadcastAfterRefresh();
                    }
                }
            });
        }
        return true;
    }

    private void sendUpdateBroadcastAfterRefresh(){
        Intent intent = new Intent("update_deviceinfo_intent");
        ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);

    }

}
