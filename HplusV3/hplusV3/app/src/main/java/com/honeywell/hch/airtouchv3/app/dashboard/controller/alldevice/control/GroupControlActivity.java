package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.control;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.AllDeviceActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.GroupManager;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DevicesForGroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupDataResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupRequest;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.SharePreferenceUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Group control UI.
 */
public class GroupControlActivity extends BaseHasBackgroundActivity {

    public static final String TAG = "GroupControlActivity";

    private ImageView mWholeView;

    private CircleView mCircleView;

    private Map<Integer, AirTouchSeriesDevice> mDeviceDataMap = new HashMap<>();

    private Map<Integer, DeviceStatusView> mDeviceViewMap = new HashMap();

    private List<DeviceStatusView> mDeviceStatusViews = new ArrayList<>();

    private GroupDataResponse mGroupDataResponse;

    private static Dialog mDialog;

    public GroupManager mSendControlGroupManager = new GroupManager();

    private InputMethodManager mInputMethodManager;

    private TextView mGroupNameTextView;

    private EditText mGroupNameEditText;

    private TextView mGroupModeTextView;

    private LinearLayout mDeviceStatusListLinearLayout;

    private IntentFilter mGroupControlBraodcastIntentFilter;

    private int mCurrentGroupMode = DeviceMode.MODE_UNDEFINE;

    private Boolean mIsEditGroupNameMode = false;

    private int mGroupControlCommand;

    private FrameLayout mBackLayout;

    private Animation mAlphaOffAnimation;

    private boolean isControlling = false;

    private int mHomeIndex;

    private final int GROUPCACHEID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_control);

        mCircleView = (CircleView) findViewById(R.id.group_mode_circle_view);

        Intent intent = getIntent();
        if (intent != null) {
            mGroupDataResponse = (GroupDataResponse) intent
                    .getSerializableExtra(AllDeviceActivity.ARG_GROUP);
            mHomeIndex = intent.getIntExtra(AllDeviceActivity.ARG_HOME_INDEX, 0);
        }

        if (mGroupDataResponse == null)
            return;

        isControlling = getIsFlashing(mGroupDataResponse.getGroupId());

        initDynamicBackground();

        setupGroupModeTextView();

        setupGroupNameEditText();

        // register broadcast
        mGroupControlBraodcastIntentFilter = new IntentFilter();
        mGroupControlBraodcastIntentFilter.addAction(CircleView.BROADCAST_ACTION_GROUP_CONTROL);
        mGroupControlBraodcastIntentFilter.addAction(AirTouchConstants.SHORTTIME_REFRESH_END_ACTION);
        mGroupControlBraodcastIntentFilter.addAction(CircleView.BROADCAST_ACTION_SUCCEED_DEVICESTATUSVIEW);
        mGroupControlBraodcastIntentFilter.addAction(CircleView.BROADCAST_ACTION_STOP_FLASHINGTASK);
        registerReceiver(mGroupCommandReceiver, mGroupControlBraodcastIntentFilter);

        mDeviceStatusListLinearLayout = (LinearLayout) findViewById(
                R.id.group_control_group_status_layout);

        showAirTouchDeviceStatus(mDeviceStatusListLinearLayout);

        mWholeView = (ImageView) findViewById(R.id.whole_view);
        mWholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditGroupNameMode) {
                    updateNameGroupProcess();
                }
            }
        });

        mBackLayout = (FrameLayout) findViewById(R.id.back_layout);
        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAlphaOffAnimation = AnimationUtils.loadAnimation(this, R.anim.control_alpha);

        isFlashingView();

    }

    private void isFlashingView() {
        if (isControlling && (mCurrentGroupMode != DeviceMode.MODE_UNDEFINE)) {
            mCircleView.startFlashingTask();
            flashDeviceStatusView();
        }
    }

    private void flashDeviceStatusView() {
        for (DeviceStatusView deviceStatusView : mDeviceStatusViews) {
            String modeName = DeviceMode.getDeviceRunningStatusName(mCurrentGroupMode);
            String deviceStatus = deviceStatusView.getDeviceStatus();
            if (!deviceStatusView.getDeviceStatus().equals(HPlusApplication.getInstance().getString(R.string.offline))
                    && !modeName.equals(deviceStatus)) {
                deviceStatusView.getDeviceStatusTextView().startAnimation(mAlphaOffAnimation);
                deviceStatusView.getDeviceStatusTextView().setText(DeviceMode.getDeviceRunningStatusName(mCurrentGroupMode));
                clearAllColor(deviceStatusView);
            }
        }
        mAlphaOffAnimation = AnimationUtils.loadAnimation(this, R.anim.group_alpha);
    }

    private void setupGroupModeTextView() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mGroupNameEditText = (EditText) findViewById(R.id.group_control_group_name_et);
        mGroupModeTextView = (TextView) findViewById(R.id.group_control_group_mode);
        mGroupNameTextView = (TextView) findViewById(R.id.group_control_group_name);
        mGroupNameTextView.setText(mGroupDataResponse.getGroupName());

        // Set the group running status.
        mCurrentGroupMode = getCurrentGroupMode(mGroupDataResponse.getGroupId());
        mGroupControlCommand = mCurrentGroupMode;
        if (DeviceMode.MODE_UNDEFINE == mCurrentGroupMode) {
            mGroupModeTextView.setText("");
        } else {
            mGroupModeTextView.setText(getString(R.string.group_control_running_mode)
                    + DeviceMode.getModeName(mCurrentGroupMode));
        }
    }

    private void setupGroupNameEditText() {
        mGroupNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEditGroupNameMode = true;
                mGroupNameEditText.setVisibility(View.VISIBLE);
                mGroupNameTextView.setVisibility(View.INVISIBLE);
                mWholeView.setVisibility(View.VISIBLE);
                mGroupNameEditText.setText(mGroupNameTextView.getText());
                mGroupNameEditText.setFocusable(true);
                mGroupNameEditText.setFocusableInTouchMode(true);
                mGroupNameEditText.requestFocus();
                mGroupNameEditText.setHighlightColor(
                        getResources().getColor(R.color.group_edit_text_background));
                mGroupNameEditText.setCursorVisible(true);
                mGroupNameEditText.selectAll();
                mGroupNameEditText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_ENTER
                                && mInputMethodManager.isActive()) {

                            updateNameGroupProcess();
                        }
                        return false;
                    }
                });
                mInputMethodManager
                        .showSoftInput(mGroupNameEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        mGroupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mDeviceStatusListLinearLayout = (LinearLayout) findViewById(
                        R.id.group_control_group_status_layout);

                // register broadcast
                mGroupControlBraodcastIntentFilter = new IntentFilter();
                mGroupControlBraodcastIntentFilter
                        .addAction(CircleView.BROADCAST_ACTION_GROUP_CONTROL);
                registerReceiver(mGroupCommandReceiver, mGroupControlBraodcastIntentFilter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.maxCharacterFilter(mGroupNameEditText);
                StringUtil.addOrEditHomeFilter(mGroupNameEditText);
//                StringUtil.lengthMaxCharacterFilter(mGroupNameEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCircleView.stopDraw();
        mCircleView.stopFlashingTask(mCurrentGroupMode);
        isControlling = false;

        unregisterReceiver(mGroupCommandReceiver);


    }

    private BroadcastReceiver mGroupCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (CircleView.BROADCAST_ACTION_GROUP_CONTROL.equals(intentAction)) {
                mGroupControlCommand = intent.
                        getIntExtra(CircleView.BROADCAST_INTENT_EXTRA_KEY_GROUP_CONTROL_COMMAND, -1);
                if (mGroupControlCommand != -1) {
                    sendGroupControlCommand(mGroupControlCommand);
                }
            }
            if (AirTouchConstants.SHORTTIME_REFRESH_END_ACTION.equals(intentAction)) {
                isControlling = getIsFlashing(mGroupDataResponse.getGroupId());
                if (!isControlling) {
                    mCircleView.stopFlashingTask(mCurrentGroupMode);
                    showAirTouchDeviceStatus(mDeviceStatusListLinearLayout);
                }
            }
            if (CircleView.BROADCAST_ACTION_SUCCEED_DEVICESTATUSVIEW.equals(intentAction)) {
                int deviceId = intent.getIntExtra(CircleView.BROADCAST_INTENT_EXTRA_KEY_SUCCEED_DEVICESTATUSVIEW, 0);
                DeviceStatusView deviceStatusView = mDeviceViewMap.get(deviceId);
                if (deviceStatusView != null) {
                    clearDeviceViewAnimation(deviceStatusView);
                    clearAllColor(deviceStatusView);
                }
            }
            if (CircleView.BROADCAST_ACTION_STOP_FLASHINGTASK.equals(intentAction)) {
                mCircleView.stopFlashingTask(mCurrentGroupMode);
                clearAllDeviceViewAnimation();
            }
        }
    };

    private void sendGroupControlCommand(int scenarioMode) {
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG,
                "sendGroupControlCommand: scenarioMode=" + scenarioMode + ", " + DeviceMode
                        .getModeName(scenarioMode));
        ScenarioGroupRequest request = new ScenarioGroupRequest(scenarioMode);

        // show device scenario textView animation
        for (DeviceStatusView deviceStatusView : mDeviceStatusViews) {
            if (!deviceStatusView.getDeviceStatus()
                    .equals(HPlusApplication.getInstance().getString(R.string.offline))) {
                deviceStatusView.getDeviceStatusTextView().startAnimation(mAlphaOffAnimation);
                deviceStatusView.getDeviceStatusTextView().setText(DeviceMode.getDeviceRunningStatusName(scenarioMode));
                clearAllColor(deviceStatusView);
            }
        }

        mSendControlGroupManager.sendScenarioToGroup(mGroupDataResponse.getGroupId(), request);
        clearPreference();
        setIsFlashing(mGroupDataResponse.getGroupId(), true);
        mSendControlGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(final ResponseResult sendGroupCommandResponseResult) {
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "onSuccessCallback");
                // set group mode
                setCurrentGroupMode(mGroupDataResponse.getGroupId(), mGroupControlCommand);
                mGroupModeTextView.setText(getString(R.string.group_control_running_mode)
                        + DeviceMode.getModeName(mCurrentGroupMode));
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "sendGroupCommandResponseResult.getFlag(): " + sendGroupCommandResponseResult.getFlag());
                switch (sendGroupCommandResponseResult.getFlag()) {
                    case AirTouchConstants.COMM_TASK_PART_SUCCEED:
                        handleSucceededDeviceStatusView(sendGroupCommandResponseResult);
                        break;

                    case AirTouchConstants.COMM_TASK_SUCCEED:
                        handleSucceededDeviceStatusView(sendGroupCommandResponseResult);
                        sendStopFlashTask();
                        setIsFlashing(mGroupDataResponse.getGroupId(), false);
                        break;

                    case AirTouchConstants.COMM_TASK_PART_FAILED:
                        handleFailedDeviceStatusView();
                        sendStopFlashTask();
                        setIsFlashing(mGroupDataResponse.getGroupId(), false);
                        MessageBox.createSimpleDialog(GroupControlActivity.this, null,
                                getString(R.string.part_device_failed), null, null);
                        break;

                    case AirTouchConstants.COMM_TASK_ALL_FAILED:
                        handleFailedDeviceStatusView();
                        sendStopFlashTask();
                        setIsFlashing(mGroupDataResponse.getGroupId(), false);
                        MessageBox.createSimpleDialog(GroupControlActivity.this, null,
                                getString(R.string.all_device_failed), null, null);
                        break;

                    case AirTouchConstants.COMM_TASK_END:
                        handleFailedDeviceStatusView();
                        sendStopFlashTask();
                        setIsFlashing(mGroupDataResponse.getGroupId(), false);
                        break;

                    default:
                        break;

                }
            }
        });

        mSendControlGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "onErrorCallback");

                handleFailedDeviceStatusView();
                stopAllFlash();
                setIsFlashing(mGroupDataResponse.getGroupId(), false);
                // Reset the circle UI to previous mode.
                mCircleView.setCurrentMode(mCurrentGroupMode);

                if (responseResult.getRequestId() == RequestID.SEND_SCENARIO_TO_GROUP) {
                    errorHandle(responseResult, getString(id));
                }
            }
        });

    }

    private void showAirTouchDeviceStatus(LinearLayout layout) {
        mDeviceDataMap.clear();
        mDeviceViewMap.clear();
        UserLocationData userLocationData = AppManager.shareInstance().getLocationWithId(
                mGroupDataResponse.getLocationId());

        if (userLocationData == null)
            return;

        List<DevicesForGroupResponse> deviceList = mGroupDataResponse.getGroupDeviceList();
        layout.removeAllViews();
        for (DevicesForGroupResponse device : deviceList) {
            if (AppManager.shareInstance().isAirtouchSeries((int) device.getDeviceType())) {
                AirTouchSeriesDevice airTouchDevice = (AirTouchSeriesDevice) userLocationData
                        .getHomeDeviceWithDeviceId(device.getDeviceId());

                // Check the current device mode
                if (airTouchDevice == null || airTouchDevice.getDeviceRunStatus() == null
                        || airTouchDevice.getDeviceInfo() == null)
                    continue;

                mDeviceDataMap.put(airTouchDevice.getDeviceInfo().getDeviceID(), airTouchDevice);
                String deviceRunStatus = airTouchDevice.getDeviceModeOrSpeed(mContext);
                int deviceId = airTouchDevice.getDeviceInfo().getDeviceID();
                String deviceName = airTouchDevice.getDeviceInfo().getName();

                DeviceStatusView deviceStatusView = new DeviceStatusView(layout.getContext(), deviceId,
                        deviceName, deviceRunStatus);

                showStatusDifferentColor(deviceStatusView, mCurrentGroupMode);

                mDeviceStatusViews.add(deviceStatusView);
                layout.addView(deviceStatusView);
                mDeviceViewMap.put(airTouchDevice.getDeviceInfo().getDeviceID(), deviceStatusView);
            }
        }

        mCircleView.setCurrentMode(mCurrentGroupMode);
    }

    public int getCurrentGroupMode(int groupID) {
        // TO-DO: need to delete data in SharePreference
        return SharePreferenceUtil.getPrefInt(Integer.toString(groupID), DeviceMode.MODE_UNDEFINE);
    }

    public void setCurrentGroupMode(int groupID, int groupMode) {
        mCurrentGroupMode = groupMode;
        SharePreferenceUtil.setPrefInt(Integer.toString(groupID), groupMode);
    }

    private boolean getIsFlashing(int groupID) {
        return SharePreferenceUtil.getMyPrefBoolean(Integer.toString(groupID + GROUPCACHEID), DeviceMode.IS_REFLASHING);
    }

    private void setIsFlashing(int groupID, boolean isFlashing) {
        isControlling = isFlashing;
        SharePreferenceUtil.setMyPrefBoolean(Integer.toString(groupID + GROUPCACHEID), isFlashing);
    }

    private void clearPreference() {
        SharePreferenceUtil.clearMyPreference(mContext, SharePreferenceUtil.getSharedPreferencesInstance());
    }

    private void updateNameGroupProcess() {
        mWholeView.setVisibility(View.INVISIBLE);
        mGroupNameEditText.setVisibility(View.INVISIBLE);
        mInputMethodManager.hideSoftInputFromWindow(mGroupNameEditText.getWindowToken(), 0);

        mDialog = LoadingProgressDialog.show(GroupControlActivity.this, getString(R.string.updating_group_name));

        if (mGroupNameEditText.getText().toString().equals("")) {
            quitEditGroupNameMode();
            return;
        }

        if (mGroupNameEditText.getText().toString().equals(mGroupNameTextView.getText().toString())) {
            quitEditGroupNameMode();
            return;
        }

        GroupManager groupManager = new GroupManager();
        groupManager.updateGroupName(mGroupNameEditText.getText().toString(),
                mGroupDataResponse.getGroupId());
        groupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            public void onSuccess(ResponseResult responseResult) {
                quitEditGroupNameMode();
                mGroupNameTextView.setText(mGroupNameEditText.getText());
            }
        });
        groupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                quitEditGroupNameMode();
                mGroupNameTextView.setText(mGroupDataResponse.getGroupName());
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private void quitEditGroupNameMode() {
        if (mDialog != null)
            mDialog.dismiss();

        mIsEditGroupNameMode = false;
        mGroupNameEditText.setVisibility(View.INVISIBLE);
        mGroupNameTextView.setVisibility(View.VISIBLE);
    }

    public void showStatusDifferentColor(DeviceStatusView deviceStatusView, int currentMode) {
        String deviceStatus = deviceStatusView.getDeviceStatus();
        TextView deviceStatusTextView = deviceStatusView.getDeviceStatusTextView();
        switch (currentMode) {
            case DeviceMode.MODE_UNDEFINE:
                break;

            case DeviceMode.MODE_HOME:
                if (deviceStatus.equals(HPlusApplication.getInstance().getString(R.string.control_auto)))
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.white_80));
                else
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.group_control_different));
                break;

            case DeviceMode.MODE_SLEEP:
                if (deviceStatus.equals(HPlusApplication.getInstance().getString(R.string.control_sleep)))
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.white_80));
                else
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.group_control_different));
                break;

            case DeviceMode.MODE_AWAY:
                if (deviceStatus.equals(HPlusApplication.getInstance().getString(R.string.off)))
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.white_80));
                else
                    deviceStatusTextView.setTextColor(getResources().getColor(R.color.group_control_different));
                break;

        }
    }

    public void clearAllColor(DeviceStatusView deviceStatusView) {
        TextView deviceStatusTextView = deviceStatusView.getDeviceStatusTextView();
        deviceStatusTextView.setTextColor(getResources().getColor(R.color.white_80));
    }

    private void handleSucceededDeviceStatusView(ResponseResult responseResult) {
        Bundle bundle = responseResult.getResponseData();
        ArrayList<Integer> deviceIds = bundle.getIntegerArrayList(GroupManager.BUNDLE_DEVICES_IDS);

        if (deviceIds != null) {
            for (int deviceId : deviceIds) {
                sendSucceedDeviceView(deviceId);
            }
        }
    }

    private void handleFailedDeviceStatusView() {
        for (DeviceStatusView deviceStatusView : mDeviceStatusViews) {
            deviceStatusView.getDeviceStatusTextView().clearAnimation();
            deviceStatusView.getDeviceStatusTextView().setText(deviceStatusView.getDeviceStatus());
            showStatusDifferentColor(deviceStatusView, mGroupControlCommand);
        }
    }

    //stop textview animation
    private void sendSucceedDeviceView(int deviceId) {
        Intent intent = new Intent(CircleView.BROADCAST_ACTION_SUCCEED_DEVICESTATUSVIEW);
        intent.putExtra(CircleView.BROADCAST_INTENT_EXTRA_KEY_SUCCEED_DEVICESTATUSVIEW, deviceId);
        HPlusApplication.getInstance().sendBroadcast(intent);
    }

    //stop flashing task animation
    private void sendStopFlashTask() {
        Intent intent = new Intent(CircleView.BROADCAST_ACTION_STOP_FLASHINGTASK);
        HPlusApplication.getInstance().sendBroadcast(intent);
    }

    private void stopAllFlash() {
        mCircleView.stopFlashingTask(DeviceMode.MODE_AWAY);
        mCircleView.stopFlashingTask(DeviceMode.MODE_HOME);
        mCircleView.stopFlashingTask(DeviceMode.MODE_SLEEP);
    }

    private void clearDeviceViewAnimation(DeviceStatusView deviceStatusView) {
        deviceStatusView.getDeviceStatusTextView().clearAnimation();
    }

    //clear all deviceAnimation
    private void clearAllDeviceViewAnimation() {
        for (DeviceStatusView deviceStatusView : mDeviceViewMap.values()) {
            deviceStatusView.getDeviceStatusTextView().clearAnimation();
        }
    }

}
