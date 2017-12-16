//package com.honeywell.hch.airtouch.app.airtouch.controller.enrollment;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.res.ColorStateList;
//import android.graphics.drawable.AnimationDrawable;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.google.gson.Gson;
//import com.honeywell.hch.airtouch.R;
//import com.honeywell.hch.airtouch.app.airtouch.model.dbmodel.City;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.control.GroupCommTaskResponse;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.request.AddLocationRequest;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.request.DeviceRegisterRequest;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response.ErrorResponse;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response.GatewayAliveResponse;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response.HomeDevicePM25;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
//import com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response.UserLocation;
//import com.honeywell.hch.airtouch.app.authorize.AuthorizeApp;
//import com.honeywell.hch.airtouch.framework.app.activity.BaseActivity;
//import com.honeywell.hch.airtouch.framework.config.AppConfig;
//import com.honeywell.hch.airtouch.framework.database.CityChinaDBService;
//import com.honeywell.hch.airtouch.framework.enrollment.models.DIYInstallationState;
//import com.honeywell.hch.airtouch.framework.global.AirTouchConstants;
//import com.honeywell.hch.airtouch.framework.model.DeviceInfo;
//import com.honeywell.hch.airtouch.framework.model.UserLocationData;
//import com.honeywell.hch.airtouch.framework.model.modelinterface.IRefreshEnd;
//import com.honeywell.hch.airtouch.framework.view.AirTouchEditText;
//import com.honeywell.hch.airtouch.framework.view.MessageBox;
//import com.honeywell.hch.airtouch.framework.webservice.StatusCode;
//import com.honeywell.hch.airtouch.framework.webservice.TccClient;
//import com.honeywell.hch.airtouch.lib.http.HTTPRequestResponse;
//import com.honeywell.hch.airtouch.lib.http.IReceiveResponse;
//import com.honeywell.hch.airtouch.lib.http.RequestID;
//import com.honeywell.hch.airtouch.lib.location.CityInfo;
//import com.honeywell.hch.airtouch.lib.location.LocationManager;
//import com.honeywell.hch.airtouch.lib.util.LogUtil;
//import com.honeywell.hch.airtouch.lib.util.StringUtil;
//import com.honeywell.hch.airtouch.lib.util.WifiUtil;
//import com.umeng.analytics.MobclickAgent;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Created by Jin Qian on 1/26/2015.
// * GPS City means located position by GPS or selected by user
// * Home name equals to TCC location name
// * This Class is to add new location and new device bond to user account
// */
//public class LocationAndDeviceRegisterActivity extends BaseActivity implements WifiUtil.IWifiOpen {
//    private static String TAG = "AirTouchLoc&Dev";
//    public static final int SELECT_LOCATION_REQUEST = 10;
//
//    private boolean isHasAutoConnectWifi = false;
//    private boolean isMacAlreadyRegistered = false;
//    private boolean isHomeNumberReachMax = false;
//    private boolean isDeviceNumberReachMax = false;
//    private boolean isAddingDevice = false;
//    private boolean isNetworkRetry = false;
//    private boolean isGetLocationFinished = false;
//    private boolean isMacPollingFinished = false;
//    private boolean isMacAlive = false;
//    private boolean isButtonClickedWaitPollingDone = false;
//    private boolean isHomeEditTextFocused = false;
//
//    private View gpsPromptView;
//    private View homePromptView;
//    private ImageView gpsLoadingImageView;
//    private ImageView homeLoadingImageView;
//    private ImageView gpsSelectImageView;
//    private ImageView gpsLineImageView;
//    private ImageView homeLineImageView;
//    private TextView locationTextView;
//    private TextView cityTextView;
//    private TextView homeTextView;
//    private AirTouchEditText userLocationEditText;
//    private AirTouchEditText userDevice;
//    private LinearLayout homeLayout;
//    private LinearLayout homeNameLayout;
//    private LinearLayout homeSelectLayout;
//    private LinearLayout deviceLayout;
//    private Spinner homeSpinner;
//    private HomeSpinnerArrayAdapter<String> homeSpinnerTypeAdapter;
//    private Button doneButton;
//    private AlertDialog mAlertDialog;
//
//    private String mUserId;
//    private int mLocationId;
//    private String mSessionId;
//    private String mSelectedHomeName;
//    private String mUserHomeName;
//    private City mSelectedGPSCity;
//    private static ProgressDialog mDialog;
//    private WifiManager mWifi;
//    private String mMacId = "";
//    private String mCrcId;
//    private int checkMacPollingTime = 0;
//    private AddLocationRequest addLocationRequest;
//    private ArrayList<String> homeStrings = new ArrayList<>();
//    //    private ArrayList<UserLocation> locationResponses = new ArrayList<>();
//    private ArrayList<UserLocationData> mUserLocations = new ArrayList<>();
//    private ArrayList<ArrayList<HomeDevicePM25>> homeDevicesList = new ArrayList<>();
//    private static final int GPS_TIMEOUT = 20 * 1000;
//    private static final int CHECK_MAC_TIMES = 10;
//    private CityChinaDBService mCityDBService;
//    private int mCommTaskId;
//    private int mUserHomeNumber;
//    private int mCommTaskCount = 0;
//    private int getHomePm25Count = 0;
//    private static final int COMM_TASK_TIMEOUT_COUNT = 60;
//    private static final int MAX_HOME_NUMBER = 5;
//
//    private boolean isAddHome = false;
//
//    //when cmm task is retry
//    private boolean isCmmtaskRetry = true;
//
//    private int mHomeAirTouchSeriesDeviceNumber = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location_device_register);
//
//        super.TAG = TAG;
//        initView();
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
//            initWifi();
//        startGPStoGetCity();
//
//        /*
//         *  Step 0 - init Wifi
//         *  Step 1 - Start GPS
//         *  Step 2 - Get location & Check device after GPS finish
//         *  Step 3 - Add device (Click button)
//         */
//
//        mCityDBService = new CityChinaDBService(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (mAlertDialog != null)
//            mAlertDialog.dismiss();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//            if (mScanResultsReceiver != null) {
//                unregisterReceiver(mScanResultsReceiver);
//            }
//        }
//
//        checkMacPollingTime = 0;
//        isMacAlive = false;
//        isMacPollingFinished = false;
//        isGetLocationFinished = false;
//        isButtonClickedWaitPollingDone = false;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
//            MessageBox.createTwoButtonDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.enroll_quit), getString(R.string.yes),
//                    quitEnroll, getString(R.string.no), null);
//        }
//
//        return false;
//    }
//
//    private void initView() {
//        locationTextView = (TextView) findViewById(R.id.enroll_gps_tv);
//        cityTextView = (TextView) findViewById(R.id.enroll_city_tv);
//        cityTextView.setVisibility(View.INVISIBLE);
//        cityTextView.setOnClickListener(cityClick);
//        homeTextView = (TextView) findViewById(R.id.enroll_home_tv);
//        gpsPromptView = findViewById(R.id.gps_loading_image);
//        homePromptView = findViewById(R.id.home_loading_image);
//        gpsLoadingImageView = (ImageView) findViewById(R.id.gps_loading_image);
//        homeLoadingImageView = (ImageView) findViewById(R.id.home_loading_image);
//        gpsSelectImageView = (ImageView) findViewById(R.id.enroll_gps_select);
//        gpsLineImageView = (ImageView) findViewById(R.id.enroll_gps_line);
//        homeLineImageView = (ImageView) findViewById(R.id.enroll_home_line);
//        gpsLineImageView.setVisibility(View.INVISIBLE);
//        userLocationEditText = (AirTouchEditText) findViewById(R.id.home_city_et);
//        userLocationEditText.getEditText().addTextChangedListener(mEditTextWatch);
//        userLocationEditText.getEditText().setOnFocusChangeListener(locationOnFocus);
//        userLocationEditText.setInputMaxLength(14);
//        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.enroll_light_grey);
//        userLocationEditText.getEditText().setHintTextColor(csl);
//        userDevice = (AirTouchEditText) findViewById(R.id.enroll_device_et);
//        userDevice.getEditText().setHintTextColor(csl);
//        userDevice.getEditText().setOnFocusChangeListener(deviceOnFocus);
//        userDevice.setInputMaxLength(14);
//        homeLayout = (LinearLayout) findViewById(R.id.enroll_location_ll);
//        homeLayout.setVisibility(View.INVISIBLE);
//        homeNameLayout = (LinearLayout) findViewById(R.id.enroll_location_home_ll);
//        homeSelectLayout = (LinearLayout) findViewById(R.id.enroll_location_home_select_ll);
//        deviceLayout = (LinearLayout) findViewById(R.id.enroll_device_ll);
//        deviceLayout.setVisibility(View.INVISIBLE);
//        homeSpinner = (Spinner) findViewById(R.id.home_spinner);
//        doneButton = (Button) findViewById(R.id.doneBtn);
//        doneButton.setVisibility(View.INVISIBLE);
//        doneButton.setOnClickListener(doneOnClick);
//        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        mWifi.disconnect();
//        mWifi.startScan();
//        //Start GPS loading
//        gpsPromptView.setVisibility(View.VISIBLE);
//        AnimationDrawable anim = (AnimationDrawable) gpsLoadingImageView.getBackground();
//        anim.start();
//
//        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
//            mMacId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
//            mCrcId = DIYInstallationState.getWAPIDeviceResponse().getCrcID();
//        }
//
//        if (AppConfig.isDebugMode) {
//            if (mMacId.equals("")) {
//                mMacId = "144146000018";
//                mCrcId = "D0B7";
//            }
//        }
//        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "macId：" + mMacId);
//
//    }
//
//    /**
//     * get add device error in a method.because if before add device error,user may add
//     * a home successfully,so need to get all location if user have add a new home,then,
//     * call this method after get all location
//     * @param httpRequestResponse
//     */
//    private void addDeviceErrorHandle(HTTPRequestResponse httpRequestResponse)
//    {
//        if (httpRequestResponse.getStatusCode() == StatusCode.BAD_REQUEST) {
//        if (mDialog != null) {
//            mDialog.dismiss();
//        }
//        MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                getString(R.string.device_already_registered), null, quitEnroll);
//
//        // Umeng statistic
//        Map<String, String> map = new HashMap<>();
//        map.put("userId", mUserId + "_" + mMacId + "_" + getString(R.string.device_already_registered));
////        map.put("macId", mMacId);
////        map.put("errorType", getString(R.string.device_already_registered));
//        MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_fail", map);
//
//    } else {
//        if (mDialog != null) {
//            mDialog.dismiss();
//        }
//
//        homePromptView.setVisibility(View.INVISIBLE);
//        homeTextView.setText(getString(R.string.wait_for_connection));
//        homeSelectLayout.setVisibility(View.INVISIBLE);
//        homeLineImageView.setVisibility(View.INVISIBLE);
//        deviceLayout.setVisibility(View.INVISIBLE);
//        doneButton.setVisibility(View.INVISIBLE);
//
////                        errorHandle(httpRequestResponse);
//        MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                getString(R.string.enroll_error), null, quitEnroll);
//
//        // Umeng statistic
//        Map<String, String> map = new HashMap<>();
//        map.put("userId", mUserId + "_" + mMacId + "_" + getString(R.string.enroll_error));
////        map.put("macId", mMacId);
////        map.put("errorType", getString(R.string.enroll_error));
//        MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_fail", map);
//
//        if (httpRequestResponse.getException() != null) {
//            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
//        }
//
//        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//            try {
//                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                JSONObject responseJSON = responseArray.getJSONObject(0);
//                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
//                        ErrorResponse.class);
//                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    }
//
//    /**
//     * HTTP Response handle
//     */
//    IReceiveResponse getTCCResponse = new IReceiveResponse() {
//
//        @Override
//        public void onReceive(HTTPRequestResponse httpRequestResponse) {
//            switch (httpRequestResponse.getRequestID()) {
//                case GET_LOCATION:
//                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            try {
//                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                                mUserHomeNumber = responseArray.length();
//                                isHomeNumberReachMax = mUserHomeNumber >= MAX_HOME_NUMBER;
//                                mUserLocations.clear();
//                                homeDevicesList.clear();
//                                if (isAddingDevice || isAddHome) {
//                                    isAddingDevice = false;
//                                    for (int i = 0; i < responseArray.length(); i++) {
//                                        JSONObject responseJSON = responseArray.getJSONObject(i);
//                                        final UserLocation getLocationResponse = new Gson().fromJson(responseJSON.toString(),
//                                                UserLocation.class);
//                                        mUserLocations.add(getLocationResponse);
//                                        // get devices of each home
//                                        TccClient.sharedInstance().getHomePm25
//                                                (getLocationResponse.getLocationID(), mSessionId,
//                                                        getTCCResponse);
//                                        getLocationResponse.loadHomeDevicesData(new IRefreshEnd()
//                                        {
//                                            @Override
//                                            public void notifyDataRefreshEnd(RequestID requestID)
//                                            {
//                                               if (requestID == RequestID.GET_DEVICE_STATUS){
//                                                   mHomeAirTouchSeriesDeviceNumber++;
//                                                   if (mHomeAirTouchSeriesDeviceNumber ==
//                                                           getLocationResponse
//                                                                   .getAirTouchSDeviceNumber())
//                                                   {
//                                                       getHomePm25End();
//                                                   }
//
//                                               }
//                                            }
//                                        });
//                                    }
//                                } else {
//                                    for (int i = 0; i < responseArray.length(); i++) {
//                                        JSONObject responseJSON = responseArray.getJSONObject(i);
//                                        UserLocation getLocationResponse = new Gson().fromJson(responseJSON.toString(),
//                                                UserLocation.class);
//                                        mUserLocations.add(getLocationResponse);
//                                        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GET_LOCATION：" + mUserLocations.get(i).getName());
//                                        ArrayList<DeviceInfo> deviceInfos = mUserLocations.get(i).getDeviceInfo();
//                                        for (int j = 0; j < deviceInfos.size(); j++) {
//                                            if (deviceInfos.get(j).getMacID().equals(mMacId)) {
//                                                isMacAlreadyRegistered = true;
//                                                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "device already registered by this user.");
//                                            }
//                                        }
//                                    }
//                                }
//                                AuthorizeApp.shareInstance().setUserLocations(mUserLocations);
//                                //old location has been cleared,so need to reset one as current home
//                                AuthorizeApp.shareInstance().resetCurrentHome();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            if (mDialog != null) {
//                                mDialog.dismiss();
//                            }
//                        }
//
//                        homeFinishView();
//                        showHomeSpinner();
//
//                    } else {
//                        if (mDialog != null) {
//                            mDialog.dismiss();
//                        }
//
//                        homePromptView.setVisibility(View.INVISIBLE);
//                        homeTextView.setText(getString(R.string.wait_for_connection));
//                        homeSelectLayout.setVisibility(View.INVISIBLE);
//                        homeLineImageView.setVisibility(View.INVISIBLE);
//                        deviceLayout.setVisibility(View.INVISIBLE);
//                        doneButton.setVisibility(View.INVISIBLE);
//
//                        if (httpRequestResponse.getException() != null) {
//                            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
////                            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                                    getString(R.string.no_network), null, null);
//                        }
//
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            try {
//                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                                JSONObject responseJSON = responseArray.getJSONObject(0);
//                                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
//                                        ErrorResponse.class);
//                                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                                        getString(R.string.enroll_error), null, null);
////                                MessageBox.createTwoButtonDialog(LocationAndDeviceRegisterActivity.this, null,
////                                        getString(R.string.enroll_error),
////                                        getString(R.string.retry), retryGetLocation, getString(R.string.cancel), null);
//                                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            /*
//                             * It is possible to lose network after enrolling device
//                             * Let user make sure network is available before retry
//                             */
//                            homeTextView.setText(R.string.wait_for_connection);
//                            mAlertDialog = MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                                    getString(R.string.network_retry), null, null);
////                            homeTextView.setText(R.string.network_retry);
//                            doneButton.setVisibility(View.VISIBLE);
//                            doneButton.setText(R.string.reconnect);
//                            isNetworkRetry = true;
//                        }
//                    }
//                    break;
//
//                case ADD_LOCATION:
//                    if (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK) {
//                        if (mDialog != null) {
//                            mDialog.dismiss();
//                        }
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(httpRequestResponse.getData(),
//                                    RecordCreatedResponse.class);
//                            mLocationId = recordCreatedResponse.getId();
//                            isAddHome = true;
//
//
//                            /*
//                             * It's time to add device here
//                             * because we just add a new location and get a locationId here
//                             */
//                            addDevice(getTCCResponse);
//                        }
//                    } else {
//                        homePromptView.setVisibility(View.INVISIBLE);
//                        homeTextView.setText(getString(R.string.wait_for_connection));
//                        homeSelectLayout.setVisibility(View.INVISIBLE);
//                        homeLineImageView.setVisibility(View.INVISIBLE);
//                        deviceLayout.setVisibility(View.INVISIBLE);
//                        doneButton.setVisibility(View.INVISIBLE);
//
//                        errorHandle(httpRequestResponse);
//                    }
//                    break;
//
//                case ADD_DEVICE:
//                    if (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK) {
//                        Log.e("hehe","httpRequestResponse.getData = " + StringUtil.isEmpty(httpRequestResponse.getData()));
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(httpRequestResponse.getData(),
//                                    RecordCreatedResponse.class);
//
//                            mCommTaskId = recordCreatedResponse.getId();
//                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "commTaskId：" + mCommTaskId);
//                            TccClient.sharedInstance().getCommTask(mCommTaskId, mSessionId, getTCCResponse);
//
//
//                        }
//                    }
//                    else if (isAddHome)
//                    {
//                        //if add home successfully ,we need to get all location
//                        // Use userId & sessionId received from Login to get TCC location data.
//                        mUserId = AuthorizeApp.shareInstance().getUserID();
//                        mSessionId = AuthorizeApp.shareInstance().getSessionId();
//                        TccClient.sharedInstance().getLocation(mUserId, mSessionId, getTCCResponse);
//                    }
//                    else
//                    {
//                        addDeviceErrorHandle(httpRequestResponse);
//                    }
//                    break;
//
//                case CHECK_MAC:
//                    if ((httpRequestResponse.getStatusCode() == StatusCode.OK)
//                            || (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK)) {
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            GatewayAliveResponse gatewayAliveResponse = new Gson().fromJson(httpRequestResponse.getData(),
//                                    GatewayAliveResponse.class);
//
//                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "CHECK_MAC:" + gatewayAliveResponse.isAlive());
//                            if (gatewayAliveResponse.isAlive()) {
//                                isMacPollingFinished = true;
//                                isMacAlive = true;
//
//                                /*
//                                 * When to add device to TCC depends on both MacPolling and getLocation thread.
//                                 * If MacPolling finishes before getLocation, addDeviceToTCC() after getLocation.(user click button)
//                                 * If getLocation finishes before MacPolling, addDeviceToTCC() after MacPolling.
//                                 */
//                                if (isGetLocationFinished) {
//                                    if (mDialog != null)
//                                        mDialog.dismiss();
//
//                                    if (isButtonClickedWaitPollingDone) {
//                                        isButtonClickedWaitPollingDone = false;
//                                        addDeviceToTCC();
//                                    }
//
//                                }
//
//                            } else {
//                                if (checkMacPollingTime == CHECK_MAC_TIMES) {
//                                    if (mDialog != null)
//                                        mDialog.dismiss();
//
//                                    isMacAlive = false;
//                                    isMacPollingFinished = true;
//                                    checkMacPollingTime = 0;
//                                    MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                                            getString(R.string.device_not_alive), null, null);
//                                } else {
//                                    checkMacPollingTime++;
//                                    checkMacPolling();
//                                }
//                            }
//                        }
//                    } else {
//                        if (mDialog != null)
//                            mDialog.dismiss();
//
//                        isMacAlive = false;
//                        isMacPollingFinished = true;
//                    }
//                    break;
//
////                case GET_HOME_PM25:
////                    ArrayList<HomeDevicePM25> homeDevices = new ArrayList<>();
////                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
////                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
////                            try {
////                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
////                                for (int i = 0; i < responseArray.length(); i++) {
////                                    JSONObject responseJSON = responseArray.getJSONObject(i);
////                                    HomeDevicePM25 device = new Gson().fromJson(responseJSON.toString(),
////                                            HomeDevicePM25.class);
////                                    homeDevices.add(device);
////                                }
////                                homeDevicesList.add(homeDevices);
////
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
////                        }
////
////                        /*
////                         * 1) GET_LOCATION - get all homes
////                         * 2) GET_HOME_PM25 - get all devices in each home
////                         * 3) save data of devices into each home
////                         */
////                        getHomePm25Count++;
////                        if (getHomePm25Count == mUserHomeNumber) {
////                            if (mDialog != null) {
////                                mDialog.dismiss();
////                            }
////                            for (int i = 0; i < mUserHomeNumber; i++) {
////                                AuthorizeApp.shareInstance().getUserLocations().get(i)
////                                        .setHomeDevicesPM25(homeDevicesList.get(i));
////                            }
////                            //send broadcast to MainActivity to refresh home page
////                            Intent intent = new Intent(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
////                            intent.putExtra(AirTouchConstants.IS_ADD_HOME,isAddHome);
////                            intent.putExtra(AirTouchConstants.LOCAL_ID, mLocationId);
////                            sendBroadcast(intent);
////
////                            if (!isCmmtaskRetry)
////                            {
////                                // finish add device
////                                finish();
////                                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
////                            }
////                            else
////                            {
////                                if (mDialog != null) {
////                                    mDialog.dismiss();
////                                }
////
////                                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                                        getString(R.string.add_device_fail), null, quitEnroll);
////                            }
////
////                        }
////
////                    } else {
////                        //send broadcast to MainActivity to refresh home page
////                        Intent intent = new Intent(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
////                        intent.putExtra(AirTouchConstants.IS_ADD_HOME, isAddHome);
////                        intent.putExtra(AirTouchConstants.LOCAL_ID, mLocationId);
////                        sendBroadcast(intent);
////                        if(!isCmmtaskRetry)
////                        {
////                            errorHandle(httpRequestResponse);
////                        }
////                        else
////                        {
////                            if (mDialog != null) {
////                                mDialog.dismiss();
////                            }
////
////                            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                                    getString(R.string.add_device_fail), null, quitEnroll);
////                        }
////                    }
////                    break;
//
//                case COMM_TASK:
//                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            GroupCommTaskResponse commTaskResponse = new Gson().fromJson(httpRequestResponse.getData(),
//                                    GroupCommTaskResponse.class);
//                            if (commTaskResponse.getState().equals("Succeeded")) {
//                                // Umeng statistic
//                                Map<String, String> map = new HashMap<>();
//                                map.put("userId", mUserId + "_" + mMacId);
////                                map.put("macId", mMacId);
//                                MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_succeed", map);
//
//                                isAddingDevice = true;
//                                isCmmtaskRetry = false;
//                                TccClient.sharedInstance().getLocation(mUserId, mSessionId, getTCCResponse);
//                                // stop commTaskPollingThread
//                                mCommTaskCount = 100;
//                            }
//
//                            if (commTaskResponse.getState().equals("Failed")) {
//                                isCmmtaskRetry = true;
//
//                                //if add home success ,need to get all Localtion
//                                if (isAddHome) {
//                                    mUserId = AuthorizeApp.shareInstance().getUserID();
//                                    mSessionId = AuthorizeApp.shareInstance().getSessionId();
//                                    TccClient.sharedInstance().getLocation(mUserId, mSessionId, getTCCResponse);
//
//                                } else {
//                                    if (mDialog != null) {
//                                        mDialog.dismiss();
//                                    }
//
//                                    MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                                            getString(R.string.add_device_fail), null, quitEnroll);
//                                }
//
//
//
//                                // Umeng statistic
//                                Map<String, String> map = new HashMap<>();
//                                map.put("userId", mUserId + "_" + mMacId + "_" + "commTaskResponse Failed");
////                                map.put("macId", mMacId);
////                                map.put("errorType", "commTaskResponse Failed");
//                                MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_failed", map);
//
////                                if (mDialog != null) {
////                                    mDialog.dismiss();
////                                }
////
////                                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                                        getString(R.string.add_device_fail), null, quitEnroll);
//                                // stop commTaskPollingThread
//                                mCommTaskCount = 100;
//                            }
//
//                            mCommTaskCount++;
//                            if (mCommTaskCount < COMM_TASK_TIMEOUT_COUNT) {
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            Thread.sleep(2000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        TccClient.sharedInstance().getCommTask(mCommTaskId,
//                                                mSessionId, getTCCResponse);
//                                    }
//                                }).start();
//                            } else {
//                                if (mCommTaskCount == COMM_TASK_TIMEOUT_COUNT) {
//                                    // Umeng statistic
//                                    Map<String, String> map = new HashMap<>();
//                                    map.put("userId", mUserId + "_" + mMacId + "_" + "commTaskResponse time out");
////                                    map.put("macId", mMacId);
////                                    map.put("errorType", "commTaskResponse time out");
//                                    MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_failed", map);
//
//                                    if (mDialog != null) {
//                                        mDialog.dismiss();
//                                    }
//                                    MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                                            getString(R.string.control_timeout), null, null);
//                                }
//                                mCommTaskCount = 0;
//                            }
//                        } else {
//                            if (mDialog != null) {
//                                mDialog.dismiss();
//                            }
//                        }
//                    } else {
//                        errorHandle(httpRequestResponse);
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
//
//
//    private void getHomePm25End(){
//        if (mDialog != null) {
//            mDialog.dismiss();
//        }
//
//        //send broadcast to MainActivity to refresh home page
//        Intent intent = new Intent(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
//        intent.putExtra(AirTouchConstants.IS_ADD_HOME,isAddHome);
//        intent.putExtra(AirTouchConstants.LOCAL_LOCATION_ID, mLocationId);
//        sendBroadcast(intent);
//
//        if (!isCmmtaskRetry)
//        {
//            // finish add device
//            finish();
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//        }
//        else
//        {
//            if (mDialog != null) {
//                mDialog.dismiss();
//            }
//
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.add_device_fail), null, quitEnroll);
//        }
//
//    }
//
//    private MessageBox.MyOnClick retryGetLocation = new MessageBox.MyOnClick() {
//        @Override
//        public void onClick(View v) {
//            homeTextView.setText(R.string.enroll_loading);
//            getLocationFromTCC();
//        }
//    };
//
//    private MessageBox.MyOnClick retryAddLocation = new MessageBox.MyOnClick() {
//        @Override
//        public void onClick(View v) {
//            homeTextView.setText(R.string.enroll_loading);
//            addLocation(getTCCResponse);
//        }
//    };
//
//    private MessageBox.MyOnClick retryAddDevice = new MessageBox.MyOnClick() {
//        @Override
//        public void onClick(View v) {
//            homeTextView.setText(R.string.enroll_loading);
//            addDevice(getTCCResponse);
//        }
//    };
//
//    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
//        @Override
//        public void onClick(View v) {
//            // Umeng statistic
////            Map<String, String> map = new HashMap<>();
////            map.put("userId", AuthorizeApp.shareInstance().getUserID());
////            MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "cancel_enroll_event", map);
//
//            finish();
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//        }
//    };
//
//    private void startGPStoGetCity() {
//        LocationManager.getInstance()
//                .registerGPSLocationListener(mMessageHandler);
//
//        //Start GPS timeout thread
//        TimeoutCheckThread timeoutCheckThread = new TimeoutCheckThread(this);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
//            timeoutCheckThread
//                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            timeoutCheckThread.execute("");
//        }
//    }
//
//    /**
//     * Receive GPS location update
//     */
//    private Handler mMessageHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg == null)
//                return;
//            switch (msg.what) {
//                //GPS find the city
//                case LocationManager.HANDLER_GPS_LOCATION:
//                    Bundle bundle = msg.getData();
//                    if (bundle != null) {
//                        CityInfo cityLocation = (CityInfo) bundle
//                                .getSerializable(LocationManager.HANDLER_MESSAGE_KEY_GPS_LOCATION);
//                        if (cityLocation != null) {
//                            processLocation(cityLocation);
//                        }
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
//
//
//    /**
//     * We already get the GPS city location info. This method check whether need
//     * to alert user to switch city, or update current GPS location.
//     *
//     * @param cityLocation
//     */
//    private void processLocation(CityInfo cityLocation) {
//        if (cityLocation != null) {
//            cityTextView.setVisibility(View.VISIBLE);
//            City city = mCityDBService.getCityByName(cityLocation.getCity());
//            if (city != null) {
//                mSelectedGPSCity = city;
//                cityTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ?
//                        city.getNameZh() : city.getNameEn());
//            }
//
//            gpsFinishView();
//
//            // Step 2 - Get location & Check device after GPS finish
//            checkMacPolling();
//            getLocationFromTCC();
//
//        }
//    }
//
//
//    /**
//     * 20S GPS timeout
//     */
//    private class TimeoutCheckThread extends
//            AsyncTask<String, Integer, String> {
//
//        private LocationAndDeviceRegisterActivity mLocAndDevRegAct;
//
//        public TimeoutCheckThread(LocationAndDeviceRegisterActivity locAndDevRegAct) {
//            mLocAndDevRegAct = locAndDevRegAct;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            Date date1 = new Date();
//            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating start：" + date1.toLocaleString());
//
//            try {
//                Thread.sleep(GPS_TIMEOUT);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Date date2 = new Date();
//            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating end：" + date2.toLocaleString());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if ((mLocAndDevRegAct != null) && (mSelectedGPSCity == null)
//                    && !mLocAndDevRegAct.isFinishing()) {
//
//                LocationManager.getInstance().unRegisterGPSLocationListener(
//                        mLocAndDevRegAct.mMessageHandler);
//
////                showToast("GPS locating timeout");
//                cityTextView.setVisibility(View.VISIBLE);
//                cityTextView.setText(getString(R.string.enroll_select));
//                gpsFinishView();
//
//                // Step 2 - Get location & Check device after GPS finish
//                checkMacPolling();
//                getLocationFromTCC();
//
//            }
//        }
//    }
//
//
//    private void getLocationFromTCC() {
//        // Umeng statistic
//        Map<String, String> map = new HashMap<>();
//        map.put("userId", mUserId + "_" + mMacId);
////        map.put("macId", mMacId);
//        MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "start_add_device", map);
//
//        isGetLocationFinished = false;
//        homeStartView();
//        // Use userId & sessionId received from Login to get TCC location data.
//        mUserId = AuthorizeApp.shareInstance().getUserID();
//        mSessionId = AuthorizeApp.shareInstance().getSessionId();
//        TccClient.sharedInstance().getLocation(mUserId, mSessionId, getTCCResponse);
//
//    }
//
//    private void homeStartView() {
//        homeLayout.setVisibility(View.VISIBLE);
//        homeNameLayout.setVisibility(View.VISIBLE);
//        homeSelectLayout.setVisibility(View.INVISIBLE);
//        homeLineImageView.setVisibility(View.INVISIBLE);
//        homePromptView.setVisibility(View.VISIBLE);
//        AnimationDrawable anim = (AnimationDrawable) homeLoadingImageView.getBackground();
//        anim.start();
//    }
//
//    private void homeFinishView() {
//        isGetLocationFinished = true;
//        deviceLayout.setVisibility(View.VISIBLE);
//        homeLayout.setVisibility(View.VISIBLE);
//        homeSelectLayout.setVisibility(View.VISIBLE);
//        homeLineImageView.setVisibility(View.VISIBLE);
//        homePromptView.setVisibility(View.INVISIBLE);
//        homeTextView.setText(getString(R.string.enroll_home));
//        doneButton.setVisibility(View.VISIBLE);
//        doneButton.setText(R.string.enroll_done);
//    }
//
//    private void gpsFinishView() {
//        locationTextView.setText(getString(R.string.enroll_gps));
//        gpsPromptView.setVisibility(View.INVISIBLE);
//        gpsLineImageView.setVisibility(View.VISIBLE);
//        gpsSelectImageView.setVisibility(View.VISIBLE);
//
//    }
//
//    /**
//     * 2 place need showHomeSpinner
//     * 1) GPS get city or select city again
//     * 2) finish loading home name
//     */
//    private void showHomeSpinner() {
//        String[] homeTypes = getHomeStringArray();
//        homeSpinnerTypeAdapter = new HomeSpinnerArrayAdapter<>(this, homeTypes);
//        homeSpinner.setAdapter(homeSpinnerTypeAdapter);
//    }
//
//    /**
//     * get home names which already registered on TCC
//     *
//     * @return string array of home name from GPS city
//     */
//    public String[] getHomeStringArray() {
//        homeStrings.clear();
//        if ((mUserLocations != null) && (mSelectedGPSCity != null)) {
//            for (int i = 0; i < mUserLocations.size(); i++) {
//                if (mUserLocations.get(i).getCity().equals(mSelectedGPSCity.getCode()))
//                    homeStrings.add(mUserLocations.get(i).getName());
//            }
//        }
//
//        /*
//         * If home name is available, spinner will not show automatically
//         * the editText hint need to be cleaned
//         */
//        if (homeStrings.isEmpty()) {
//            userLocationEditText.setEditorHint(getString(R.string.my_home));
//        } else {
//            userLocationEditText.setEditorHint("");
//        }
//
//        String[] stringsArray = new String[homeStrings.size()];
//        return homeStrings.toArray(stringsArray);
//    }
//
//    /**
//     * SpinnerArrayAdapter for home selection
//     */
//    public class HomeSpinnerArrayAdapter<T> extends ArrayAdapter<T> {
//
//        public HomeSpinnerArrayAdapter(Context context, List<T> objects) {
//            super(context, 0, objects);
//        }
//
//        public HomeSpinnerArrayAdapter(Context context, T[] objects) {
//            super(context, 0, objects);
//        }
//
//        protected String getItemValue(T item, Context context) {
//            return item.toString();
//        }
//
//        @Override
//        public View getDropDownView(int position, View convertView, ViewGroup parent) {
//            View view = convertView;
//            if (convertView == null) {
//                view = LayoutInflater.from(getContext()).inflate(R.layout
//                        .list_item_home_spinner_drop_down, parent, false);
//            }
//
//            TextView tv = (TextView) view.findViewById(R.id.list_item_home_drop_text);
//            String city = getItemValue(getItem(position), getContext());
//            tv.setText(city);
//
//            return view;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = convertView;
//            if (convertView == null) {
//                view = LayoutInflater.from(getContext()).inflate(R.layout
//                        .list_item_home_spinner, parent, false);
//            }
//
//            TextView tv = (TextView) view.findViewById(R.id.list_item_home_title);
//            mSelectedHomeName = getItemValue(getItem(position), getContext());
//            tv.setText(mSelectedHomeName);
//
//            return view;
//        }
//
//    }
//
//
//    /**
//     * When User input Home Name, clean spinner text
//     */
//    private TextWatcher mEditTextWatch = new TextWatcher() {
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if (userLocationEditText.getEditorText().isEmpty()
//                    && (!isHomeEditTextFocused)) {
//                homeSpinner.setVisibility(View.VISIBLE);
//            } else {
//                homeSpinner.setVisibility(View.INVISIBLE);
//            }
//
//        }
//    };
//
//    View.OnFocusChangeListener locationOnFocus = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if (hasFocus) {
//                isHomeEditTextFocused = true;
//                homeSpinner.setVisibility(View.INVISIBLE);
//                userLocationEditText.setEditorHint("");
//            } else {
//                isHomeEditTextFocused = false;
//                if (userLocationEditText.getEditorText().isEmpty()) {
//                    homeSpinner.setVisibility(View.VISIBLE);
//                    if (homeStrings.isEmpty()) {
//                        userLocationEditText.setEditorHint(getString(R.string.my_home));
//                    } else {
//                        userLocationEditText.setEditorHint("");
//                    }
//                } else {
//                    homeSpinner.setVisibility(View.INVISIBLE);
//                    userLocationEditText.setEditorHint("");
//                }
//            }
//        }
//    };
//
//    View.OnFocusChangeListener deviceOnFocus = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if (hasFocus == true) {
//                userDevice.setEditorHint("");
//            } else {
//                userDevice.setEditorHint(getString(R.string.my_device));
//            }
//        }
//    };
//
//    // Step 1 - select custom city location (Click button)
//    public View.OnClickListener cityClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(LocationAndDeviceRegisterActivity.this,
//                    EditGPSActivity.class);
//            intent.putExtra("currentGPS", mSelectedGPSCity);
//            startActivityForResult(intent, SELECT_LOCATION_REQUEST);
//        }
//    };
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null || data.getSerializableExtra("city") == null)
//            return;
//        City selectedCity = (City) data.getSerializableExtra("city");
//        switch (requestCode) {
//            case SELECT_LOCATION_REQUEST:
//                mSelectedGPSCity = selectedCity;
//                cityTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ?
//                        selectedCity.getNameZh() : selectedCity.getNameEn());
//
//                showHomeSpinner();
//                break;
//            default:
//                break;
//        }
//    }
//
//    // Step 3 - Add device (Click button)
//    public View.OnClickListener doneOnClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (isNetworkRetry) {
//                isNetworkRetry = false;
//                homeTextView.setText(R.string.enroll_loading);
//                checkMacPolling();
//                getLocationFromTCC();
//                return;
//            }
//
//            if (mSelectedGPSCity == null) {
//                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                        getString(R.string.select_city), null, null);
//                return;
//            }
//
//            if (isMacAlreadyRegistered) {
//                isMacAlreadyRegistered = false;
//                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                        getString(R.string.device_registered), null, quitEnroll);
//                return;
//            }
//
//            /*
//             * When to add device to TCC depends on both MacPolling and getLocation thread.
//             * If MacPolling finishes before getLocation, addDeviceToTCC() after getLocation.(user click button)
//             * If getLocation finishes before MacPolling, addDeviceToTCC() after MacPolling.
//             */
//            if (isMacPollingFinished) {
//                if (isMacAlive) {
//                    addDeviceToTCC();
//                } else {
//                    /*
//                     * If device is offline checked before, check mac again here
//                     */
//                    isButtonClickedWaitPollingDone = true;
//                    mDialog = ProgressDialog.show(LocationAndDeviceRegisterActivity.this,
//                            null, getString(R.string.checking_device));
//                    checkMacPolling();
////                    MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                            getString(R.string.device_not_alive), null, null);
//                }
//            } else {
//                isButtonClickedWaitPollingDone = true;
//                mDialog = ProgressDialog.show(LocationAndDeviceRegisterActivity.this,
//                        null, getString(R.string.checking_device));
//            }
//        }
//    };
//
//    private void addDeviceToTCC() {
//        /*
//        * If user already registered home name,
//        * do not add location,just add device.
//        * Otherwise, add location then add device.
//        */
//        isDeviceNumberReachMax = false;
//        mLocationId = getLocationId();
//        if (mLocationId > 0) {
//            isAddHome = false;
//            addDevice(getTCCResponse);
//        } else {
//            if (isHomeNumberReachMax) {
//                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                        getString(R.string.max_home), null, null);
//                return;
//            }
//
//            addLocation(getTCCResponse);
//        }
//    }
//
//    private int getLocationId() {
//        if (mUserLocations.size() > 0) {
//            for (int i = 0; i < mUserLocations.size(); i++) {
//                if (mUserLocations.get(i).getName().equals(userLocationEditText.getEditorText())) {
//                    isDeviceNumberReachMax = mUserLocations.get(i).getDeviceInfo().size() >= 5;
//                    return mUserLocations.get(i).getLocationID();
//                }
//
//                if (userLocationEditText.getEditorText().equals("")) {
//                    if (mUserLocations.get(i).getName().equals(mSelectedHomeName)
//                            && !homeStrings.isEmpty()) {
//                        isDeviceNumberReachMax = mUserLocations.get(i).getDeviceInfo().size() >= 5;
//                        return mUserLocations.get(i).getLocationID();
//                    }
//                }
//            }
//        }
//
//        return 0;
//    }
//
//    private void addLocation(IReceiveResponse recordCreatedResponse) {
//        mUserHomeName = userLocationEditText.getEditorText();
//
//        if (mUserLocations.size() > 0) {
//            for (int i = 0; i < mUserLocations.size(); i++) {
//                if (mUserLocations.get(i).getName().equals(mUserHomeName)) {
//                    addDevice(getTCCResponse);
//                    return;
//                }
//            }
//        }
//
//        if (mUserHomeName.isEmpty()) {
//            mUserHomeName = getString(R.string.my_home);
//        }
//
//        if (mUserLocations.size() > 5) {
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.max_home), null, null);
//            return;
//        }
//
//        if (mSelectedGPSCity != null) {
//
//
//            addLocationRequest = new AddLocationRequest();
//            addLocationRequest.setCity(mSelectedGPSCity.getCode());
//            addLocationRequest.setName(mUserHomeName);
//
//            mDialog = ProgressDialog.show(LocationAndDeviceRegisterActivity.this, null, getString(R.string.adding_home));
//            TccClient.sharedInstance().addLocation(mUserId, mSessionId, addLocationRequest,
//                    recordCreatedResponse);
//        } else {
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.select_city), null, null);
//        }
//
//    }
//
//
//    private void addDevice(IReceiveResponse recordCreatedResponse) {
//        String device = userDevice.getEditorText();
//        if (isDeviceNumberReachMax) {
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.max_device), null, null);
//            return;
//        }
//
//        if (device.isEmpty()) {
//            device = getString(R.string.my_device);
//        }
//
//        mDialog = ProgressDialog.show(LocationAndDeviceRegisterActivity.this, null, getString(R.string.adding_device));
//        DeviceRegisterRequest deviceRegisterRequest = new DeviceRegisterRequest(mMacId, mCrcId, device);
//        TccClient.sharedInstance().addDevice(mLocationId, mSessionId, deviceRegisterRequest,
//                recordCreatedResponse);
//    }
//
//    private void checkMacPolling() {
//        isMacPollingFinished = false;
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                TccClient.sharedInstance().checkMac(mMacId, mSessionId, getTCCResponse);
//            }
//
//        }).start();
//    }
//
//
//    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
//        if (mDialog != null) {
//            mDialog.dismiss();
//        }
//
//        if (httpRequestResponse.getException() != null) {
//            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.no_network), null, null);
//
//            // Umeng statistic
//            Map<String, String> map = new HashMap<>();
//            map.put("userId", mUserId + "_" + mMacId + "_" + getString(R.string.no_network));
////            map.put("macId", mMacId);
////            map.put("errorType", getString(R.string.no_network));
//            MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_failed", map);
//            return;
//        }
//
//        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//            try {
//                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                JSONObject responseJSON = responseArray.getJSONObject(0);
//                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
//                        ErrorResponse.class);
////                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
////                        errorResponse.getMessage(), null, null);
//                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
//                MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                        getString(R.string.enroll_error), null, null);
//
//                // Umeng statistic
//                Map<String, String> map = new HashMap<>();
//                map.put("userId", mUserId + "_" + mMacId + "_" + getString(R.string.enroll_error));
////                map.put("macId", mMacId);
////                map.put("errorType", getString(R.string.enroll_error));
//                MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_failed", map);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            MessageBox.createSimpleDialog(LocationAndDeviceRegisterActivity.this, null,
//                    getString(R.string.enroll_error), null, null);
//
//            // Umeng statistic
//            Map<String, String> map = new HashMap<>();
//
//            map.put("userId", mUserId + "_" + mMacId + "_" + getString(R.string.enroll_error));
////            map.put("macId", mMacId);
////            map.put("errorType", getString(R.string.enroll_error));
//            MobclickAgent.onEvent(LocationAndDeviceRegisterActivity.this, "add_device_failed", map);
//        }
//    }
//
//    /**
//     * Wifi handling
//     */
//    private void initWifi() {
//        IntentFilter scanResultsFilter = new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        registerReceiver(mScanResultsReceiver, scanResultsFilter);
//
////        if (!WifiUtil.isWifiOpen(this)) {
//        WifiUtil.openWifi(this, this);
////        }
//    }
//
//    private BroadcastReceiver mScanResultsReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context c, Intent intent) {
//            if (isHasAutoConnectWifi == false) {
//                isHasAutoConnectWifi = true;
//                List<ScanResult> scanResults = mWifi.getScanResults();
//                for (ScanResult scanResult : scanResults) {
//                    if (DIYInstallationState.getmHomeConnectedSsid() != null) {
//                        if (DIYInstallationState.getmHomeConnectedSsid().contains(scanResult.SSID)) {
//                            connectAp(scanResult);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    };
//
//    private void connectAp(ScanResult mResult) {
//        List<WifiConfiguration> mList = WifiUtil.getConfigurations(LocationAndDeviceRegisterActivity.this);
//        if (mList == null || mList.isEmpty()) {
//            if (WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")) {
//                WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID, "",
//                        WifiUtil.getWifiCipher(mResult.capabilities)), LocationAndDeviceRegisterActivity.this);
//            }
//        } else {
//            boolean flag = false;
//            for (int i = 0; i < mList.size(); i++) {
//                if (mList.get(i).SSID.equals("\"" + mResult.SSID + "\"")) {
//                    WifiUtil.addNetWork(mList.get(i), LocationAndDeviceRegisterActivity.this);
//                    flag = true;
//                    break;
//                }
//            }
//            if (!flag) {
//                if (WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")) {
//                    WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID, "",
//                            WifiUtil.getWifiCipher(mResult.capabilities)), LocationAndDeviceRegisterActivity.this);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onWifiOpen(int state) {
//
//    }
//}