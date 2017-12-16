package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.content.Intent;
import android.os.AsyncTask;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

import java.util.HashMap;
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


    protected  boolean isRunning = false;

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
        ResponseResult result =  new ResponseResult(false, RequestID.USER_LOGIN);
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (authorizeApp.isLoginSuccess()) {
            timeDeviation = System.currentTimeMillis()
                    - authorizeApp.getSessionLastUpdated();
            timeDeviation /= 1000;
            if ((timeDeviation > SESSION_TIMEOUT)
                    && (authorizeApp.getSessionLastUpdated() != 0)) {
                authorizeApp.setSessionLastUpdated(System.currentTimeMillis());

                //relogin
                if (!authorizeApp.isLoginSuccess()) {
                    authorizeApp.setIsAutoLoginOngoing(true);
                }
                UserLoginRequest userLoginRequest = new UserLoginRequest(authorizeApp.getMobilePhone(), authorizeApp.getPassword(),
                        AppConfig.APPLICATION_ID);
                authorizeApp.setSessionLastUpdated(System.currentTimeMillis());

                return HttpProxy.getInstance().getWebService().userLogin(userLoginRequest,null);
            }
            else{
                result.setResult(true);
                return result;
            }

        }
        return result;
    }

    /*
     * Combine RunStatus into device and send broadcast to main page
     */
    public boolean reloadDeviceInfo(){
        List<UserLocationData> locations = AppManager.shareInstance().getUserLocationDataList();
        HashMap<String, WeatherPageData> weatherDataHashMap = AppManager.shareInstance()
                .getWeatherPageDataHashMap();
        if (locations != null && locations.size() > 0){
            for (int i = 0; i < locations.size(); i++) {
                final UserLocationData locationItem = locations.get(i);
                locationItem.setCityWeatherData(weatherDataHashMap.get(locationItem.getCity()));

                for (HomeDevice homeDevice : locationItem.getAirTouchSeriesList()){

                    if (homeDevice instanceof AirTouchSeriesDevice){
                        ResponseResult runstatusData = HttpProxy.getInstance().getWebService().getDeviceRunStatus(
                                homeDevice.getDeviceInfo().getDeviceID(),
                                AppManager.shareInstance().getAuthorizeApp().getSessionId());

                        RunStatus runstatusResponse = locationItem.getRunStatusWhenReturnNull();
                        if (runstatusData != null && runstatusData.getResponseData() != null){
                            runstatusResponse = (RunStatus)runstatusData.getResponseData().getSerializable(AirTouchConstants.DEVICE_RUNSTATUS_KEY);
                        }
                        ((AirTouchSeriesDevice)homeDevice).setDeviceRunStatus(runstatusResponse);
                    }

                }
            }
        }
        sendUpdateBroadcastAfterRefresh();

        return true;
    }

    private void sendUpdateBroadcastAfterRefresh(){
        Intent boradIntent = new Intent(AirTouchConstants.HOME_CHANGED);
        HPlusApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);
    }


    public boolean isRunning(){
        return isRunning;
    }

}
