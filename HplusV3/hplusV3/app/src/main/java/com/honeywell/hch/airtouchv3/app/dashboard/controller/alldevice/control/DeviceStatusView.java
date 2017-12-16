package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.control;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by allanhwmac on 15/10/20.
 */
public class DeviceStatusView extends LinearLayout {

    private View mParentLayout;

    private TextView mDeviceNamePoint;
    private TextView mDeviceNameTextView;
    private TextView mDeviceStatusTextView;

    private int mDeviceId;
    private String mDeviceName;
    private String mDeviceStatus;

    public DeviceStatusView(Context context) {
        super(context);

        initView();
    }

    public DeviceStatusView(Context context, int deviceId, String deviceName, String deviceStatus) {
        super(context);

        mDeviceId = deviceId;
        mDeviceName = deviceName;
        mDeviceStatus = deviceStatus;
        initView();
    }

    private void initView() {
        if (mParentLayout == null) {
            mParentLayout = LayoutInflater.from(getContext())
                    .inflate(R.layout.view_group_control_device_status, this);
        }

        mDeviceNamePoint = (TextView) mParentLayout.findViewById(R.id.group_control_group_name_point);
        mDeviceNameTextView = (TextView) mParentLayout.findViewById(R.id.group_control_group_name_view);
        mDeviceStatusTextView = (TextView) mParentLayout.findViewById(R.id.group_control_group_status_view);

        mDeviceNameTextView.setText(mDeviceName + ":");
        mDeviceStatusTextView.setText(mDeviceStatus);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DensityUtil.getScreenWidth() / 5;
        mDeviceNamePoint.setLayoutParams(params);
    }

    public int getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceStatus(String deviceStatus) {
        mDeviceStatus = deviceStatus;
        mDeviceStatusTextView.setText(deviceStatus);
    }

    public String getDeviceStatus() {
        return mDeviceStatus;
    }


    public TextView getDeviceStatusTextView() {
        return mDeviceStatusTextView;
    }

}