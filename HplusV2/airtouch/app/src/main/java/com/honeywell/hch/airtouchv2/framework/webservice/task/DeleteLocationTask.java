package com.honeywell.hch.airtouchv2.framework.webservice.task;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/7/2.
 */
public class DeleteLocationTask extends BaseRequestTask {
    private int mLocationId;
    private String mUserId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;

    public DeleteLocationTask(int locationId, IActivityReceive iReceiveResponse) {
        this.mLocationId = locationId;
        this.mIReceiveResponse = iReceiveResponse;

        mUserId = AuthorizeApp.shareInstance().getUserID();
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult deleteResult = HttpClientRequest.sharedInstance()
                    .deleteLocation(mLocationId, mSessionId, mIReceiveResponse);

            if (deleteResult.isResult()) {
                HttpClientRequest.sharedInstance().getLocation(mUserId, mSessionId, null, null);
                reloadDeviceInfo();
            }

            return deleteResult;
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.DELETE_DEVICE);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
