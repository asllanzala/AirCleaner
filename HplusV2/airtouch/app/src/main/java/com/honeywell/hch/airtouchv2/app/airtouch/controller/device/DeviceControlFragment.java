package com.honeywell.hch.airtouchv2.app.airtouch.controller.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.CommTaskResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.DeviceControlRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.LedSetting;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jin Qian on 2/11/2015.
 * 1) led for speed control
 * 2) mode/power/offline/remote control
 * 3) filter status
 * 4) clean time for back home
 */
public class DeviceControlFragment extends BaseRequestFragment {

    private final String TAG = "AirTouchControl";
    private static final String ARG_DEVICE = "device";
    private int tooQuickCount;
    static final int COMM_TASK_TIMEOUT_COUNT = 180;
    static final int USER_INPUT_TOO_QUICK = 1 * 1000;

    private FrameLayout cancelLayout;
    private LinearLayout errorLayout;
    private LinearLayout filterStatusLayout;
    private RelativeLayout tutorialMask;
    private TextView deviceNameTextView;
    private TextView errorTextView;
    private TextView cleanUpTextView;
    private TextView arriveHomeTextView;
    private TextView preFilterTextView;
    private TextView pm25FilterTextView;
    private TextView hisivFilterTextView;
    private CheckBox powerCheckBox;
    private View controlButtonPanel;
    private ArrayList<CheckBox> ledCheckBox = new ArrayList<>();
    private int[] ledCheckBoxIds = {R.id.cb1, R.id.cb2, R.id.cb3, R.id.cb4, R.id.cb5, R.id.cb6,
            R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10, R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14};
    private ImageButton minLedImageButton, maxLedImageButton;
    private RadioGroup modeRadioGroup;
    private RadioButton sleepRadioButton, autoRadioButton, quickRadioButton, silentRadioButton;

    private Animation alphaOffAnimation;
    private Animation translateInAnimation;
    private Animation translateOutAnimation;
    private LedSetting ledSetting;
    private ColorStateList cslModeOn, cslModeOff;

    private Boolean isMobileControl = true;
    private Boolean isPowerOn = false;
    private Boolean isLedOnMove = false;
    private Boolean isLedOnDown = false;
    private boolean isCommTaskIdChanged = false;
    private RunStatus mLatestRunStatus;
    private String mSessionId;
    private int mDeviceId;
    private HomeDevice mCurrentDevice = null;
    private FragmentActivity mActivity;

    private int mCommTaskId = 0;
    private int mCommTaskCount = 0;
    private Long lastTimeUserInput = 0L;
    private Long lastTimeRunTimeSucceed = 0L;
    private int[] mCleanTimeArray;
    private BroadcastReceiver runStatusChangedReceiver;
    private boolean isUserInputTooQuick = false;
    private boolean isControllingDevice = false;
    private boolean isSwitchPowerOn = false; // for Auto mode
    private boolean isModeOn = false; // for a special case: mode switch to manual
    private OnControlClickListener mOnControlClickListener;
    private OnFilterClickListener mOnFilterClickListener;

    private String mCommand = ""; // this command combines speed and mode
    private final String AUTO = "Auto";
    private final String OFF = "Off";
    private final String SLEEP = "Sleep";
    private final String QUICK = "QuickClean";
    private final String SILENT = "Silent";
    private final String MANUAL = "Manual";

    // getString message
    private String mDeviceOffline;
    private String mDeviceControl;
    private String mControlTimeout;
    private String mError;

    private RelativeLayout filterTitleLayout;

    public static DeviceControlFragment newInstance(FragmentActivity activity) {
        DeviceControlFragment fragment = new DeviceControlFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        mCurrentDevice = AuthorizeApp.shareInstance().getCurrentDevice();
        if (getArguments() != null) {
            if ( mCurrentDevice.getDeviceInfo() == null) {
                MessageBox.createSimpleDialog(mActivity, null,
                        getString(R.string.no_data), getString(R.string.ok), quit);
            }
        }
        ((DeviceActivity) mActivity).registerMyTouchListener(mTouchListener);
        registerRunStatusChangedReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isHasNullPoint()) {
            MessageBox.createSimpleDialog(mActivity, null,
                    getString(R.string.no_data), getString(R.string.ok), quit);
        } else {
            initDevice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterRunStatusChangedReceiver();
        ((DeviceActivity) mActivity).unRegisterMyTouchListener(mTouchListener);
    }

    private void initView(View view) {
        cslModeOn = (ColorStateList) getResources().getColorStateList(R.color.white);
        cslModeOff = (ColorStateList) getResources().getColorStateList(R.color.white_50);
        alphaOffAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_alpha);
        translateInAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        translateOutAnimation.setAnimationListener(new translateOutAnimationListener());

        deviceNameTextView = (TextView) view.findViewById(R.id.home_name);
        deviceNameTextView.setText(mCurrentDevice.getDeviceInfo().getName());
        errorTextView = (TextView) view.findViewById(R.id.control_error_tv);
        cleanUpTextView = (TextView) view.findViewById(R.id.clean_time_tv);
        arriveHomeTextView = (TextView) view.findViewById(R.id.arrive_home_tv);
        preFilterTextView = (TextView) view.findViewById(R.id.pre_filter_text);
        pm25FilterTextView = (TextView) view.findViewById(R.id.pm25_filter_text);
        hisivFilterTextView = (TextView) view.findViewById(R.id.hisiv_filter_text);
        errorLayout = (LinearLayout) view.findViewById(R.id.control_error_layout);
        cancelLayout = (FrameLayout) view.findViewById(R.id.cancel_layout);
        cancelLayout.setOnClickListener(cancelOnClick);
        filterStatusLayout = (LinearLayout) view.findViewById(R.id.filter_title_text_layout);
        filterStatusLayout.setOnClickListener(filterOnClick);
        powerCheckBox = (CheckBox) view.findViewById(R.id.power_checkBox);
        powerCheckBox.setOnClickListener(powerOnClick);
        for (int id : ledCheckBoxIds) {
            CheckBox led = (CheckBox) view.findViewById(id);
            ledCheckBox.add(led);
        }
        minLedImageButton = (ImageButton) view.findViewById(R.id.led_min_iv);
        minLedImageButton.setOnClickListener(minLedOnClick);
        maxLedImageButton = (ImageButton) view.findViewById(R.id.led_max_iv);
        maxLedImageButton.setOnClickListener(maxLedOnClick);
        controlButtonPanel = view.findViewById(R.id.control_button_panel);
        tutorialMask = (RelativeLayout) view.findViewById(R.id.tutorial_mask);

        modeRadioGroup = (RadioGroup) view.findViewById(R.id.mode_rb);
        sleepRadioButton = (RadioButton) view.findViewById(R.id.sleep);
        autoRadioButton = (RadioButton) view.findViewById(R.id.auto);
        quickRadioButton = (RadioButton) view.findViewById(R.id.quick);
        silentRadioButton = (RadioButton) view.findViewById(R.id.silent);

        mDeviceOffline = getString(R.string.device_offline);
        mDeviceControl = getString(R.string.device_control);
        mControlTimeout = getString(R.string.control_timeout);
        mError = getString(R.string.enroll_error);

        showControlTutorial();

        filterTitleLayout = (RelativeLayout) view.findViewById(R.id.filter_title_layout);
        /**
         * Mode control
         */
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (isPowerOn) {
                    clearAllAnimation();
                    if (sleepRadioButton.isChecked()) {
                        sleepRadioButton.setTextColor(cslModeOn);
                        sleepRadioButton.startAnimation(alphaOffAnimation);
                        mCommand = SLEEP;
                        controlDevice();
                        displayCleanTime(1);
                    } else if (autoRadioButton.isChecked()) {
                        autoRadioButton.setTextColor(cslModeOn);
                        autoRadioButton.startAnimation(alphaOffAnimation);
                        mCommand = AUTO;
                        controlDevice();
                        displayCleanTime(3);
                    } else if (quickRadioButton.isChecked()) {
                        quickRadioButton.setTextColor(cslModeOn);
                        quickRadioButton.startAnimation(alphaOffAnimation);
                        mCommand = QUICK;
                        controlDevice();
                        displayCleanTime(7);
                    } else if (silentRadioButton.isChecked()) {
                        silentRadioButton.setTextColor(cslModeOn);
                        silentRadioButton.startAnimation(alphaOffAnimation);
                        mCommand = SILENT;
                        controlDevice();
                        displayCleanTime(2);
                    }
                }
            }
        });
    }

    public boolean titleLayoutIsScroll() {

        //get screen height
        int screenHeight = DensityUtil.getScreenHeight();

        //get filter title layout height
        int scrollHeight = filterTitleLayout.getHeight();

        //get filter tile layout current position
        int[] curLocation = new int[2];
        filterTitleLayout.getLocationOnScreen(curLocation);

        if (curLocation[1] < screenHeight - scrollHeight) {
            return true;
        }
        return false;

    }

    public void removeControlTutorial() {
        tutorialMask.setVisibility(View.INVISIBLE);
    }

    public void showControlTutorial() {
        if (AppConfig.isControlTutorial) {
            tutorialMask.setVisibility(View.INVISIBLE);
        } else {
            tutorialMask.setVisibility(View.VISIBLE);
            tutorialMask.setOnClickListener(tutorialOnClick);
        }
    }

    public void showControlTutorialForFilter() {
        if (AppConfig.isFilterTutorial) {
            tutorialMask.setVisibility(View.INVISIBLE);
        } else {
            tutorialMask.setVisibility(View.VISIBLE);
            tutorialMask.setOnClickListener(tutorialOnClick);
        }
    }

    View.OnClickListener tutorialOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tutorialMask.setVisibility(View.INVISIBLE);
            AppConfig appConfig = AppConfig.shareInstance();
            appConfig.setIsControlTutorial(true);
        }
    };

    /**
     * get device running status and display.
     */
    private void initDevice() {
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        mDeviceId = AuthorizeApp.shareInstance().getCurrentDeviceId();
        ledSetting = new LedSetting();
        mLatestRunStatus = new RunStatus();

        UserLocation userLocation = ((DeviceActivity) mActivity).getmUserLocation();
        ArrayList<AirTouchSeriesDevice> airTouchSeriesList = userLocation.getAirTouchSeriesList();
        for (AirTouchSeriesDevice homeDevicePM25Item : airTouchSeriesList) {
            if (homeDevicePM25Item.getDeviceInfo().getDeviceID() == mCurrentDevice.getDeviceInfo().getDeviceID()) {
                if (homeDevicePM25Item.getDeviceRunStatus() == null) {
                    mLatestRunStatus.setScenarioMode(OFF);
                    mLatestRunStatus.setFanSpeedStatus(OFF);
                    mLatestRunStatus.setIsAlive(true);
                } else {
                    mLatestRunStatus.setScenarioMode(homeDevicePM25Item.getDeviceRunStatus().getScenarioMode());
                    mLatestRunStatus.setFanSpeedStatus(homeDevicePM25Item.getDeviceRunStatus().getFanSpeedStatus());
                    mLatestRunStatus.setIsAlive(homeDevicePM25Item.getDeviceRunStatus().getIsAlive());
                }
                break;
            }
        }

        displayStatus(mLatestRunStatus);

        int getDeviceStatusRequestId = getRequestClient().getDeviceStatus(mDeviceId, mSessionId,
                mReceiveResponse);
        addRequestId(getDeviceStatusRequestId);

        RunStatus runs = ((AirTouchSeriesDevice)AuthorizeApp.shareInstance().getCurrentDevice()).getDeviceRunStatus();
        mCleanTimeArray = runs.getCleanTime();
        displayStatus(runs);
    }

    public void registerRunStatusChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("runStatusChanged");
        runStatusChangedReceiver = new RunStatusChangedReceiver();
        mActivity.registerReceiver(runStatusChangedReceiver, intentFilter);
    }

    public void unRegisterRunStatusChangedReceiver() {
        if (runStatusChangedReceiver != null) {
            mActivity.unregisterReceiver(runStatusChangedReceiver);
        }
    }

    private class RunStatusChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("runStatusChanged".equals(action)) {
                mLatestRunStatus = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus();

                long timeDeviation = System.currentTimeMillis() - lastTimeRunTimeSucceed;
                if (timeDeviation > 20 * 1000) {
                    if (!isControllingDevice)
                        displayStatus(mLatestRunStatus);
                }
            }
        }
    }

    /**
     * HTTP response
     */
    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case CONTROL_DEVICE:
                    if (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RecordCreatedResponse.class);

                            handleCommTaskChangedAndCount(recordCreatedResponse.getId());
                        }
                    } else {
                        displayStatus(mLatestRunStatus);
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case COMM_TASK:
                    if (!isAdded())
                        return;
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            CommTaskResponse commTaskResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    CommTaskResponse.class);

                            // Caution: there are 2 places to handle COMM_TASK "Succeeded"
                            if (commTaskResponse.getState().equals("Succeeded")) {
                                lastTimeRunTimeSucceed = System.currentTimeMillis();
                                mLatestRunStatus = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus();

                                switch (mCommand) {
                                    case AUTO:
                                    case SLEEP:
                                    case QUICK:
                                    case SILENT:
                                        mLatestRunStatus.setScenarioMode(mCommand);
                                        break;
                                    case OFF:
                                        mLatestRunStatus.setFanSpeedStatus("");
                                        mLatestRunStatus.setScenarioMode(mCommand);
                                        break;
                                    default:
                                        mLatestRunStatus.setFanSpeedStatus(mCommand);
                                        mLatestRunStatus.setScenarioMode("");
                                        break;
                                }
                                displayStatus(mLatestRunStatus);

                                // deprecated
//                                TccClient.sharedInstance().getDeviceStatus(mDeviceId, mSessionId,
//                                        RequestID.GET_DEVICE_STATUS, mReceiveResponse);

                                // stop commTaskPollingThread
                                mCommTaskCount = 1000;
                            }

                            mCommTaskCount++;
                            if (mCommTaskCount < COMM_TASK_TIMEOUT_COUNT) {
                                commTaskPollingThread();
                            } else {
                                // CommTaskCount timeout, display latest view
                                if (mCommTaskCount == COMM_TASK_TIMEOUT_COUNT) {
                                    displayStatus(mLatestRunStatus);
                                }

                                mCommTaskCount = 0;
                                isControllingDevice = false;
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RunStatus runStatusResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);
                            mCleanTimeArray = runStatusResponse.getCleanTime();
                            displayStatus(runStatusResponse);
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }

                    break;

                default:
                    break;
            }
        }
    };

    /**
     * CommTaskPollingThread
     * <p/>
     * If there are more than one thread polling at the same time, need to kill previous one.
     * If there are more than one new controlDevice thread, the latter one need to wait for a while
     * <p/>
     * CONTROL_DEVICE => COMM_TASK send - response => polling CommTaskThread -> sleep
     * => in polling thread COMM_TASK send - response => ... => succeed => GET_DEVICE_STATUS
     */
    private void commTaskPollingThread() {
        final IReceiveResponse commTaskResponse = new IReceiveResponse() {

            @Override
            public void onReceive(HTTPRequestResponse httpRequestResponse) {
                removeRequestId(httpRequestResponse.getRandomRequestID());
                switch (httpRequestResponse.getRequestID()) {
                    case COMM_TASK:
                        if (httpRequestResponse.getStatusCode() == StatusCode.OK) {

                            /*
                             * If CommTaskIdChanged, the old thread need to be killed.
                             * (mCommTaskCount != 0) means more than 1 thread running
                             */
                            if (isCommTaskIdChanged && (mCommTaskCount != 0)) {
                                isCommTaskIdChanged = false;
                                mCommTaskCount = 0;
                                return;
                            }

                            if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                                CommTaskResponse commTaskResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                        CommTaskResponse.class);

                                // Caution: there are 2 places to handle COMM_TASK "Succeeded"
                                if (commTaskResponse.getState().equals("Succeeded")) {
                                    lastTimeRunTimeSucceed = System.currentTimeMillis();
                                    mLatestRunStatus = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus();

                                    switch (mCommand) {
                                        case AUTO:
                                        case SLEEP:
                                        case QUICK:
                                        case SILENT:
                                            mLatestRunStatus.setScenarioMode(mCommand);
                                            break;
                                        case OFF:
                                            mLatestRunStatus.setFanSpeedStatus("");
                                            mLatestRunStatus.setScenarioMode(mCommand);
                                            break;
                                        default:
                                            mLatestRunStatus.setFanSpeedStatus(mCommand);
                                            mLatestRunStatus.setScenarioMode("");
                                            break;
                                    }
                                    displayStatus(mLatestRunStatus);

                                    // deprecated
//                                    TccClient.sharedInstance().getDeviceStatus(mDeviceId, mSessionId,
//                                            RequestID.GET_DEVICE_STATUS, mReceiveResponse);

                                    // stop commTaskPollingThread
                                    mCommTaskCount = 1000;

                                }

                                mCommTaskCount++;
                                if (mCommTaskCount < COMM_TASK_TIMEOUT_COUNT) {
                                    // start polling of COMM_TASK
                                    commTaskPollingThread();
                                } else {
                                    // CommTaskCount timeout, display latest view
                                    if (mCommTaskCount == COMM_TASK_TIMEOUT_COUNT) {
                                        displayStatus(mLatestRunStatus);
                                        errorLayout.startAnimation(translateInAnimation);
                                        errorTextView.setText(mControlTimeout);
                                    }
                                    mCommTaskCount = 0;
                                    isControllingDevice = false;
                                }

                            }
                        } else {
                            if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                                if (httpRequestResponse.getException() != null) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
                                } else {
                                    try {
                                        JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                                        JSONObject responseJSON = responseArray.getJSONObject(0);
                                        ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                                                ErrorResponse.class);
                                        LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "error：" + errorResponse.getMessage());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        break;


                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int commTaskRequestId = getRequestClient().getCommTask(mCommTaskId,
                        mSessionId, commTaskResponse);
                addRequestId(commTaskRequestId);
            }
        }).start();

    }

    private void handleCommTaskChangedAndCount(final int taskId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isUserInputTooQuick) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // if get a new commTaskId, need to kill previous polling thread, start a new one.
                if (mCommTaskId != taskId && (mCommTaskCount != 0)) {
                    isCommTaskIdChanged = true;
                }
                mCommTaskId = taskId;
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "commTaskId：" + mCommTaskId);
                int commTaskRequestId = getRequestClient().getCommTask(mCommTaskId,
                        mSessionId, mReceiveResponse);
                addRequestId(commTaskRequestId);
            }
        }).start();
    }

    View.OnClickListener cancelOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Umeng statistic
            MobclickAgent.onEvent(mActivity, "control_cancel_event");

            mActivity.finish();
            mActivity.overridePendingTransition(0, 0);
        }
    };

    View.OnClickListener filterOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mOnFilterClickListener.onClick();
        }
    };

    /**
     * Min/Max led control
     */
    View.OnClickListener minLedOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isPowerOn || !isMobileControl)
                return;

            if (ledSetting.getLastSettingLed() != 2) {
                minLedImageButton.setClickable(false);
                ledSetting.setSettingSpeed(1);
                ledSetting.setSettingLed(2);

                // show led view
                if (ledSetting.getSettingLed() > ledSetting.getLastSettingLed()) {
                    ledRising();
                } else if (ledSetting.getSettingLed() < ledSetting.getLastSettingLed()) {
                    ledFalling();
                }

                ledSetting.setLastSettingSpeed(1);
                ledSetting.setLastSettingLed(2);
                ledCheckBox.get(1).startAnimation(alphaOffAnimation);

                mCommand = "Speed_1";
                controlDevice();
                displayCleanTime(1);
            }
        }
    };

    View.OnClickListener maxLedOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isPowerOn || !isMobileControl)
                return;

            if (ledSetting.getLastSettingLed() != 14) {
                maxLedImageButton.setClickable(false);
                ledSetting.setSettingSpeed(7);
                ledSetting.setSettingLed(14);

                // show led view
                if (ledSetting.getSettingLed() > ledSetting.getLastSettingLed()) {
                    ledRising();
                } else if (ledSetting.getSettingLed() < ledSetting.getLastSettingLed()) {
                    ledFalling();
                }

                ledSetting.setLastSettingSpeed(7);
                ledSetting.setLastSettingLed(14);
                ledCheckBox.get(13).startAnimation(alphaOffAnimation);

                mCommand = "Speed_7";
                controlDevice();
                displayCleanTime(7);
            }
        }
    };

    /**
     * Power on/off control
     */
    View.OnClickListener powerOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isMobileControl) {
                errorLayout.startAnimation(translateInAnimation);
                errorTextView.setText(getString(R.string.device_not_control));
                return;
            }

            powerCheckBox.setButtonDrawable(R.drawable.power_on);
            powerCheckBox.setClickable(false);
            clearAllAnimation();
            powerCheckBox.startAnimation(alphaOffAnimation);

            if (powerCheckBox.isChecked()) {
                isSwitchPowerOn = true;
                isPowerOn = true;
                mCommand = AUTO;
                controlDevice();
                displayCleanTime(3);

            } else {
                isSwitchPowerOn = false;
                isPowerOn = false;
                mCommand = OFF;
                controlDevice();
                displayCleanTime(0);

                for (int i = 0; i < 14; i++) {
                    ledCheckBox.get(i).setChecked(false);
                }
                minLedImageButton.setClickable(false);
                maxLedImageButton.setClickable(false);
            }
        }
    };

    /**
     * Led speed control
     */
    private DeviceActivity.MyTouchListener mTouchListener = new DeviceActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (isPowerOn && !AppConfig.isFilterScrollPage) {
                        // Show led rising or falling when onMove (touch and move)
                        for (int i = 0; i < 14; i++) {
                            if ((ev.getX() > ledCheckBox.get(i).getLeft() - 3)
                                    && (ev.getX() < ledCheckBox.get(i).getRight() + 3)
                                    && (ev.getY() < ledCheckBox.get(i).getBottom() + 5)
                                    && (ev.getY() > ledCheckBox.get(i).getTop() - 5)) {
                                isLedOnMove = true;
                                ledSetting.setSettingLed(i + 1);

                                // show led view
                                if (ledSetting.getSettingLed() > ledSetting.getLastSettingLed()) {
                                    ledRising();
                                } else if (ledSetting.getSettingLed() < ledSetting.getLastSettingLed()) {
                                    ledFalling();
                                }
                                ledSetting.setLastSettingLed(ledSetting.getSettingLed());
                            }
                        } // end of for
                    } // end of if (isPowerOn && !AppConfig.isFilterScrollPage)

                    // disable scroll view if in control
                    if (!AppConfig.isFilterScrollPage) {
                        if ((ev.getX() > minLedImageButton.getLeft() - 3)
                                && (ev.getX() < maxLedImageButton.getRight() + 3)
                                && (ev.getY() < ledCheckBox.get(1).getBottom())
                                && (ev.getY() > ledCheckBox.get(7).getTop()) && !titleLayoutIsScroll()) {
                            mOnControlClickListener.onControl(true);
                        } else {
                            mOnControlClickListener.onControl(false);
                        }
                    }

                    break;
                case MotionEvent.ACTION_DOWN:
                    if (isPowerOn && !AppConfig.isFilterScrollPage) {
                        // Show led rising or falling when onTouch
                        for (int i = 0; i < 14; i++) {
                            if ((ev.getX() > ledCheckBox.get(i).getLeft() - 20)
                                    && (ev.getX() < ledCheckBox.get(i).getRight() + 20)
                                    && (ev.getY() < ledCheckBox.get(i).getBottom() + 20)
                                    && (ev.getY() > ledCheckBox.get(i).getTop() - 20)) {
                                isLedOnDown = true;
                                ledSetting.setSettingLed(i + 1);

                                // show led view
                                if (ledSetting.getSettingLed() > ledSetting.getLastSettingLed()) {
                                    ledRising();
                                } else if (ledSetting.getSettingLed() < ledSetting.getLastSettingLed()) {
                                    ledFalling();
                                }
                                ledSetting.setLastSettingLed(ledSetting.getSettingLed());
                            }
                        }
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if (isPowerOn && !AppConfig.isFilterScrollPage) {

                        // After showing, control speed when UP.
                        if ((isLedOnMove || (isLedOnDown))) {
                            isLedOnMove = false;
                            isLedOnDown = false;
                            clearAllAnimation();

                            // clear mode checked
                            modeRadioGroup.clearCheck();

                        /*
                         * For example, if led is 3, add to 4.
                         * If led is 6, do nothing.
                         */
                            int led = ledSetting.getSettingLed();
                            if ((led % 2 == 1)) {
                                ledSetting.setSettingLed(led + 1);
                                ledSetting.setLastSettingLed(led + 1);
                                ledSetting.setSettingSpeed((led + 1) / 2);
                                ledCheckBox.get(led).setChecked(true);

                                if (ledSetting.getSettingSpeed() != ledSetting.getLastSettingSpeed()) {
                                    ledCheckBox.get(led).startAnimation(alphaOffAnimation);
                                    ledSetting.setLastSettingSpeed((led + 1) / 2);
                                    mCommand = "Speed_" + (led + 1) / 2;
                                    controlDevice();
                                    displayCleanTime((led + 1) / 2);
                                }

                            } else {
                                if (led > 0) {
                                    ledSetting.setSettingSpeed(led / 2);
                                    if (ledSetting.getSettingSpeed() != ledSetting.getLastSettingSpeed()) {
                                        ledCheckBox.get(led - 1).startAnimation(alphaOffAnimation);
                                        ledSetting.setLastSettingSpeed(led / 2);
                                        mCommand = "Speed_" + led / 2;
                                        controlDevice();
                                        displayCleanTime(led / 2);
                                    }

                                    // for a special case: mode switch to manual
                                    if (ledSetting.getSettingSpeed() == ledSetting.getLastSettingSpeed() && isModeOn) {
                                        ledCheckBox.get(led - 1).startAnimation(alphaOffAnimation);
                                        ledSetting.setLastSettingSpeed(led / 2);
                                        mCommand = "Speed_" + led / 2;
                                        controlDevice();
                                        displayCleanTime(led / 2);
                                    }
                                }
                            }

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Function
     */
    private void ledRising() {
        for (int i = 0; i < 14; i++) {
            if (((i + 1) > ledSetting.getLastSettingLed())
                    && ((i + 1) <= ledSetting.getSettingLed())) {
                ledCheckBox.get(i).setChecked(true);
            }
        }

    }

    private void ledFalling() {
        for (int i = 13; i > 0; i--) {
            if (((i + 1) <= ledSetting.getLastSettingLed())
                    && ((i + 1) > ledSetting.getSettingLed())) {
                ledCheckBox.get(i).setChecked(false);
            }
        }
    }

    private void clearAllAnimation() {
        for (int i = 0; i < 14; i++) {
            ledCheckBox.get(i).clearAnimation();
        }
        powerCheckBox.clearAnimation();
        sleepRadioButton.clearAnimation();
        sleepRadioButton.setClickable(true);
        sleepRadioButton.setTextColor(cslModeOff);
        autoRadioButton.clearAnimation();
        autoRadioButton.setClickable(true);
        autoRadioButton.setTextColor(cslModeOff);
        quickRadioButton.clearAnimation();
        quickRadioButton.setClickable(true);
        quickRadioButton.setTextColor(cslModeOff);
        silentRadioButton.clearAnimation();
        silentRadioButton.setClickable(true);
        silentRadioButton.setTextColor(cslModeOff);
    }

    /**
     * Control device function
     */
    private void controlDevice() {
        errorLayout.startAnimation(translateInAnimation);
        errorTextView.setText(mDeviceControl);
        isControllingDevice = true;

        // If user input too quick
        CalculateUserInputGap();

        // remote control disabled
        if (!isMobileControl) {
            isControllingDevice = false;
            errorLayout.startAnimation(translateInAnimation);
            errorTextView.setText(getString(R.string.device_not_control));
            displayStatus(mLatestRunStatus);
            return;
        }

        DeviceControlRequest deviceControlRequest = new DeviceControlRequest();
        if (!mCommand.equals("")) {
            // This command combines mode and speed.
            deviceControlRequest.setFanModeSwitch(mCommand);
            mSessionId = AuthorizeApp.shareInstance().getSessionId();
            int controlDeviceRequestId = getRequestClient().controlDevice(mCurrentDevice
                            .getDeviceInfo().getDeviceID(), mSessionId, deviceControlRequest, mReceiveResponse);
            addRequestId(controlDeviceRequestId);
        }
    }

    /**
     * Parse device running status to speed and mode.
     * Display all status including led speed, mode, power, offline, control.
     * Display clean time for back home.
     * Display arriving home time
     * Clear all animation showing at the present.
     * Set mode and power button clickable.
     * Save latest running status.
     *
     * @param runStatusResponse
     */
    private void displayStatus(RunStatus runStatusResponse) {
        int speed = 0;

        if (runStatusResponse == null)
            return;

        clearAllAnimation();
        isPowerOn = true;
        mLatestRunStatus = runStatusResponse;
        powerCheckBox.setButtonDrawable(R.drawable.power_on);
        powerCheckBox.setClickable(true);
        powerCheckBox.setChecked(true);
        modeRadioGroup.setClickable(true);
        minLedImageButton.setClickable(!isControllingDevice);
        maxLedImageButton.setClickable(!isControllingDevice);

        // if mLatestRunStatus no data, click power is not allowed.
        if (mLatestRunStatus.getAqDisplayLevel() == null)
            powerCheckBox.setClickable(false);

        // Parse device status to speed and mode.
        String speedString = mLatestRunStatus.getFanSpeedStatus();
        String modeString = mLatestRunStatus.getScenarioMode();
        if (speedString != null) {
            if (speedString.contains("Speed")) {
                speed = Integer.parseInt(speedString.substring(6, 7));
            }
        }

        // show led view
        showLedView(speed);

        // clear mode checked
        modeRadioGroup.clearCheck();

        // show mode view
        switch (modeString) {
            case AUTO:
                autoRadioButton.setTextColor(cslModeOn);
                autoRadioButton.setClickable(false);
                speed = decideAutoSpeed();
                isModeOn = true;
                isSwitchPowerOn = false;
                showLedView(speed);
                break;

            case SLEEP:
                sleepRadioButton.setTextColor(cslModeOn);
                sleepRadioButton.setClickable(false);
                isModeOn = true;
                isSwitchPowerOn = false;
                speed = 1;
                showLedView(speed);
                break;

            case QUICK:
                quickRadioButton.setTextColor(cslModeOn);
                quickRadioButton.setClickable(false);
                isModeOn = true;
                isSwitchPowerOn = false;
                speed = 7;
                showLedView(speed);
                break;

            case SILENT:
                silentRadioButton.setTextColor(cslModeOn);
                silentRadioButton.setClickable(false);
                isModeOn = true;
                isSwitchPowerOn = false;
                speed = 2;
                showLedView(speed);
                break;

            case MANUAL:
                isModeOn = false;
                isSwitchPowerOn = false;

            case OFF:
                if (speed > 0)
                    break;
                powerCheckBox.setChecked(false);
                powerCheckBox.setButtonDrawable(R.drawable.power_off);
                ledSetting.setLastSettingLed(0);
                isPowerOn = false;
                isModeOn = false;
                isSwitchPowerOn = false;
                showLedView(0);
                break;

            default:
                break;
        }

        // device offline
        if (!mLatestRunStatus.getIsAlive()) {
            powerCheckBox.setChecked(false);
            powerCheckBox.setButtonDrawable(R.drawable.power_off);
            ledSetting.setLastSettingLed(0);
            powerCheckBox.setClickable(false);
            minLedImageButton.setClickable(false);
            maxLedImageButton.setClickable(false);
            isPowerOn = false;
            isModeOn = false;
            isSwitchPowerOn = false;
            showLedView(0);
            sleepRadioButton.setClickable(false);
            sleepRadioButton.setTextColor(cslModeOff);
            autoRadioButton.setClickable(false);
            autoRadioButton.setTextColor(cslModeOff);
            quickRadioButton.setClickable(false);
            quickRadioButton.setTextColor(cslModeOff);
            silentRadioButton.setClickable(false);
            silentRadioButton.setTextColor(cslModeOff);
            errorLayout.startAnimation(translateInAnimation);
            errorTextView.setText(mDeviceOffline);
        }

        // remote control enable/disable
        if (mLatestRunStatus.getMobileCtrlFlags() != null) {
            if (mLatestRunStatus.getMobileCtrlFlags().equals("ENABLED")) {
                isMobileControl = true;
            } else if (mLatestRunStatus.getMobileCtrlFlags().equals("DISABLED")) {
                isMobileControl = false;
            }
        }

        displayCleanTime(speed);
        displayArriveHome();
        ((AirTouchSeriesDevice)mCurrentDevice).setDeviceRunStatus(mLatestRunStatus);

        UserLocation userLocation = ((DeviceActivity) mActivity).getmUserLocation();
        ArrayList<AirTouchSeriesDevice> pm25List = userLocation.getAirTouchSeriesList();
        for (AirTouchSeriesDevice homeDevicePM25Item : pm25List) {
            if (homeDevicePM25Item.getDeviceInfo().getDeviceID() == mCurrentDevice.getDeviceInfo().getDeviceID()) {
                homeDevicePM25Item.setDeviceRunStatus(mLatestRunStatus);
                break;
            }
        }

    }

    /**
     * Display clean time for back home.
     *
     * @param speed - setting speed or running speed
     */
    private void displayCleanTime(int speed) {
        if (mCleanTimeArray == null)
            return;

        if (!mLatestRunStatus.getIsAlive())
            return;

        if (mLatestRunStatus.getMobileCtrlFlags() != null) {
            if (mLatestRunStatus.getMobileCtrlFlags().equals("DISABLED"))
                return;
        }

        if (mCleanTimeArray.length == 7 && mLatestRunStatus.isCleanBeforeHomeEnable()) {
            if (speed > 0) {
                int cleanTime = mCleanTimeArray[speed - 1];
                if (cleanTime > 60) {
                    cleanTime /= 60;
                    cleanUpTextView.setText(String.format
                            (getString(R.string.clean_time_hour), cleanTime));
                } else if (cleanTime > 0) {
                    cleanUpTextView.setText(String.format
                            (getString(R.string.clean_time_minute), cleanTime));
                }
                return;
            }
        }
        cleanUpTextView.setText("");
    }

    /**
     * Display arrive home time for back home.
     */
    private void displayArriveHome() {
        if (mCleanTimeArray == null || mLatestRunStatus == null)
            return;

        if (!mLatestRunStatus.getIsAlive())
            return;

        if (mLatestRunStatus.getMobileCtrlFlags() != null) {
            if (mLatestRunStatus.getMobileCtrlFlags().equals("DISABLED"))
                return;
        }

        if (mLatestRunStatus.isCleanBeforeHomeEnable()) {
            Calendar calendar = Calendar.getInstance();
            int hour = Integer.parseInt(mLatestRunStatus.getTimeToHome().substring(11, 13));
            int minute = Integer.parseInt(mLatestRunStatus.getTimeToHome().substring(14, 16));
            int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
            int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
            SimpleDateFormat sdf = new SimpleDateFormat("HH':'mm");
            String settingTime = sdf.format(calendar.getTime());
            arriveHomeTextView.setText(getString(R.string.arriving_home) + " " + settingTime);
        } else {
            arriveHomeTextView.setText("");
        }
    }


    /**
     * If user input is too quick,
     * set (isPowerOn = false) to forbid input until displayStatus()
     */
    private void CalculateUserInputGap() {
        long timeDeviation = System.currentTimeMillis() - lastTimeUserInput;
        if (isUserInputTooQuick) {
            isUserInputTooQuick = false;

            isPowerOn = false;
            powerCheckBox.setClickable(false);
            errorLayout.startAnimation(translateInAnimation);
            errorTextView.setText(getString(R.string.input_quick));

        }

        if (timeDeviation < USER_INPUT_TOO_QUICK) {
            tooQuickCount++;
            if (tooQuickCount >= 2) {
                tooQuickCount = 0;
                isUserInputTooQuick = true;
            }
        }
        lastTimeUserInput = System.currentTimeMillis();
    }

    /**
     * Animation helper
     */
    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            errorLayout.startAnimation(translateOutAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
            cancelLayout.setVisibility(View.INVISIBLE);
            deviceNameTextView.setVisibility(View.INVISIBLE);
        }
    }

    private class translateOutAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            cancelLayout.setVisibility(View.VISIBLE);
            deviceNameTextView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }

    private MessageBox.MyOnClick quit = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mActivity.finish();
        }
    };


    private void showLedView(int speed) {
        if (speed > 0) {
            for (int i = 0; i < 14; i++) {
                if (i <= (speed * 2 - 1)) {
                    ledCheckBox.get(i).setChecked(true);
                } else {
                    ledCheckBox.get(i).setChecked(false);
                }
            }
        } else if (speed == 0) {
            for (int i = 0; i < 14; i++) {
                ledCheckBox.get(i).setChecked(false);
            }
        }
        ledSetting.setLastSettingSpeed(speed);
        ledSetting.setLastSettingLed(speed * 2);
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        isControllingDevice = false;

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            errorLayout.startAnimation(translateInAnimation);
            errorTextView.setText(getString(R.string.enroll_error));
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
                errorLayout.startAnimation(translateInAnimation);
                errorTextView.setText(mError);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
//                errorTextView.setText(errorResponse.getMessage());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            errorLayout.startAnimation(translateInAnimation);
            errorTextView.setText(mError);
        }
    }

    private Boolean isHasNullPoint() {
        return mCurrentDevice == null
                || ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus() == null || mCurrentDevice.getDeviceInfo() == null;
    }

    private int decideAutoSpeed() {
        if (isSwitchPowerOn) {
            return 3;
        } else {
            if (((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getmPM25Value() < 75) {
                return 1;
            } else if (((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getmPM25Value() < 150) {
                return 3;
            } else {
                return 6;
            }
        }
    }

    /**
     * the listener of onControl clicked
     */
    public interface OnControlClickListener {
        public void onControl(boolean isOnControl);
    }

    public void setOnControlClickListener(OnControlClickListener onControlClickListener) {
        mOnControlClickListener = onControlClickListener;
    }

    /**
     * the listener of filter clicked
     */
    public interface OnFilterClickListener {
        public void onClick();
    }

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        mOnFilterClickListener = onFilterClickListener;
    }

    public void setFilterAnimation(int filter) {
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.pre_filter_bar);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.pm25_filter);
        ColorStateList csl3 = (ColorStateList) getResources().getColorStateList(R.color.hisiv_filter);

        switch (filter) {
            case 1:
                preFilterTextView.startAnimation(alphaOffAnimation);
                preFilterTextView.setTextColor(csl1);
                break;

            case 2:
                pm25FilterTextView.startAnimation(alphaOffAnimation);
                pm25FilterTextView.setTextColor(csl2);
                break;

            case 3:
                hisivFilterTextView.startAnimation(alphaOffAnimation);
                hisivFilterTextView.setTextColor(csl3);
                break;

            default:
                break;
        }
    }

}
