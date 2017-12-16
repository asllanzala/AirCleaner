package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

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
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        mUserId =  AppManager.shareInstance().getAuthorizeApp().getUserID();
    }


    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult addResult = HttpProxy.getInstance().getWebService()
                    .addDevice(mLocationId, mSessionId,
                            mRequestParams, mIReceiveResponse);

//            if (addResult.isResult()) {
//                HttpWebService.sharedInstance().getLocation(mUserId, mSessionId, null, mIReceiveResponse);
//                reloadDeviceInfo();
//            }
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
