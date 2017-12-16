package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

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

        mUserId = AppManager.shareInstance().getAuthorizeApp().getUserID();
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult deleteResult = HttpProxy.getInstance().getWebService()
                    .deleteLocation(mLocationId, mSessionId, mIReceiveResponse);

            if (deleteResult.isResult()) {
                HttpProxy.getInstance().getWebService().getLocation(mUserId, mSessionId, null, null);
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
