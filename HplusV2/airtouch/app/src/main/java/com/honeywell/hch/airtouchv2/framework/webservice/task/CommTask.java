package com.honeywell.hch.airtouchv2.framework.webservice.task;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

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
    private static final int CHECK_COMM_TASK_TIMES = 60;

    public CommTask(int taskId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mTaskId = taskId;
        this.mUserId = AuthorizeApp.shareInstance().getUserID();
        this.mSessionId = AuthorizeApp.shareInstance().getSessionId();
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult taskResult = HttpClientRequest.sharedInstance()
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
                    HttpClientRequest.sharedInstance().getLocation(mUserId, mSessionId, null, mIReceiveResponse);
                    reloadDeviceInfo();
                case AirTouchConstants.COMM_TASK_FAILED:
                case AirTouchConstants.COMM_TASK_TIMEOUT:
                    checkMacPollingTime = 0;
                    break;

                default:
                break;
            }
        }

        return taskResult;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
