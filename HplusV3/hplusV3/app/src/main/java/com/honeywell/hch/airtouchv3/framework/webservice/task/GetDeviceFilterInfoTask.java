package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by wuyuan on 9/25/15.
 */
public class GetDeviceFilterInfoTask extends BaseRequestTask {

    private IActivityReceive mIReceiveResponse;

    private int mDeviceId;

    private String mSessionId;

    private RunStatus mRunstatus;

    public GetDeviceFilterInfoTask(String sessionId, int deviceId, RunStatus runStatus, IActivityReceive iReceiveResponse) {

        this.mDeviceId = deviceId;
        this.mSessionId = sessionId;
        this.mIReceiveResponse = iReceiveResponse;
        mRunstatus = runStatus;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reloginResult = reloginSuccessOrNot();
        if (reloginResult.isResult()) {
            if (mRunstatus == null) {
                ResponseResult runstatusResponse = HttpProxy.getInstance().getWebService().getDeviceRunStatus(mDeviceId, mSessionId);
                if (runstatusResponse != null && runstatusResponse.getResponseData() != null) {
                    mRunstatus = (RunStatus) runstatusResponse.getResponseData().getSerializable(AirTouchConstants.DEVICE_RUNSTATUS_KEY);
                }
                // if get runstatus failed.
                if (mRunstatus == null) {
                    return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.GET_DEVICE_CAPABILITY);
                }
            }
            return HttpProxy.getInstance().getWebService().getDeviceCapability(mDeviceId, mSessionId);


        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.GET_DEVICE_CAPABILITY);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
