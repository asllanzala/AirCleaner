package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.os.Bundle;

import com.honeywell.hch.airtouchv3.app.dashboard.model.CreateGroupResponse;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 10/13/15.
 */
public class CreateGroupTask extends BaseRequestTask {
    private String mSessionId;
    private String mGroupName;
    private int mMasterDeviceId;
    private int mLocationId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public CreateGroupTask(String groupName, int masterDeviceId, int locationId,
                           IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        mGroupName = groupName;
        mMasterDeviceId = masterDeviceId;
        mLocationId = locationId;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult result = HttpProxy.getInstance().getWebService()
                    .createGroup(mSessionId, mGroupName, mMasterDeviceId, mLocationId,
                            mRequestParams, mIReceiveResponse);

            if (result.isResult()) {
                Bundle bundle = result.getResponseData();
                if (bundle.getInt(CreateGroupResponse.CODE_ID) == 200)
                    return result;
            }
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.CREATE_GROUP);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
