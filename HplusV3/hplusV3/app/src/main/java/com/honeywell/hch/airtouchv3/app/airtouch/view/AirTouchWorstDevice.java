package com.honeywell.hch.airtouchv3.app.airtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AirTouchView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Custom view for worst device info at home page.
 * Created by nan.liu on 3/3/15.
 */
public class AirTouchWorstDevice extends AirTouchView {

    private TextView mDeviceStatusTextView;
    private ImageView mDeviceStatusImageView;
    private TextView mDeviceNameTextView;
    private TextView mCleanTimeTextView;

    private Context mContext;
    private AirTouchSeriesDevice mHomeDevice;

    private TextView mPmTitleTextView;

    private TypeTextView mPmValue25TextView;

    private LinearLayout mAirtouchSLayout;

    private RelativeLayout mAirtouchPLayout;

    private RelativeLayout mTvocLayout;
    private TypeTextView mTvocValueTextView;

    private float mDefaultPosition = DensityUtil.getScreenWidth() - DensityUtil.dip2px(130);

    private WorstDeviceLine mWorstDeviceLine;

    private final int WHERE = AirTouchConstants.FROM_HOME_PAGE;

    public AirTouchWorstDevice(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public AirTouchWorstDevice(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.device_worst, this);
        mAirtouchSLayout = (LinearLayout)findViewById(R.id.horizontal_pm);
        mAirtouchPLayout = (RelativeLayout)findViewById(R.id.pm_layout);
        mTvocLayout = (RelativeLayout)findViewById(R.id.tvoc_layout);

        mTvocValueTextView = (TypeTextView)findViewById(R.id.tvoc_value);

        mDeviceStatusTextView = (TextView) findViewById(R.id.device_status_text);
        mDeviceStatusImageView = (ImageView) findViewById(R.id.device_status_image);
        mDeviceNameTextView = (TextView) findViewById(R.id.device_name);
        mCleanTimeTextView = (TextView) findViewById(R.id.clean_time_tv);
        ViewHelper.setTranslationX(mAirtouchPLayout, BitmapUtil.getImageWith(mContext.getResources(), R.drawable.fan));
        ViewHelper.setTranslationX(mAirtouchSLayout, BitmapUtil.getImageWith(mContext.getResources(), R.drawable.fan));

        mAirtouchSLayout.setVisibility(View.GONE);
        mAirtouchPLayout.setVisibility(View.GONE);
        mTvocLayout.setVisibility(View.GONE);

        mWorstDeviceLine = (WorstDeviceLine)findViewById(R.id.worst_line_id);
        mWorstDeviceLine.setPosition(mDefaultPosition);
    }


    public void updateView(AirTouchSeriesDevice homeDevice) {
        mHomeDevice = homeDevice;
        if (mHomeDevice == null || mHomeDevice.getDeviceInfo() == null || mHomeDevice.getDeviceRunStatus() == null) {
            return;
        }
        if (AppManager.shareInstance().isAirtouchs(mHomeDevice.getDeviceType())){
            mAirtouchSLayout.setVisibility(View.VISIBLE);
            mAirtouchPLayout.setVisibility(View.GONE);
            mTvocLayout.setVisibility(View.GONE);
            mPmValue25TextView = (TypeTextView) findViewById(R.id.pm25_value2);
        }
        else{
            mAirtouchSLayout.setVisibility(View.GONE);
            mAirtouchPLayout.setVisibility(View.VISIBLE);
            mTvocLayout.setVisibility(View.VISIBLE);
            mPmValue25TextView = (TypeTextView) findViewById(R.id.pm25_value);
            updateTvocValue(mContext, mHomeDevice, mTvocValueTextView, WHERE);
        }

        mDeviceNameTextView.setText(mHomeDevice.getDeviceInfo().getName());

        updateDeviceValue(mContext, mHomeDevice, mPmValue25TextView, WHERE);
        updateRunStatusAndFan(mContext, mHomeDevice, mDeviceStatusTextView, mDeviceStatusImageView);

    }

    public void updateCleanTime(String text) {
        mCleanTimeTextView.setText(text);
    }

    public void setDefaultPosition(float position){
        mDefaultPosition = position;
        mWorstDeviceLine.setPosition(mDefaultPosition);
    }

    public TypeTextView getmPm25TextView(){
        return mPmValue25TextView;
    }

    public TypeTextView getmTvocTextView(){
        return mTvocValueTextView;
    }
}
