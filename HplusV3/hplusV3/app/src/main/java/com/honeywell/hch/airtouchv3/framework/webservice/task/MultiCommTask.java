package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskListResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskResponse;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

import java.util.List;

/**
 * Created by Jin Qian on 10/30/15.
 */
public class MultiCommTask extends BaseRequestTask {
    private String mUserId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;
    private static int checkMacPollingTime = 0;
    private static final int CHECK_COMM_TASK_TIMES = 15;

    public MultiCommTask(IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mUserId = AppManager.shareInstance().getAuthorizeApp().getUserID();
        this.mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult taskResult = HttpProxy.getInstance().getWebService()
                .multiCommTask(mSessionId, mRequestParams, mIReceiveResponse);

        if (taskResult.isResult()) {
            MultiCommTaskListResponse multiTaskData = (MultiCommTaskListResponse) taskResult.getResponseData()
                    .getSerializable(MultiCommTaskListResponse.MUTLICOMMTASK);
            if (multiTaskData == null)
                return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.MULTI_COMM_TASK);

            // If there exists task not finished, that means at least one task "Running" or "Created"
            List<MultiCommTaskResponse> tasks = multiTaskData.getMultiCommTaskResponses();
            for (MultiCommTaskResponse task : tasks) {
                if (task.getState().equals("Running") || task.getState().equals("Created")) {
                    taskResult.setFlag(AirTouchConstants.COMM_TASK_RUNNING);
                    checkMacPollingTime++;
                    if (checkMacPollingTime == CHECK_COMM_TASK_TIMES) {
                        checkMacPollingTime = 0;
                        if (hasTaskSucceed(tasks))
                            taskResult.setFlag(AirTouchConstants.COMM_TASK_PART_FAILED);
                        else
                            taskResult.setFlag(AirTouchConstants.COMM_TASK_ALL_FAILED);
                    }
                    return taskResult;
                }
            }

            // If all tasks finished, all "Succeeded"
            for (int i = 0; i < tasks.size(); i++) {
                if (!tasks.get(i).getState().equals("Succeeded")) {
                    break;
                } else {
                    if (i == (tasks.size() - 1)) {
                        taskResult.setFlag(AirTouchConstants.COMM_TASK_SUCCEED);
                        return taskResult;
                    }
                }
            }

            // If all tasks finished, some are "Failed"
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getState().equals("Failed")) {
                    if (hasTaskSucceed(tasks)) {
                        taskResult.setFlag(AirTouchConstants.COMM_TASK_PART_FAILED);
                        return taskResult;
                    }
                    if (i == (tasks.size() - 1)) {
                        taskResult.setFlag(AirTouchConstants.COMM_TASK_ALL_FAILED);
                        return taskResult;
                    }
                }
            }

            taskResult.setFlag(AirTouchConstants.COMM_TASK_END);
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

    private boolean hasTaskSucceed(List<MultiCommTaskResponse> tasks) {
        for (MultiCommTaskResponse task : tasks) {
            if (task.getState().equals("Succeeded")) {
                return true;
            }
        }
        return false;
    }

}
