package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice;

import android.os.Bundle;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DeviceListRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupCommTaskResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskListResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupResponse;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.http.PhoneNameRequest;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.task.AddDeviceToGroupTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.CommTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.CreateGroupTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.DeleteDeviceFromGroupTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.DeleteDeviceTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.DeleteGroupTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.GetGroupByGroupIdTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.GetGroupByLocationIdTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.IsDeviceMasterTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.MultiCommTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.framework.webservice.task.SendScenarioToGroupTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.UpdateGroupNameTask;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Qian Jin on 10/13/15.
 */
public class GroupManager {
    private SuccessCallback mSuccessCallback;
    private ErrorCallback mErrorCallback;
    private Map<Integer, Integer> mCommTaskMap = new HashMap<>();
    private static int mSendSenarioCount = 0;
    public static final String BUNDLE_DEVICE_ID = "device_id";
    public static final String BUNDLE_DEVICES_IDS = "device_ids";

    public GroupManager() {

    }

    public interface SuccessCallback {
        void onSuccess(ResponseResult responseResult);
    }

    public interface ErrorCallback {
        void onError(ResponseResult responseResult, int id);
    }

    public void setSuccessCallback(SuccessCallback successCallback) {
        mSuccessCallback = successCallback;
    }

    public void setErrorCallback(ErrorCallback errorCallback) {
        mErrorCallback = errorCallback;
    }


    IActivityReceive response = new IActivityReceive() {
        @Override
        public void onReceive(ResponseResult responseResult) {
            switch (responseResult.getRequestId()) {
                case CREATE_GROUP:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case DELETE_GROUP:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case ADD_DEVICE_TO_GROUP:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case DELETE_DEVICE_FROM_GROUP:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case UPDATE_GROUP_NAME:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        Bundle bundle = responseResult.getResponseData();
                        if (bundle != null) {
                            if (bundle.getInt(GroupResponse.CODE_ID) == UpdateGroupNameTask
                                    .CODE_GROUP_NAME_ALREADY_EXIST) {
                                mErrorCallback.onError(responseResult, R.string.group_name_exist);
                                break;
                            }
                        }

                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case GET_GROUP_BY_GROUP_ID:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case GET_GROUP_BY_LOCATION_ID:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case IS_DEVICE_MASTER:
                    if (responseResult.isResult()
                            && (responseResult.getResponseCode() == StatusCode.OK)) {
                        mSuccessCallback.onSuccess(responseResult);
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;

                case DELETE_DEVICE:
                    if (responseResult.getResponseCode() == StatusCode.OK) {
                        if (responseResult.getResponseData() != null) {
                            int taskId = responseResult.getResponseData()
                                    .getInt(AirTouchConstants.COMM_TASK_BUNDLE_KEY);
                            runCommTaskForDeleteDevice(taskId, 0);
                        } else {
                            mErrorCallback.onError(responseResult, R.string.enroll_error);
                        }
                    } else {
                        mErrorCallback.onError(responseResult, R.string.enroll_error);
                    }
                    break;
                case SEND_SCENARIO_TO_GROUP:
                    if (responseResult.getResponseCode() == StatusCode.OK) {
                        // Return success result to UI thread.
                        mSuccessCallback.onSuccess(responseResult);

                        ScenarioGroupResponse response = (ScenarioGroupResponse) responseResult.getResponseData()
                                .getSerializable(ScenarioGroupResponse.SCENARIO_DATA);
                        if (response == null) {
                            mErrorCallback.onError(responseResult, R.string.enroll_error);
                            break;
                        }

                        mCommTaskMap.clear();
                        List<Integer> taskIds = new ArrayList<>();
                        int taskCount = 0;
                        for (GroupCommTaskResponse res : response.getGroupCommTaskResponse()) {
                            if (res.getCommTaskId() == 0)
                                continue;

                            taskCount += res.getCommTaskId();
                            taskIds.add(res.getCommTaskId());
                            mCommTaskMap.put(res.getCommTaskId(), res.getDeviceId());
                        }

                        // If all task is 0, do not run multi-task.
                        if (taskCount == 0) {
                            mErrorCallback.onError(responseResult, R.string.all_device_failed);
                        } else {
                            mSendSenarioCount++;
                            runMultiCommTask(new MultiCommTaskRequest(taskIds));
                        }
                    } else {
                        mErrorCallback.onError(responseResult, R.string.all_device_failed);
                    }
                    break;

                default:
                    mErrorCallback.onError(responseResult, R.string.enroll_error);
                    break;
            }
        }
    };

    public void createGroup(String groupName, int masterDeviceId,
                            int locationId, DeviceListRequest request) {
        CreateGroupTask requestTask
                = new CreateGroupTask(toURLEncoded(groupName), masterDeviceId, locationId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }


    public void deleteGroup(int groupId) {
        // temp - server issue
        PhoneNameRequest request = new PhoneNameRequest("");
        DeleteGroupTask requestTask
                = new DeleteGroupTask(groupId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void addDeviceToGroup(int groupId, DeviceListRequest request) {
        AddDeviceToGroupTask requestTask
                = new AddDeviceToGroupTask(groupId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void deleteDeviceFromGroup(int groupId, DeviceListRequest request) {
        DeleteDeviceFromGroupTask requestTask
                = new DeleteDeviceFromGroupTask(groupId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void updateGroupName(String groupName, int groupId) {
        // temp - server issue
        PhoneNameRequest request = new PhoneNameRequest("");
        UpdateGroupNameTask requestTask
                = new UpdateGroupNameTask(toURLEncoded(groupName), groupId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void getGroupByGroupId(int groupId) {
        GetGroupByGroupIdTask requestTask
                = new GetGroupByGroupIdTask(groupId, null, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void getGroupByLocationId(int locationId) {
        GetGroupByLocationIdTask requestTask
                = new GetGroupByLocationIdTask(locationId, null, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void isMasterDevice(int deviceId) {
        IsDeviceMasterTask requestTask
                = new IsDeviceMasterTask(deviceId, null, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    public void deleteDevice(int deviceId) {
        DeleteDeviceTask requestTask
                = new DeleteDeviceTask(deviceId, null, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    private void runCommTaskForDeleteDevice(final int taskId, final int deviceId) {
        final IActivityReceive runCommTaskResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case COMM_TASK:
                            switch (responseResult.getFlag()) {
                                case AirTouchConstants.COMM_TASK_RUNNING:
                                    runCommTaskForDeleteDevice(taskId, deviceId);
                                    break;

                                case AirTouchConstants.COMM_TASK_SUCCEED:
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putInt(BUNDLE_DEVICE_ID, deviceId);
                                    responseResult.setResponseData(bundle1);
                                    mSuccessCallback.onSuccess(responseResult);
                                    break;

                                case AirTouchConstants.COMM_TASK_FAILED:
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putInt(BUNDLE_DEVICE_ID, deviceId);
                                    responseResult.setResponseData(bundle2);
                                    mErrorCallback.onError(responseResult, R.string.delete_device_fail);
                                    break;

                                case AirTouchConstants.COMM_TASK_TIMEOUT:
                                    Bundle bundle3 = new Bundle();
                                    bundle3.putInt(BUNDLE_DEVICE_ID, deviceId);
                                    responseResult.setResponseData(bundle3);
                                    // PO said that regard TIMEOUT as SUCCESS.... >_<
//                                    mErrorCallback.onError(responseResult, R.string.control_timeout);
                                    mSuccessCallback.onSuccess(responseResult);
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    mErrorCallback.onError(responseResult, R.string.enroll_error);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(AirTouchConstants.COMM_TASK_TIME_GAP);
                    CommTask commTask = new CommTask(taskId, null, runCommTaskResponse);
                    AsyncTaskExecutorUtil.executeAsyncTask(commTask);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void sendScenarioToGroup(int groupId, ScenarioGroupRequest request) {
        SendScenarioToGroupTask requestTask
                = new SendScenarioToGroupTask(groupId, request, response);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }

    private void runMultiCommTask(final MultiCommTaskRequest request) {
        final IActivityReceive runCommTaskResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case MULTI_COMM_TASK:
                            if (mSendSenarioCount > 1) {
                                mSendSenarioCount--;
                                return;
                            }

                            switch (responseResult.getFlag()) {
                                case AirTouchConstants.COMM_TASK_RUNNING:
                                    getSucceedDevice(responseResult);
                                    runMultiCommTask(request);
                                    break;
                                case AirTouchConstants.COMM_TASK_SUCCEED:
                                    mSendSenarioCount = 0;
                                    getSucceedDevice(responseResult);
                                    break;
                                case AirTouchConstants.COMM_TASK_END:
                                case AirTouchConstants.COMM_TASK_PART_FAILED:
                                case AirTouchConstants.COMM_TASK_ALL_FAILED:
                                    mSendSenarioCount = 0;
                                    mSuccessCallback.onSuccess(responseResult);
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    mErrorCallback.onError(responseResult, R.string.enroll_error);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(AirTouchConstants.COMM_TASK_TIME_GAP);
                    MultiCommTask commTask = new MultiCommTask(request, runCommTaskResponse);
                    AsyncTaskExecutorUtil.executeAsyncTask(commTask);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null ||
                paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void getSucceedDevice(ResponseResult result) {
        MultiCommTaskListResponse multiTaskData = (MultiCommTaskListResponse) result.getResponseData()
                .getSerializable(MultiCommTaskListResponse.MUTLICOMMTASK);
        if (multiTaskData == null)
            return;

        ArrayList<Integer> deviceIds = new ArrayList<>();
        List<MultiCommTaskResponse> tasks = multiTaskData.getMultiCommTaskResponses();
        for (MultiCommTaskResponse task : tasks) {
            if (task.getState().equals("Succeeded")) {
                deviceIds.add(mCommTaskMap.get(task.getCommTaskId()));
            }
        }
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(BUNDLE_DEVICES_IDS, deviceIds);
        result.setResponseData(bundle);
        if (result.getFlag() != AirTouchConstants.COMM_TASK_SUCCEED)
            result.setFlag(AirTouchConstants.COMM_TASK_PART_SUCCEED);

        mSuccessCallback.onSuccess(result);
    }

    public IActivityReceive getResponse(){
        return response;
    }

}
