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
public class UpdateGroupNameTask extends BaseRequestTask {
    private String mSessionId;
    private String mGroupName;
    private int mGroupId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;
    public static int CODE_GROUP_NAME_ALREADY_EXIST = 107;

    public UpdateGroupNameTask(String groupName, int groupId,
                               IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        mGroupName = groupName;
        mGroupId = groupId;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult result = HttpProxy.getInstance().getWebService()
                    .updateGroupName(mSessionId, mGroupName, mGroupId,
                            mRequestParams, mIReceiveResponse);
            if (result.isResult()) {
                Bundle bundle = result.getResponseData();
                if (bundle.getInt(GroupResponse.CODE_ID) == 200)
                    return result;

                if (bundle.getInt(GroupResponse.CODE_ID) == CODE_GROUP_NAME_ALREADY_EXIST)
                    return result;
            }
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.UPDATE_GROUP_NAME);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
