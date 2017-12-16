package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.SmartEnrollScanEntity;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.WAPIRouter;
import com.honeywell.hch.airtouchv3.framework.permission.Permission;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;
import com.honeywell.hch.airtouchv3.lib.util.WifiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enrollment Step 1 - SmartPhone find Air Touch's SSID.
 * Scanning surrounding every 1s.
 */
public class EnrollWelcomeActivity extends EnrollBaseActivity {

    private static final String TAG = "AirTouchEnrollWelcome";
    public static final String AIR_TOUCH_S_SSID = "AirTouch S";
    public static final String AIR_TOUCH_X_SSID = "AirTouch X";
    public static final String AIR_TOUCH_P_SSID = "AirTouch P";

    private final static int AIR_TOUCH_X_TYPE = 2;

    private RelativeLayout mAirTouchTitleLayout;
    private RelativeLayout mAirPremiumTitleLayoutCn;
    private RelativeLayout mAirPremiumTitleLayoutEn;
    private ImageView loadingImageView;
    private ImageView mDeviceImageView;
//    private ImageView handImageView;

    private static Dialog mDialog;
    private ArrayList<WAPIRouter> mWAPIRouters;
    private ListView mNetworkList;
    private NetworkListAdapter networkListAdapter;
    private WifiManager mWifi;
    private ArrayList<ScanResult> mScanResults = new ArrayList<>();
    private ScanResult mSelectedScanResult;
    private ConfigureState mConfigureState = ConfigureState.WIFI_OFF;
    private boolean isScanning = false;
    private int noticeCount = 0;

    private TextView mFromApTextView;

    private TextView mNoPermissionTextViewOne;

    private RelativeLayout mHasPermissionLayout;
    private RelativeLayout mNoPermissionLayout;

    private TextView mGoToSettingsTextView;

    private enum ConfigureState {
        WIFI_OFF, WIFI_ON, SCANNING, RESCAN, NO_STAT_NETWORKS,
        MULTIPLE_STAT_NETWORKS, STAT_NETWORK_SELECTED, CONNECTING, CONNECTED, CONFIGURED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_enrollwelcome);
        mAirTouchTitleLayout = (RelativeLayout) findViewById(R.id.enroll_page1_title_layout);
        mAirPremiumTitleLayoutCn = (RelativeLayout) findViewById(R.id.air_premium_welcome_layout_cn);
        mAirPremiumTitleLayoutEn = (RelativeLayout) findViewById(R.id.air_premium_welcome_layout_en);
        mDeviceImageView = (ImageView) findViewById(R.id.machine_image);
        loadingImageView = (ImageView) findViewById(R.id.loading_image);

        mNetworkList = (ListView) findViewById(R.id.wifi_list);
        mNetworkList.setOnItemClickListener(mScanResultClickListener);
        networkListAdapter = new NetworkListAdapter(EnrollWelcomeActivity.this);

        mNoPermissionTextViewOne = (TextView)findViewById(R.id.no_permission_textview_txt);

        mHasPermissionLayout = (RelativeLayout)findViewById(R.id.has_permission_id);
        mNoPermissionLayout = (RelativeLayout)findViewById(R.id.no_permission_layout);
        mGoToSettingsTextView = (TextView)findViewById(R.id.go_to_settings);
        mGoToSettingsTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHPlusPermission != null){
                    mHPlusPermission.fourceOpenGPS(EnrollWelcomeActivity.this);
                }
            }
        });
        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (AppConfig.isDebugMode) {
            loadingImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(EnrollWelcomeActivity.this, EnrollConnectDeviceActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }

        mFromApTextView = (TextView) findViewById(R.id.from_conntect_timeout_id);

        if (!SmartEnrollScanEntity.getEntityInstance().isFromTimeout()) {

            SpannableString ssTitle = new SpannableString(getString(R.string.smart_choose_title));
            if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 3, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 12, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 42, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 42, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 14, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 51, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mFromApTextView.setText(ssTitle);
        } else {
            //when smartlink is connected timeout,than show this
            SpannableString ssTitle = new SpannableString(getResources().getString(R.string.connect_timeout_to_ap));
            if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 3, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 12, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new RelativeSizeSpan(1.3f), 42, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 42, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 14, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 51, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mFromApTextView.setText(ssTitle);
        }

        mDeviceImageView.setImageResource(SmartEnrollScanEntity.getEntityInstance().getDeviceImage());



        //do something, permission was previously granted; or legacy device
        IntentFilter scanResultsFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mScanResultsReceiver, scanResultsFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        mHPlusPermission.checkAndRequestPermission(Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

//        mHPlusPermission.checkAndRequestPermission(Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE, true, this);
        if (mHPlusPermission.isLocationPermissionGranted(this) && mHPlusPermission.isGPSOPen(this)){
            showHasPermissionAndGpsOpen();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        isScanning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mScanResultsReceiver != null){
            unregisterReceiver(mScanResultsReceiver);
            mScanResultsReceiver = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            MessageBox.createTwoButtonDialog(this, null,
                    getString(R.string.enroll_quit), getString(R.string.no), null,
                    getString(R.string.yes), quitEnroll);
        }

        return false;
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            isScanning = false;
            UmengUtil.onEvent(EnrollWelcomeActivity.this,
                    UmengUtil.EventType.ENROLL_CANCEL.toString(), "page1");

            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        showHasPermissionAndGpsOpen();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    setNoPermissionLayoutShow();
                    setNoPermissionText();
                }
                break;
        }
    }


    private BroadcastReceiver mScanResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (!WifiUtil.isWifiOpen(EnrollWelcomeActivity.this)
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showToast(getString(R.string.enroll_open_wifi));
                return;
            }

            if (mConfigureState != ConfigureState.SCANNING) {
                return;
            }

            try {
                addScanResult();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            checkGpsForAndroidM();
        }
    };

    private void addScanResult() {
        List<ScanResult> scanResults = mWifi.getScanResults();
        mScanResults.clear();

            /*
             *   Smart phone is scanning Air Touch's SSID,
             *   if found one, connect to it and go to the next step.
             *   Ignore others similar SSID.
             */
        for (ScanResult scanResult : scanResults) {
            if (SmartEnrollScanEntity.getEntityInstance().getDeviceName() == R.string.airtouch_x_str) {
                if (scanResult.SSID.contains(AIR_TOUCH_X_SSID)) {
                    mScanResults.add(scanResult);
                }
            } else if (SmartEnrollScanEntity.getEntityInstance().getDeviceName() == R.string.airtouch_s_str) {
                if (scanResult.SSID.contains(AIR_TOUCH_S_SSID)) {
                    mScanResults.add(scanResult);
                }
            } else {
                if (scanResult.SSID.contains(AIR_TOUCH_P_SSID)) {
                    mScanResults.add(scanResult);
                }
            }
        }

        // If there is only one automatically connect to that scan result.
        if (mScanResults.size() == 1) {
            mSelectedScanResult = mScanResults.get(0);
            mConfigureState = ConfigureState.STAT_NETWORK_SELECTED;
        } else if (mScanResults.size() == 0) {
            noticeCount++;
            if (noticeCount == 10) {
                noticeCount = 0;
                showToast(getString(R.string.enroll_not_found));
            }
            mConfigureState = ConfigureState.RESCAN;
        } else if (mScanResults.size() >= 2) {
            mNetworkList.setVisibility(View.VISIBLE);
            mWAPIRouters = new ArrayList<>();
            for (ScanResult scanResult : mScanResults) {
                WAPIRouter router = new WAPIRouter();
                router.setSSID(scanResult.SSID);
                mWAPIRouters.add(router);
            }
            mNetworkList.setAdapter(networkListAdapter);
            mConfigureState = ConfigureState.CONFIGURED;
        }

        saveSsid();

        loadData();
    }

    private void loadData() {
        if (mConfigureState == ConfigureState.WIFI_OFF) {
            determineWifiState();
        }

        switch (mConfigureState) {
            case WIFI_OFF:
                showToast("WIFI off");
                break;
            case WIFI_ON:
            case SCANNING:
                startScan();
                break;
            case RESCAN:
                rescan();
                break;
            case STAT_NETWORK_SELECTED:
            case CONNECTING:
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "CONNECTING...");
                connectToScanResult(mSelectedScanResult);
                break;
            case CONNECTED:
                mDialog = LoadingProgressDialog.show(EnrollWelcomeActivity.this, getString(R.string.enroll_loading));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                            Intent i = new Intent();
                            i.setClass(EnrollWelcomeActivity.this, EnrollConnectDeviceActivity.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case NO_STAT_NETWORKS:
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Connecting...");
                break;
            case CONFIGURED:
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "More than 1 device found...");
                break;
            default:
                break;
        }
    }

    private void startScan() {
        mConfigureState = ConfigureState.SCANNING;
        mWifi.startScan();
    }

    private void rescan() {
        if (!isScanning)
            return;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mConfigureState = ConfigureState.SCANNING;
                mWifi.startScan();
            }

        }).start();

    }

    private void determineWifiState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mConfigureState = ConfigureState.WIFI_ON;
            return;
        }
        if (mWifi.isWifiEnabled()) {
            mConfigureState = ConfigureState.WIFI_ON;
        } else {
            if (mWifi.setWifiEnabled(true)) {
                mConfigureState = ConfigureState.WIFI_ON;
            } else {
                mConfigureState = ConfigureState.WIFI_OFF;
            }
        }
    }

    private void saveSsid() {
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            DIYInstallationState.setmHomeConnectedSsid(ssid);
        }
    }

    private void connectToScanResult(ScanResult scanResult) {

        mConfigureState = ConfigureState.CONNECTING;

        if (WifiUtil.reConnectWifiInAndroidM(scanResult,mWifi)){
            connectionCompleted();
        }
    }

    private void connectionCompleted() {
        mConfigureState = ConfigureState.CONNECTED;
        loadData();
    }

    private WifiConfiguration getWifiConfiguration(ScanResult scanResult) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = '\"' + scanResult.SSID + '\"';
        wifiConfiguration.hiddenSSID = false;
        wifiConfiguration.allowedKeyManagement
                .set(WifiConfiguration.KeyMgmt.NONE);
        return wifiConfiguration;
    }

    private class NetworkListAdapter extends ArrayAdapter<WAPIRouter> {

        public NetworkListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public WAPIRouter getItem(int position) {
            return mWAPIRouters.get(position);
        }

        @Override
        public int getCount() {
            if (mWAPIRouters == null) {
                return 0;
            }
            return mWAPIRouters.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_network,
                        parent, false);
            }

            WAPIRouter wapiRouter = getItem(position);

            if (wapiRouter.getSSID() != null) {
                TextView ssidTextView = (TextView) convertView
                        .findViewById(R.id.list_item_network_text);
                ssidTextView.setText(wapiRouter.getSSID());
            }

            Drawable drawable = null;
            if (wapiRouter.isLocked()) {
                drawable = getContext().getResources().getDrawable(R.drawable.wifi_lock);
            }

            ImageView image = (ImageView) convertView
                    .findViewById(R.id.list_item_network_lock_image);
            image.setImageDrawable(drawable);
            return convertView;
        }

    }

    private AdapterView.OnItemClickListener mScanResultClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WAPIRouter wapiRouter = (WAPIRouter) parent.getItemAtPosition(position);

            for (int i = 0; i < mScanResults.size(); i++) {
                if (mScanResults.get(i).SSID.equals(wapiRouter.getSSID())) {
                    mSelectedScanResult = mScanResults.get(i);
                    mConfigureState = ConfigureState.STAT_NETWORK_SELECTED;
                }
            }
            loadData();
        }
    };

    private void checkGpsForAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MessageBox.createTwoButtonDialog(EnrollWelcomeActivity.this, getString(R.string.no_need_to_buy),
                        getString(R.string.pre_filter_suggestion), getString(R.string.buy_it),
                        goToGPSSetting, getString(R.string.cancel), quit);
            }
        }
    }

    private MessageBox.MyOnClick goToGPSSetting = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // The Android SDK doc says that the location settings activity
                // may not be found. In that case show the general settings.
                // General settings activity
                intent.setAction(Settings.ACTION_SETTINGS);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
    };

    private MessageBox.MyOnClick quit = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };

    private void showHasPermissionAndGpsOpen(){
        setHasPermissionLayoutShow();

        isScanning = true;
        AnimationDrawable anim1 = (AnimationDrawable) loadingImageView.getBackground();
        anim1.start();


        loadData();

    }


    private void setNoPermissionText(){
        SpannableString ssTitle = new SpannableString(getString(R.string.no_located_permission_scantxt));
        if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 79, 95, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 79, 95, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mNoPermissionTextViewOne.setText(ssTitle);
        mGoToSettingsTextView.setVisibility(View.GONE);
    }


    private void setGpsNotOpenText(){
        SpannableString ssTitle = new SpannableString(getString(R.string.gps_no_open));
        if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 11, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 11, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        } else {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 51, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 51, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mNoPermissionTextViewOne.setText(ssTitle);
        mGoToSettingsTextView.setVisibility(View.VISIBLE);
    }


    private void setNoPermissionLayoutShow(){
        mNoPermissionLayout.setVisibility(View.VISIBLE);
        mHasPermissionLayout.setVisibility(View.GONE);
    }

    private void setHasPermissionLayoutShow(){
        mHasPermissionLayout.setVisibility(View.VISIBLE);
        mNoPermissionLayout.setVisibility(View.GONE);

    }

    @Override
    public void onPermissionGranted(int permissionCode) {
        if (!mHPlusPermission.isGPSOPen(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            setNoPermissionLayoutShow();
            setGpsNotOpenText();
        }
        else{
            showHasPermissionAndGpsOpen();
        }

    }

    @Override
    public void onPermissionNotGranted(String[] permission, int permissionCode) {
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M) {
            this.requestPermissions(permission, permissionCode);
        }
    }

    @Override
    public void onPermissionDenied(int permissionCode) {
        setNoPermissionLayoutShow();
        setNoPermissionText();
    }



}
