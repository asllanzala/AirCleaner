package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/8/24.
 */
public class CommTask extends BaseRequestTask {
    private int mTaskId;
    private String mUserId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;
    private static int checkMacPollingTime;
    private static final int CHECK_COMM_TASK_TIMES = 40;

    public CommTask(int taskId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mTaskId = taskId;
        this.mUserId = AppManager.shareInstance().getAuthorizeApp().getUserID();
        this.mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult taskResult = HttpProxy.getInstance().getWebService()
                .getCommTask(mTaskId, mSessionId, mRequestParams, mIReceiveResponse);

        if (taskResult.isResult()) {
            switch (taskResult.getFlag()) {
                case AirTouchConstants.COMM_TASK_RUNNING:
                    if (checkMacPollingTime == CHECK_COMM_TASK_TIMES) {
                        taskResult.setFlag(AirTouchConstants.COMM_TASK_TIMEOUT);
                        checkMacPollingTime = 0;
                    } else {
                        checkMacPollingTime++;
                    }
                    break;

                case AirTouchConstants.COMM_TASK_SUCCEED:
                    HttpProxy.getInstance().getWebService().getLocation(mUserId, mSessionId, null, mIReceiveResponse);
                    reloadDeviceInfo();
                case AirTouchConstants.COMM_TASK_FAILED:
                case AirTouchConstants.COMM_TASK_TIMEOUT:
                    checkMacPollingTime = 0;
                    break;

                default:
                break;
            }
            return taskResult;
        }

        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.COMM_TASK);
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
