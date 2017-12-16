package com.honeywell.hch.airtouchv2.app.airtouch.controller.device;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jin Qian on 3/9/2015.
 */
public class DeviceRunStatusService extends Service {
    private static final String TAG = "AirTouchService";
    private static final int POLLING_GAP = 20 * 1000;
    private boolean isThreadRunning = true;
    private final IBinder binder = new MyBinder();
    private HomeDevice mHomeDevice;

    public class MyBinder extends Binder {
        public DeviceRunStatusService getService() {
            return DeviceRunStatusService.this;
        }
    }
    private String mSessionId;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        mHomeDevice = AuthorizeApp.shareInstance().getCurrentDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRunning) {
                    try {
                        Thread.sleep(POLLING_GAP);
                        TccClient.sharedInstance().getDeviceStatus(mHomeDevice.getDeviceInfo()
                                .getDeviceID(), mSessionId, mReceiveResponse);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isThreadRunning = false;
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RunStatus runStatusResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);

                            if (!isHasNullPoint()) {
                                ((AirTouchSeriesDevice)mHomeDevice).setDeviceRunStatus(runStatusResponse);
                                //send broadcast to MainActivity
                                Intent intent = new Intent("runStatusChanged");
                                sendBroadcast(intent);
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;
            }
        }
    };

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean isHasNullPoint() {
        return mHomeDevice == null || mHomeDevice
                .getDeviceInfo() == null;
    }


}
