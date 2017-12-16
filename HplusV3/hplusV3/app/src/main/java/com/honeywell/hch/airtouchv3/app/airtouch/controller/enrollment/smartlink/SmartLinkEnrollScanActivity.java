package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollWelcomeActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.framework.webservice.task.SmartLinkTask;
import com.honeywell.hch.airtouchv3.lib.Zxing.camera.CameraManager;
import com.honeywell.hch.airtouchv3.lib.Zxing.decoding.CaptureActivityHandler;
import com.honeywell.hch.airtouchv3.lib.Zxing.decoding.InactivityTimer;
import com.honeywell.hch.airtouchv3.lib.Zxing.view.ViewfinderView;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;

import java.io.IOException;
import java.util.Vector;

/**
 * 拍照的Activity
 *
 * @author Vincent
 */
public class SmartLinkEnrollScanActivity extends EnrollBaseActivity implements Callback {
    private static final String TAG = "SmartLinkEnroll";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private SmartEnrollScanEntity mSmartEnrollScanEntity = null;
    private LoadingProgressDialog mLoadingProgress;
    private TextView mTitleView;
    private TextView mContentView;
    private Activity mActivity;
    private static final int WIFI_CONNECTED_CHECK_END = 2002;

    private static final String CLASS_PARAMS = "classpositon";

    private Thread mCheckWifiConnectThread = null;
    public static final String IS_CONNECT = "isconnecting";
    private CameraManager cameraManager;
    SurfaceView surfaceView;
    private final int MY_PERMISSIONS_REQUEST_GET_CAMERA = 1000;
    private RelativeLayout mNoramLayout;
    private RelativeLayout mNoDenyLayout;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enrollsmart_scan);
        initView();
        checkPermission();
    }

    private void initView() {
        mActivity = this;
        cameraManager = CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.mo_scanner_viewfinder_view);
        mNoramLayout = (RelativeLayout) findViewById(R.id.mo_scanner_deny_view);
        mNoDenyLayout = (RelativeLayout) findViewById(R.id.mo_scanner_nodeny_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        mTitleView = (TextView) findViewById(R.id.input_tip_id);
        mContentView = (TextView) findViewById(R.id.input_tip_hint_id);
        initSmartEnrollScanEntity();
    }

    private void initSmartEnrollScanEntity() {
        mSmartEnrollScanEntity = SmartEnrollScanEntity.getEntityInstance();
        String enrollEntranch = getIntent().getStringExtra(AirTouchConstants.SMART_ENROLL_ENRTRANCE);
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "enrollEntranch: " + enrollEntranch);
        if (enrollEntranch != null) {
            mSmartEnrollScanEntity.setSmartEntranch(enrollEntranch);

        }
        mSmartEnrollScanEntity.clearData();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "else  shouldShowRequestPermissionRationale");
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_GET_CAMERA);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onRequestPermissionsResult  shouldShowRequestPermissionRationale");
                    initNoPermission(true);

                } else {
                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, "else onRequestPermissionsResult  shouldShowRequestPermissionRationale");
                    initNoPermission(false);
                }
                break;
        }
    }


    private void initNoPermission(boolean isPermission) {
        if (isPermission) {
            mContentView.setText(getString(R.string.smart_scan_hint));
            mTitleView.setText(getString(R.string.scan_title));
        } else {
            mContentView.setText(getString(R.string.smart_permission_deny_messege));
            mTitleView.setText(getString(R.string.smart_permission_deny_title));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView = (SurfaceView) findViewById(R.id.mo_scanner_preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
        if (mLoadingProgress != null && mLoadingProgress.isShowing())
            mLoadingProgress.dismiss();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            initNoPermission(false);
            ioe.printStackTrace();
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            initNoPermission(false);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            initNoPermission(false);
            return;
        }
        if (handler == null) {
           try{
               handler = new CaptureActivityHandler(this, decodeFormats,
                       characterSet);
           } catch (Exception ex) {
               ex.printStackTrace();
           }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    MessageBox.MyOnClick myOnClick = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            disMissDialog();
        }
    };

    private void disMissDialog() {
        if (handler != null)
            handler.restartPreviewAndDecode();
        if (mLoadingProgress != null && mLoadingProgress.isShowing())
            mLoadingProgress.dismiss();
    }

    public void handleDecode(final Result result, Bitmap barcode) {
        if (!NetWorkUtil.isNetworkAvailable(mActivity)) {
            MessageBox.createSimpleDialog(SmartLinkEnrollScanActivity.this, "", getString(R.string.no_network), getString(R.string.ok), myOnClick);
            return;
        }

        mLoadingProgress = LoadingProgressDialog.show(mContext, getString(R.string.enroll_scanning));
        inactivityTimer.onActivity();
        String resultURL = result.getText();
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "resultURL: " + resultURL);
        paresURL(resultURL);
    }

    private void paresURL(String recode) {
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "mSmartEnrollScanEntity: " + mSmartEnrollScanEntity);
        boolean isHoneyQRCode = SmartLinkTask.paseURL(recode, mSmartEnrollScanEntity);
        if (isHoneyQRCode) {
            executeGetDeviceType(mSmartEnrollScanEntity.getmModel());
        } else {
            mSmartEnrollScanEntity = SmartEnrollScanEntity.getEntityInstance();
            MessageBox.createSimpleDialog(mActivity, "", getString(R.string.no_data_device), getString(R.string.ok), myOnClick);

        }
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_back_layout:
                backIntent();
                break;
        }
    }

    private void backIntent() {
        String smartEntrance = mSmartEnrollScanEntity.getSmartEntrance();
        Class intentClass = MainActivity.class;
        if (smartEntrance != null && !"".equals(smartEntrance)) {
            Intent intent = new Intent();
            try {
                intentClass = Class.forName(smartEntrance);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                intent.setClass(mContext, intentClass);
                startActivity(intent);
            }
            intent.setClass(mContext, intentClass);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // when the progress is finding the device , can not be back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void executeGetDeviceType(String deviceType) {
        SmartLinkTask requestTask
                = new SmartLinkTask(deviceType, null, checkTypeResponse);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
    }


    IActivityReceive checkTypeResponse = new IActivityReceive() {
        @Override
        public void onReceive(ResponseResult responseResult) {
            if (responseResult.isResult()) {
                switch (responseResult.getRequestId()) {
                    case GET_ENROLL_TYPE:
                        if (responseResult.getResponseCode() == StatusCode.OK) {
                            String result = responseResult.getExeptionMsg();
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "data: " + result);
                            if (!"".equals(result)) {
                                SmartLinkTask.parseCheckTypeResponse(result, mSmartEnrollScanEntity);
                                if (mSmartEnrollScanEntity != null) {
                                    isSupportSmartLink();
                                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, "mSmartEnrollScanEntity: " + mSmartEnrollScanEntity.toString());
                                } else {
                                    MessageBox.createSimpleDialog(SmartLinkEnrollScanActivity.this, "", getString(R.string.no_data_device), getString(R.string.ok), myOnClick);
                                }
                            }
                        }
                        break;
                }
            } else {
                if (responseResult.getResponseCode() == StatusCode.NETWORK_ERROR) {
                    MessageBox.createSimpleDialog(mActivity, "", getString(R.string.no_network), getString(R.string.ok), myOnClick);
                } else {
                    MessageBox.createSimpleDialog(mActivity, "", getString(R.string.enroll_error), getString(R.string.ok), myOnClick);
                }
            }
        }
    };

    private void isSupportSmartLink() {
        String[] deviceType = mSmartEnrollScanEntity.getmEnrollType();
        if (deviceType != null) {
            for (int i = 0; i < deviceType.length; i++) {
                if ("-1".equals(deviceType[i])) {
                    MessageBox.createSimpleDialog(mActivity, "", getString(R.string.no_support_smart_link), getString(R.string.ok), myOnClick);
                    return;
                } else if ("1".equals(deviceType[i])) {
                    startCheckNetworkConnectingThread(SmartLinkChooseActivity.class); //very network
                    return;
                } else if ("0".equals(deviceType[i])) {
                    startIntent(EnrollWelcomeActivity.class); //ap don't need veryfiy network
                    return;
                }
            }
        } else {
            MessageBox.createSimpleDialog(mActivity, "", getString(R.string.no_data_device), getString(R.string.ok), myOnClick);
        }
    }

    private void startIntent(Class toWhichClass) {
        Intent intent = new Intent(mContext, toWhichClass);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_CONNECTED_CHECK_END:
                    Bundle bundle = msg.getData();
                    boolean isCanAccessNetwork = bundle.getBoolean(IS_CONNECT);
                    Class mClass = (Class) bundle.getSerializable(CLASS_PARAMS);
                    boolean isHasWifi = NetWorkUtil.isWifiAvailable(mActivity);
                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, "isHasWifi: " + isHasWifi + " isCanAccessNetwork: " + isCanAccessNetwork);
                    if (isHasWifi && isCanAccessNetwork) {
                        startIntent(mClass);
                    } else {
                        int messgeId = isHasWifi ? R.string.smartlink_access_network : R.string.smartlink_no_wifi;
                        MessageBox.createSimpleDialog(mActivity, "", getString(messgeId), getString(R.string.ok), myOnClick);
                    }
                    mCheckWifiConnectThread = null;
                    break;
            }
        }
    };

    public void startCheckNetworkConnectingThread(final Class mClass) {
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "startCheckNetworkConnectingThread");
        if (mCheckWifiConnectThread == null) {
            mCheckWifiConnectThread = new Thread() {
                public void run() {
                    boolean isCanAccessNetwork = NetWorkUtil.isNetworkCanAccessInternet();
                    Message message = Message.obtain();
                    message.what = WIFI_CONNECTED_CHECK_END;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(IS_CONNECT, isCanAccessNetwork);
                    bundle.putSerializable(CLASS_PARAMS, mClass);
                    message.setData(bundle);
                    mHandler.sendMessage(message);

                }
            };
        }
        mCheckWifiConnectThread.start();
    }

}