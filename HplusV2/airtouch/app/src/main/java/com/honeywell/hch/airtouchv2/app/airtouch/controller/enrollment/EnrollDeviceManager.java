package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.AddLocationRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.DeviceRegisterRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.enrollment.controls.EnrollmentClient;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIDeviceResponse;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIErrorCodeResponse;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIKeyResponse;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.task.AddDeviceTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.AddLocationTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.CheckMacTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.CommTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.lib.util.WifiUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Qian Jin on 8/26/15.
 * This class is to handle 3 things:
 * 1) Phone's wifi handling
 * 2) Phone connect to Device
 * 3) Phone connect to TCC
 */
public class EnrollDeviceManager implements WifiUtil.IWifiOpen {
    // Data for home and device
    private int mTaskId;
    private String mSessionId;
    private String mMacId;
    private String mCrcId;
    private String mDeviceName;
    private String mHomeName;
    private String mCityCode;
    private int mLocationId;
    private ArrayList<UserLocation> mUserLocations;
    private UserLocation mSelectedHomeWaitingForAdd;
    private boolean isDeviceNumberReachMax;
    // Data for device connect
    private WAPIDeviceResponse mWAPIDeviceResponse;
    private WAPIKeyResponse mWAPIKeyResponse;
    private WAPIErrorCodeResponse mWAPIErrorCodeResponse;
    // Data for Wifi
    private WifiManager mWifi;
    private boolean mConnectionAttempted;
    private boolean isHasAutoConnectWifi = false;
    private boolean isRegistered = false;

    private FinishCallback mFinishCallback;
    private ErrorCallback mErrorCallback;
    private LoadingCallback mLoadingCallback;
    private Context mContext;
    private Activity mActivity;

    private static final String TAG = "AirTouchEnrollConnectDevice";


    public EnrollDeviceManager(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
            mMacId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
            mCrcId = DIYInstallationState.getWAPIDeviceResponse().getCrcID();
            mDeviceName = DIYInstallationState.getDeviceName();
            mHomeName = DIYInstallationState.getHomeName();
            mCityCode = DIYInstallationState.getCityCode();
        }
    }

    public interface FinishCallback {
        void onFinish();
    }

    public interface ErrorCallback {
        void onError(ResponseResult responseResult, String errorMsg);
    }

    public interface LoadingCallback {
        void onLoad(String msg);
    }

    public void setFinishCallback(FinishCallback finishCallback) {
        mFinishCallback = finishCallback;
    }

    public void setErrorCallback(ErrorCallback errorCallback) {
        mErrorCallback = errorCallback;
    }

    public void setLoadingCallback(LoadingCallback loadingCallback) {
        mLoadingCallback = loadingCallback;
    }


    /******************
     * Wifi handling
     ******************/
    public void reconnectHomeWifi() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return;
        }

        mWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifi.disconnect();
        mWifi.startScan();

        isRegistered = true;
        IntentFilter scanResultsFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mScanResultsReceiver, scanResultsFilter);

        WifiUtil.openWifi(mContext, this);
    }

    private BroadcastReceiver mScanResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                return;
            }

            if (!isHasAutoConnectWifi) {
                isHasAutoConnectWifi = true;
                List<ScanResult> scanResults = mWifi.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (DIYInstallationState.getmHomeConnectedSsid() != null) {
                        if (DIYInstallationState.getmHomeConnectedSsid().contains(scanResult.SSID)) {
                            connectAp(scanResult);
                            return;
                        }
                    }
                }
            }
        }
    };

    private void connectAp(ScanResult mResult) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return;
        }

        List<WifiConfiguration> mList = WifiUtil.getConfigurations(mContext);
        if (mList == null || mList.isEmpty()) {
            if (WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")) {
                WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID, "",
                        WifiUtil.getWifiCipher(mResult.capabilities)), mContext);
            }
        } else {
            boolean flag = false;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).SSID.equals("\"" + mResult.SSID + "\"")) {
                    WifiUtil.addNetWork(mList.get(i), mContext);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if (WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")) {
                    WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID, "",
                            WifiUtil.getWifiCipher(mResult.capabilities)), mContext);
                }
            }
        }
    }

    @Override
    public void onWifiOpen(int state) {

    }


    /*************************************
     * Methods of Phone connect to Device
     *************************************/

    public boolean isConnectionAttempted() {
        return mConnectionAttempted;
    }

    public void setConnectionAttempted(boolean connectionAttempted) {
        mConnectionAttempted = connectionAttempted;
    }

    public BroadcastReceiver getScanResultsReceiver() {
        return mScanResultsReceiver;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    final IReceiveResponse getWAPIResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                switch (httpRequestResponse.getRequestID()) {
                    case GET_MAC_CRC:
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            mWAPIDeviceResponse = new Gson().fromJson(httpRequestResponse
                                            .getData(),
                                    WAPIDeviceResponse.class);
                        }
                        mConnectionAttempted = true;
                        DIYInstallationState.setWAPIDeviceResponse(mWAPIDeviceResponse);
                        mFinishCallback.onFinish();

                        break;

                    case GET_KEY:
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            mWAPIKeyResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    WAPIKeyResponse.class);
                        }
                        DIYInstallationState.setWAPIKeyResponse(mWAPIKeyResponse);
                        EnrollmentClient.sharedInstance()
                                .getDeviceInfo(RequestID.GET_MAC_CRC, getWAPIResponse);
                        break;

                    case SEND_PHONE_NAME:
                        EnrollmentClient.sharedInstance()
                                .getWAPIKey(RequestID.GET_KEY, getWAPIResponse);
                        break;

                    default:
                        break;
                } // end of switch
            } else {
                mErrorCallback.onError(null, null);
            }
        }
    };

    public void connectDevice() {
        EnrollmentClient.sharedInstance().sendPhoneName(android.os.Build.MODEL,
                RequestID.SEND_PHONE_NAME, getWAPIResponse);
    }

    public void processErrorCode() {
        final IReceiveResponse getErrorCodeResponse = new IReceiveResponse() {
            @Override
            public void onReceive(HTTPRequestResponse httpRequestResponse) {
                if (httpRequestResponse.getException() != null) {
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "exception："
                            + httpRequestResponse.getException());
                    return;
                }

                if (httpRequestResponse.getStatusCode() != StatusCode.OK) {
                    return;
                }

                switch (httpRequestResponse.getRequestID()) {
                    case GET_ERROR:
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "response code："
                                + httpRequestResponse.getStatusCode());

                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            mWAPIErrorCodeResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    WAPIErrorCodeResponse.class);
                            DIYInstallationState.setErrorCode(mWAPIErrorCodeResponse.getError());
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        EnrollmentClient.sharedInstance()
                .getErrorCode(RequestID.GET_ERROR, getErrorCodeResponse);
    }


    /*************************************
     * Methods of Phone connect to TCC
     * Step 1 - Check mac
     * Step 2 - Add home and device
     *************************************/

    public void startConnectServer() {
//        mLoadingCallback.onLoad(mContext.getString(R.string.checking_device));
        checkMacPolling();
    }

    public void checkMacPolling() {
        final IActivityReceive getMacResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                switch (responseResult.getRequestId()) {
                    case CHECK_MAC:
                        if (responseResult.getFlag() == AirTouchConstants.CHECK_MAC_ALIVE) {
                            addHomeAndDevice();
                        } else if (responseResult.getFlag() == AirTouchConstants.CHECK_MAC_AGAIN) {
                            checkMacPolling();
                        } else if (responseResult.getFlag() == AirTouchConstants.CHECK_MAC_OFFLINE) {
                            mErrorCallback.onError(responseResult, mContext.getString(R.string.device_not_alive));
                        }
                        return;

                    default:
                        break;
                }

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                    CheckMacTask checkMacTask = new CheckMacTask(mMacId, null, getMacResponse);
                    checkMacTask.execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getUserHomeAndDeviceDebug() {
        mMacId = "144146000031";
        mCrcId = "5AB8";
        mDeviceName = "testDeviceName";
        mHomeName = "testHomeName";
        mCityCode = "CHSH000000";
        DIYInstallationState.setIsDeviceAlreadyEnrolled(false);
    }

    /*
    * If user already registered home name,
    * do not add home, just add device.
    * Otherwise, add home then add device.
    * addLocation => addDevice
    *
    * If user already registered device, just quit
    */
    private void addHomeAndDevice() {
        if (DIYInstallationState.getIsDeviceAlreadyEnrolled()) {
            mFinishCallback.onFinish();
            return;
        }

        mLocationId = getLocationIdFromHomeName();
        if (mLocationId > 0) {
            addDevice();
        } else {
            addHome();
        }
    }

    private void addHome() {
        IActivityReceive addLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case ADD_LOCATION:
                            if (responseResult.getResponseCode() == StatusCode.OK) {
                                mLocationId = responseResult.getResponseData()
                                        .getInt(AirTouchConstants.LOCATION_ID_BUNDLE_KEY);
                                addDevice();
                                sendBroadcastToMainPage();
                            } else {
                                mErrorCallback.onError(responseResult, mContext.getString(R.string.enroll_error));
                            }
                            return;
                        default:
                            break;
                    }
                } else {
                    mErrorCallback.onError(responseResult, mContext.getString(R.string.enroll_error));
                }
            }
        };

//        mLoadingCallback.onLoad(mContext.getString(R.string.adding_home));
        AddLocationRequest addLocationRequest = new AddLocationRequest();
        addLocationRequest.setCity(mCityCode);
        addLocationRequest.setName(mHomeName);
        AddLocationTask requestTask
                = new AddLocationTask(addLocationRequest, addLocationResponse);
        requestTask.execute();
    }

    /*
     * addDevice => runCommTask => finish
     */
    private void addDevice() {
        if (isDeviceNumberReachMax) {
            MessageBox.createSimpleDialog(mActivity, null,
                    mContext.getString(R.string.max_device), null, null);
            return;
        }

        IActivityReceive addDeviceResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case ADD_DEVICE:
                            if (responseResult.getResponseCode() == StatusCode.OK) {
                                mTaskId = responseResult.getResponseData()
                                        .getInt(AirTouchConstants.COMM_TASK_BUNDLE_KEY);
                                runCommTask();
                            }
                            return;
                        default:
                            break;
                    }
                } else if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST) {
                    mErrorCallback.onError(responseResult, mContext.getString(R.string.device_already_registered_by_another));
                } else {
                    mErrorCallback.onError(responseResult, mContext.getString(R.string.enroll_error));
                }
            }
        };

//        mLoadingCallback.onLoad(mContext.getString(R.string.adding_device));
        DeviceRegisterRequest deviceRegisterRequest
                = new DeviceRegisterRequest(mMacId, mCrcId, mDeviceName);
        AddDeviceTask requestTask
                = new AddDeviceTask(mLocationId, deviceRegisterRequest, addDeviceResponse);
        requestTask.execute();
    }

    private void runCommTask() {
        final IActivityReceive runCommTaskResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case COMM_TASK:
                            switch (responseResult.getFlag()) {
                                case AirTouchConstants.COMM_TASK_RUNNING:
                                    runCommTask();
                                    break;

                                case AirTouchConstants.COMM_TASK_SUCCEED:
                                    if (mFinishCallback != null)
                                        mFinishCallback.onFinish();
                                    break;

                                case AirTouchConstants.COMM_TASK_FAILED:
                                    mErrorCallback.onError(responseResult, mContext.getString(R.string.add_device_fail));
                                    break;

                                case AirTouchConstants.COMM_TASK_TIMEOUT:
                                    mErrorCallback.onError(responseResult, mContext.getString(R.string.control_timeout));
                                    break;

                                default:
                                    mErrorCallback.onError(responseResult, mContext.getString(R.string.enroll_error));
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    mErrorCallback.onError(responseResult, mContext.getString(R.string.enroll_error));
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    CommTask commTask = new CommTask(mTaskId, null, runCommTaskResponse);
                    commTask.execute();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /*
     * get mSelectedHomeWaitingForAdd by comparing mHomeName
     * return 0 - user home name is not found in the list mUserLocations
     * return locationId - user home name is found in the list, which means home is already registered.
     */
    private int getLocationIdFromHomeName() {
        mUserLocations = AuthorizeApp.shareInstance().getUserLocations();
        if (mUserLocations.size() > 0) {
            for (int i = 0; i < mUserLocations.size(); i++) {
                if (mUserLocations.get(i).getName().equals(mHomeName)) {
                    isDeviceNumberReachMax = mUserLocations.get(i).getDeviceInfo().size()
                            >= AirTouchConstants.MAX_DEVICE_NUMBER;
                    mSelectedHomeWaitingForAdd = mUserLocations.get(i);
                    return mUserLocations.get(i).getLocationID();
                }
            }
        }
        return 0;
    }

    private void sendBroadcastToMainPage() {
        Intent intent = new Intent(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
        intent.putExtra(AirTouchConstants.IS_ADD_HOME, true);
        intent.putExtra(AirTouchConstants.LOCAL_LOCATION_ID, mLocationId);
        mContext.sendBroadcast(intent);
    }

}