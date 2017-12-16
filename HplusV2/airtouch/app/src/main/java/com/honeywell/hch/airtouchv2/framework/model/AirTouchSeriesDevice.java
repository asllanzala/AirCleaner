package com.honeywell.hch.airtouchv2.framework.model;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

/**
 * Created by wuyuan on 7/30/15.
 * the abstract class for the series of Air Touch, like AirTouch S, AirTouch 450.
 * or AirPrim
 * by Stephen(H127856)
 * data model reconstruction
 */
public class AirTouchSeriesDevice extends HomeDevice
{
    private static final String TAG = "AirTouchSeriesDevice";

    private RunStatus mDeviceRunStatus;

    IReceiveResponse mReceiveResponse = new IReceiveResponse()
    {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            mDeviceRunStatus = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);
                        }
                    } else {
                        LogUtil.log(LogUtil.LogLevel.ERROR,TAG,"GET_DEVICE_STATUS ERROR device id = " + mDeviceInfo
                                .getDeviceID() +
                                ", response status code = " + httpRequestResponse.getStatusCode() + " , " +
                                "error msg = " + httpRequestResponse.getException());
                    }
                    notifyRefreshEnd.notifyDataRefreshEnd();
                    break;
            }
        }
    };

    public RunStatus getDeviceRunStatus() {
        return mDeviceRunStatus;
    }

    public void setDeviceRunStatus(RunStatus mDeviceRunStatus) {
        this.mDeviceRunStatus = mDeviceRunStatus;
    }

    @Override
    public void loadData(IRefreshEnd iRefreshEnd){
        notifyRefreshEnd = iRefreshEnd;
        String sessionId = AuthorizeApp.shareInstance().getSessionId();
        TccClient.sharedInstance().getDeviceStatus(mDeviceInfo.getDeviceID(), sessionId,
                mReceiveResponse);
    }

    @Override
    public void refreshData(){}


}
