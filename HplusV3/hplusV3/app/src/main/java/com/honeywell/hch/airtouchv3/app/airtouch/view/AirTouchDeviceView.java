package com.honeywell.hch.airtouchv3.app.airtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

/**
 * Custom view for devices in house.
 * Created by nan.liu on 3/3/15.
 */
public class AirTouchDeviceView extends RelativeLayout {


    private TextView deviceStatusTextView;
    private ImageView deviceStatusImageView;
    private TextView deviceNameTextView;
    private TextView pm25TextView;
    private RelativeLayout deviceButtonLayout;
    private LongClick airTouchLongClick;
    private DeviceControlClick deviceControlClick;

    private Context mContext;
    private HomeDevice mHomeDevice;
    private int mResourceId;

    public AirTouchDeviceView(Context context) {
        super(context);
        mContext = context;

        initView(mContext);

    }

    public AirTouchDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initView(mContext);

    }

    public int getResourceId() {
        return mResourceId;
    }

    public void setResourceId(int resourceId) {
        this.mResourceId = resourceId;
    }

    public LongClick getAirTouchLongClick() {
        return airTouchLongClick;
    }

    public void setAirTouchLongClick(LongClick myLongClick) {
        this.airTouchLongClick = myLongClick;
    }

    public void setDeviceControlClick(DeviceControlClick deviceControlClick) {
        this.deviceControlClick = deviceControlClick;
    }

    public interface LongClick {
        void handle();
    }

    public interface DeviceControlClick {
        void onClick(HomeDevice homeDevice);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.device_inhouse, this);
        deviceStatusTextView = (TextView) findViewById(R.id.device_status_text);
        deviceStatusImageView = (ImageView) findViewById(R.id.device_status_image);
        deviceStatusImageView.setOnClickListener(startControlOnClick);
        deviceNameTextView = (TextView) findViewById(R.id.device_name);
        pm25TextView = (TextView) findViewById(R.id.pm25_value);
        deviceButtonLayout = (RelativeLayout) findViewById(R.id.device_button_layout);
        deviceButtonLayout.setOnClickListener(startControlOnClick);
        deviceButtonLayout.setOnLongClickListener(deleteDeviceLongClick);
    }

    public void updateView(HomeDevice homeDevice) {
        mHomeDevice = homeDevice;
        if (mHomeDevice == null || mHomeDevice.getDeviceInfo() == null ||
                ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus() == null){
            pm25TextView.setText(mContext.getResources().getString(R.string.not_get_pmvalue_tip));
            return;
        }
        deviceNameTextView.setText(mHomeDevice.getDeviceInfo().getName());
        if (((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getmPM25Value() > 0
                && ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getmPM25Value() < 999) {
            pm25TextView.setText("" + ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getmPM25Value());
        } else {
            ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().setmPM25Value(0);
            pm25TextView.setText(mContext.getResources().getString(R.string.not_get_pmvalue_tip));
        }
        setDeviceStatusImageViewValue();

    }


    private void setDeviceStatusImageViewValue(){

        Animation lowRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_low);
        Animation mediumRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_medium);
        Animation highRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_high);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        lowRotate.setInterpolator(linearInterpolator);
        mediumRotate.setInterpolator(linearInterpolator);
        highRotate.setInterpolator(linearInterpolator);

        // Auto, High, Low ...
        String status = "";

        if (!mHomeDevice.getDeviceInfo().getIsAlive()) {
            status = mContext.getString(R.string.offline);
            deviceStatusTextView.setText(status);
            deviceStatusImageView.clearAnimation();
            return;
        }

        if (((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus() != null) {
            status = ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getScenarioMode();
            if (status.equals("Manual")) {
                switch (((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getFanSpeedStatus()) {
                    case "Speed_1":
                    case "Speed_2":
                        status = mContext.getString(R.string.speed_low);
                        deviceStatusImageView.startAnimation(lowRotate);
                        break;
                    case "Speed_3":
                    case "Speed_4":
                    case "Speed_5":
                        status = mContext.getString(R.string.speed_medium);
                        deviceStatusImageView.startAnimation(mediumRotate);
                        break;
                    case "Speed_6":
                    case "Speed_7":
                        status = mContext.getString(R.string.speed_high);
                        deviceStatusImageView.startAnimation(highRotate);
                        break;
                    default:
                        break;
                }
            } else if (status.equals("Auto")) {
                status = mContext.getString(R.string.control_auto);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("Sleep")) {
                status = mContext.getString(R.string.control_sleep);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("QuickClean")) {
                status = mContext.getString(R.string.control_quick);
                deviceStatusImageView.startAnimation(highRotate);
            } else if (status.equals("Silent")) {
                status = mContext.getString(R.string.control_silent);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("Off")) {
                status = mContext.getString(R.string.off);
                deviceStatusImageView.clearAnimation();
            }
        }
        else
        {
            LogUtil.log(LogUtil.LogLevel.ERROR, "WorseDevice", "mHomeDevice.getDeviceRunStatus()" +
                    "" + " is null and there is no status value");
            return;
        }

        int pmValue = ((AirTouchSeriesDevice)mHomeDevice).getDeviceRunStatus().getmPM25Value();
        if (pmValue < AirTouchConstants.MAX_PMVALUE_LOW) {
            deviceButtonLayout.setBackgroundResource(R.drawable.device_pm25_low);
        } else if (pmValue < AirTouchConstants.MAX_PMVALUE_MIDDLE) {
            deviceButtonLayout.setBackgroundResource(R.drawable.device_pm25_middle);
        } else {
            deviceButtonLayout.setBackgroundResource(R.drawable.device_pm25_high);
        }
        deviceStatusTextView.setText(status);

        if (!mHomeDevice.getDeviceInfo().getIsAlive()) {
            status = mContext.getString(R.string.offline);
            deviceStatusTextView.setText(status);
            deviceStatusImageView.clearAnimation();
            return;
        }
    }

    View.OnClickListener startControlOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            deviceControlClick.onClick(mHomeDevice);
        }
    };

    OnLongClickListener deleteDeviceLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            AppManager.shareInstance().setCurrentDeviceId(mHomeDevice.getDeviceInfo().getDeviceID());
            airTouchLongClick.handle();

            return true;
        }
    };

}
