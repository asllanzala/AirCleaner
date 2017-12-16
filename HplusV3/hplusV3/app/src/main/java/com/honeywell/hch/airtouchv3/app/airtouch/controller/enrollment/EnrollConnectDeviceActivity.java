package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Enrollment Step 2 - SmartPhone communicate to Air Touch.
 * 1) sendPhoneName - send phone's BUILD.NO to Air Touch.
 * 2) getWapiKey -  get Air Touch  key for password encrypt.
 * 3) getWapiDevice - get Air Touch  mac and crc.
 * <p/>
 * Ask user to input device name, city name and home name
 * Store these data to DIYInstallationState
 */
public class EnrollConnectDeviceActivity extends EnrollConnectDeviceBaseActivity {

    private static final String TAG = "AirTouchEnrollConnectDevice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_enrollconnectdevice);

        mContext = EnrollConnectDeviceActivity.this;
        mEnrollDeviceManager = new EnrollDeviceManager(this, EnrollConnectDeviceActivity.this);

        initWhenCreate();

    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonState();
    }

    @Override
    protected void onDestroy() {
        if (mEnrollDeviceManager == null)
            return;

        if (mEnrollDeviceManager.getScanResultsReceiver() != null
                && mEnrollDeviceManager.isRegistered()) {
            unregisterReceiver(mEnrollDeviceManager.getScanResultsReceiver());
        }
        super.onDestroy();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getSerializableExtra("city") == null)
            return;

        City city = (City) data.getSerializableExtra("city");
        switch (requestCode) {
            case SELECT_LOCATION_REQUEST:
                mSelectedGPSCity = city;
                cityNameTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ?
                        city.getNameZh() : city.getNameEn());
                cityNameTextView.setTextColor(getResources().getColor(R.color.black));
                getHomeName();
                cityNameTextView.requestFocus();
                mHomeNameEditText.clearFocus();
                break;
            default:
                break;
        }
    }

    protected  void dealManagerFinishBackcall(){
        if (!checkIfDeviceAlreadyEnrolled()) {
            if (mEnrollDeviceManager.isConnectionAttempted()) {
                Intent i = new Intent();
                i.setClass(EnrollConnectDeviceActivity.this, EnrollConnectWifiActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        }
    }

    @Override
    protected void clickStartConnectBtn() {
        saveUserdataForFinalEnroll();
        mEnrollDeviceManager.connectDevice();
    }

    private void saveUserdataForFinalEnroll() {
        DIYInstallationState.setCityCode(mSelectedGPSCity.getCode());
        DIYInstallationState.setDeviceName(mDeviceNameEditText.getEditorText());
        if (mHomeNameEditText.getEditorText().isEmpty()) {
            DIYInstallationState.setHomeName(homeSpinnerTypeAdapter.getSelectedHome());
        } else {
            DIYInstallationState.setHomeName(mHomeNameEditText.getEditorText());
        }

    }


    private MessageBox.MyOnClick continueEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            if (mEnrollDeviceManager.isConnectionAttempted()) {
                Intent i = new Intent();
                i.setClass(EnrollConnectDeviceActivity.this, EnrollConnectWifiActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        }
    };

    private Boolean checkIfDeviceAlreadyEnrolled() {
        String macId = "";
        DIYInstallationState.setIsDeviceAlreadyEnrolled(false);
        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
            macId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
        }
        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();
        if ((userLocations != null) && (mSelectedGPSCity != null)) {
            for (int i = 0; i < userLocations.size(); i++) {
                ArrayList<HomeDevice> homeDevicesList = userLocations.get(i).getHomeDevicesList();
                for (int j = 0; j < homeDevicesList.size(); j++) {
                    if (homeDevicesList.get(j).getDeviceInfo().getMacID().equalsIgnoreCase(macId)) {
                        DIYInstallationState.setIsDeviceAlreadyEnrolled(true);
                        MessageBox.createTwoButtonDialog(this, null,
                                getString(R.string.device_already_registered), getString(R.string.continue_str),continueEnroll,
                                getString(R.string.no), quitEnroll);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void isNeedReConnectWifi(){
        mEnrollDeviceManager.reconnectHomeWifi();
    }
}
