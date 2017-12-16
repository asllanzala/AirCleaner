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
public class GetGroupByGroupIdTask extends BaseRequestTask {
    private String mSessionId;
    private int mGroupId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public GetGroupByGroupIdTask(int groupId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
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
                    .getGroupByGroupId(mSessionId, mGroupId,
                            mRequestParams, mIReceiveResponse);
            return result;
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL,
                "", RequestID.GET_GROUP_BY_GROUP_ID);
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
