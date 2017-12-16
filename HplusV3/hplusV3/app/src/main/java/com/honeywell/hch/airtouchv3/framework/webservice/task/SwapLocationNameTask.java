package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.SwapLocationRequest;
import java.util.List;

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

        this.mUserId = AppManager.shareInstance().getAuthorizeApp().getUserID();
        this.mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult swapResult = HttpProxy.getInstance().getWebService()
                    .swapLocationName(mLocationId, mSessionId ,
                            mRequestParams, mIReceiveResponse);
            if (swapResult.isResult()) {
//                HttpWebService.sharedInstance().getLocation(mUserId, mSessionId, null, null);
                List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
                for (UserLocationData userLocationDataItem: userLocationDataList){
                    if (userLocationDataItem.getLocationID() == mLocationId){
                        userLocationDataItem.setName(((SwapLocationRequest)mRequestParams).getName());
                    }
                }
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
