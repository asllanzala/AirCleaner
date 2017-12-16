package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 10/13/15.
 */
public class GetGroupByLocationIdTask extends BaseRequestTask {
    private String mSessionId;
    private int mLocationId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public GetGroupByLocationIdTask(int locationId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        mLocationId = locationId;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult result = HttpProxy.getInstance().getWebService()
                    .getGroupByLocationId(mSessionId, mLocationId,
                            mRequestParams, mIReceiveResponse);

            return result;
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL,
                "", RequestID.GET_GROUP_BY_LOCATION_ID);
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
