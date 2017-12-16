package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.DeviceControlRequest;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

import java.util.List;

/**
 * Created by Jin Qian on 15/8/31.
 */
public class TurnOnAllDeviceTask extends BaseRequestTask {

    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;
    private List<Integer> mDeviceIdList;

    public TurnOnAllDeviceTask(List<Integer> deviceIdList, String mCmmdStr, IActivityReceive iReceiveResponse) {
        DeviceControlRequest deviceControlRequest = new DeviceControlRequest();
        deviceControlRequest.setFanModeSwitch(mCmmdStr);
        this.mRequestParams = deviceControlRequest;
        this.mIReceiveResponse = iReceiveResponse;
        mDeviceIdList = deviceIdList;
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();

    }


    @Override
    protected ResponseResult doInBackground(Object... params) {

        if (mDeviceIdList != null || mDeviceIdList.size() ==0){
            new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.CONTROL_DEVICE);
        }

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            for (int i = 0; i < mDeviceIdList.size(); i++){
                HttpProxy.getInstance().getWebService().turnOnDevice(mDeviceIdList.get(i),mSessionId,mRequestParams,null);
            }
        }
        return  new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.CONTROL_DEVICE);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
