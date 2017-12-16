package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.WAPIRouter;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;
import com.honeywell.hch.airtouchv2.lib.util.WifiUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Enrollment Step 1 - SmartPhone find Air Touch's SSID.
 * Scanning surrounding every 1s.
 */
public class EnrollWelcomeActivity extends EnrollBaseActivity {

    private static final String TAG = "AirTouchEnrollWelcome";
    public static final String AIR_TOUCH_SSID = "AirTouch S";

    private ImageView loadingImageView;
//    private ImageView handImageView;

    private static ProgressDialog mDialog;
    private ArrayList<WAPIRouter> mWAPIRouters;
    private ListView mNetworkList;
    private NetworkListAdapter networkListAdapter;
    private WifiManager mWifi;
    private ArrayList<ScanResult> mScanResults = new ArrayList<>();
    private ScanResult mSelectedScanResult;
    private ConfigureState mConfigureState = ConfigureState.WIFI_OFF;
    private boolean isScanning = false;
    private int noticeCount = 0;

    private enum ConfigureState {
        WIFI_OFF, WIFI_ON, SCANNING, RESCAN, NO_STAT_NETWORKS,
        MULTIPLE_STAT_NETWORKS, STAT_NETWORK_SELECTED, CONNECTING, CONNECTED, CONFIGURED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_enrollwelcome);
        loadingImageView = (ImageView) findViewById(R.id.loading_image);
//        handImageView = (ImageView) findViewById(R.id.hand_iv);
        mNetworkList = (ListView) findViewById(R.id.wifi_list);
        mNetworkList.setOnItemClickListener(mScanResultClickListener);
        networkListAdapter = new NetworkListAdapter(EnrollWelcomeActivity.this);

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        isScanning = true;
        AnimationDrawable anim1 = (AnimationDrawable) loadingImageView.getBackground();
        anim1.start();
//        AnimationDrawable anim2 = (AnimationDrawable) handImageView.getBackground();
//        anim2.start();
        loadData();

        IntentFilter scanResultsFilter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mScanResultsReceiver, scanResultsFilter);
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
        unregisterReceiver(mScanResultsReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        isScanning = false;
        UmengUtil.onEvent(this, UmengUtil.EventType.ENROLL_CANCEL.toString(), "page1");

        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

        return false;
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

            List<ScanResult> scanResults = mWifi.getScanResults();
            mScanResults.clear();

            /*
             *   Smart phone is scanning Air Touch's SSID,
             *   if found one, connect to it and go to the next step.
             *   Ignore others similar SSID.
             */
            for (ScanResult scanResult : scanResults) {
                if (scanResult.SSID.contains(AIR_TOUCH_SSID)) {
                    mScanResults.add(scanResult);
//                    showToast("found " + scanResult.SSID);
//                    break;
                }
            }

            // If there is only one automatically connect to that scan result.
            if (mScanResults.size() == 1) {
                mSelectedScanResult = mScanResults.get(0);
                mConfigureState = ConfigureState.STAT_NETWORK_SELECTED;
            } else if (mScanResults.size() == 0) {
                noticeCount++;
                if (noticeCount == 5) {
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
    };

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
                mDialog = ProgressDialog.show(EnrollWelcomeActivity.this, null, getString(R.string.enroll_loading));
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
        // Check if we are already connected to the network given.
//        WifiInfo wifiInfo = mWifi.getConnectionInfo();
//        if (wifiInfo != null) {
//            String ssid = wifiInfo.getSSID();
//
//            if (!TextUtils.isEmpty(ssid)
//                    && wifiInfo.getSSID().contains(scanResult.SSID)) {
//                connectionCompleted();
//                return;
//            }
//        }

        mConfigureState = ConfigureState.CONNECTING;

        // Build WifiConfiguration object used to connect to with WifiManager
        WifiConfiguration wifiConfiguration = getWifiConfiguration(scanResult);

        // Add network and then attempt connection asynchronously
        int res = mWifi.addNetwork(wifiConfiguration);
        mWifi.enableNetwork(res, true);
        mWifi.reconnect();
        connectionCompleted();
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

}
