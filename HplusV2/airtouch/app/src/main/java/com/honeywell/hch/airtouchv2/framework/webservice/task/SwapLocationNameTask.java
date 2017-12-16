package com.honeywell.hch.airtouchv2.framework.webservice.task;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/7/2.
 */
public class SwapLocationNameTask extends BaseRequestTask {
    private int mLocationId;
    private String mUserId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public SwapLocationNameTask(int locationId, IRequestParams requestParams, IActivityReceive
            iReceiveResponse) {
        this.mLocationId = locationId;
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;

        this.mUserId = AuthorizeApp.shareInstance().getUserID();
        this.mSessionId = AuthorizeApp.shareInstance().getSessionId();
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult swapResult = HttpClientRequest.sharedInstance()
                    .swapLocationName(mLocationId, mSessionId ,
                            mRequestParams, mIReceiveResponse);
            if (swapResult.isResult()) {
                HttpClientRequest.sharedInstance().getLocation(mUserId, mSessionId, null, null);
            }

            return swapResult;
        }
        return  new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.SWAP_LOCATION);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
