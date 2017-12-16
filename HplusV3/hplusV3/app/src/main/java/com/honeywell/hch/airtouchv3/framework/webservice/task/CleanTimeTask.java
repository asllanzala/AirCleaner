package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/8/31.
 */
public class CleanTimeTask extends BaseRequestTask {
    private int mLocationId;
    private String mSessionId;
    private String mUserId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public CleanTimeTask(int locationId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
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
                    .cleanTime(mLocationId, mSessionId,
                            mRequestParams, mIReceiveResponse);

            if (addResult != null && addResult.isResult()){
                AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
                //get location
                ResponseResult getLocationResult = HttpProxy.getInstance().getWebService().getLocation(authorizeApp.getUserID(), authorizeApp.getSessionId(), null, null);
                if (getLocationResult != null && getLocationResult.isResult()){
                    reloadDeviceInfo();
                }
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
