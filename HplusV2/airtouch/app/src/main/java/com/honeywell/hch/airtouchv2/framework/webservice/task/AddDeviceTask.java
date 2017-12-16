package com.honeywell.hch.airtouchv2.framework.webservice.task;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/8/31.
 */
public class AddDeviceTask extends BaseRequestTask {
    private int mLocationId;
    private String mSessionId;
    private String mUserId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public AddDeviceTask(int locationId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;

        mLocationId = locationId;
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        mUserId =  AuthorizeApp.shareInstance().getUserID();
    }


    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult addResult = HttpClientRequest.sharedInstance()
                    .addDevice(mLocationId, mSessionId,
                            mRequestParams, mIReceiveResponse);

            if (addResult.isResult()) {
                HttpClientRequest.sharedInstance().getLocation(mUserId, mSessionId, null, mIReceiveResponse);
                reloadDeviceInfo();
            }
            return addResult;
        }
        return  new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.DELETE_DEVICE);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
