package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

/**
 * Created by Jin Qian on 15/8/24.
 */
public class CheckMacTask extends BaseRequestTask {
    private String mMacId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;
    private static int checkMacPollingTime;
    private static final int CHECK_MAC_TIMES = 15;

    public CheckMacTask(String macId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mMacId = macId;
        this.mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult checkResult = HttpProxy.getInstance().getWebService()
                .checkMac(mMacId, mSessionId, mRequestParams, mIReceiveResponse);

        if (checkResult.getFlag() == AirTouchConstants.CHECK_MAC_AGAIN) {
            if (checkMacPollingTime >= CHECK_MAC_TIMES) {
                checkResult.setFlag(AirTouchConstants.CHECK_MAC_OFFLINE);
                checkMacPollingTime = 0;
            } else {
                checkMacPollingTime++;
            }
        }

        return checkResult;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
