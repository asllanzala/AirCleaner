package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.os.Bundle;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollConnectDeviceBaseActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollDeviceManager;
import com.honeywell.hch.airtouchv3.lib.util.ByteUtil;

/**
 * Enrollment Step 2 - SmartPhone communicate to Air Touch.
 * 1) sendPhoneName - send phone's BUILD.NO to Air Touch.
 * 2) getWapiKey -  get Air Touch  key for password encrypt.
 * 3) getWapiDevice - get Air Touch  mac and crc.
 * <p/>
 * Ask user to input device name, city name and home name
 * Store these data to DIYInstallationState
 */
public class SmartlinkRegisterDeviceActivity extends EnrollConnectDeviceBaseActivity {

    private static final String TAG = "SmartlinkRegisterDeviceActivity";

    private String mDeviceMac = "144146000095";

    private String mDeviceMacCrc = "E6A3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_enrollconnectdevice);

        mContext = SmartlinkRegisterDeviceActivity.this;

        mEnrollDeviceManager = new EnrollDeviceManager(this, SmartlinkRegisterDeviceActivity.this);

        initWhenCreate();

    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonState();
    }


    @Override
    protected  void dealManagerFinishBackcall(){

        finishTheProcess();
    }



    @Override
    protected void clickStartConnectBtn(){

        String homeName = "";
        if (mHomeNameEditText.getEditorText().isEmpty()) {
            homeName = homeSpinnerTypeAdapter.getSelectedHome();
        } else {
            homeName = mHomeNameEditText.getEditorText();
        }

        mDeviceMac = SmartEnrollScanEntity.getEntityInstance().getmMacID();

        mDeviceMacCrc = ByteUtil.calculateCrc(mDeviceMac);
        mEnrollDeviceManager.setRegisteredInfo(mDeviceMac, mDeviceMacCrc, mDeviceNameEditText.getEditorText(),
                homeName, mSelectedGPSCity.getCode());

        mEnrollDeviceManager.startConnectServer();
    }
}
