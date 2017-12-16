package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.os.Bundle;

import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupResponse;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 10/13/15.
 */
public class DeleteGroupTask extends BaseRequestTask {
    private String mSessionId;
    private int mGroupId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public DeleteGroupTask(int groupId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        mGroupId = groupId;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult result = HttpProxy.getInstance().getWebService()
                    .deleteGroup(mSessionId, mGroupId,
                            mRequestParams, mIReceiveResponse);

            if (result.isResult()) {
                Bundle bundle = result.getResponseData();
                // 401 - group does not exist
                if (bundle.getInt(GroupResponse.CODE_ID) == 200
                        || bundle.getInt(GroupResponse.CODE_ID) == 401)
                    return result;
            }
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.DELETE_GROUP);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
